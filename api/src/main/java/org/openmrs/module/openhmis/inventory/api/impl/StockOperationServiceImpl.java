/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.api.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

import org.apache.commons.lang.ObjectUtils;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Seconds;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.openhmis.commons.api.Utility;
import org.openmrs.module.openhmis.inventory.ModuleSettings;
import org.openmrs.module.openhmis.inventory.api.IItemStockDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.model.IStockOperationType;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTransaction;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.api.model.TransactionBase;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;

/**
 * Provides {@link StockOperation} service implementations.
 */
public class StockOperationServiceImpl extends BaseOpenmrsService implements IStockOperationService {
	// This is the object that will provide synchronization
	private static final UUID OPERATION_LOCK = UUID.randomUUID();

	private IStockroomDataService stockroomService;
	private IItemStockDataService itemStockService;
	private IStockOperationDataService operationService;

	// These calendars are used as temporary variables when sorting operations
	private Calendar cal1 = Calendar.getInstance();
	private Calendar cal2 = Calendar.getInstance();

	@Autowired
	public StockOperationServiceImpl(IStockOperationDataService operationService, IStockroomDataService stockroomService,
	    IItemStockDataService itemStockService) {
		this.operationService = operationService;
		this.stockroomService = stockroomService;
		this.itemStockService = itemStockService;
	}

	/**
	 * Validates the stock operation.
	 * @param operation The stock operation to validate.
	 * @throws org.openmrs.api.APIException
	 * @should throw an APIException if the type requires a source and the source is null
	 * @should throw an APIException if the type requires a destination and the destination is null
	 * @should throw an APIException if the type requires a patient and the patient is null
	 */
	public static void validateOperation(StockOperation operation) {
		if (operation == null) {
			throw new IllegalArgumentException("The operation to submit must be defined.");
		}
		if (operation.getInstanceType() == null) {
			throw new APIException("The operation instance type must be defined.");
		}
		if (operation.getStatus() == null) {
			throw new APIException("The operation status must be defined.");
		}

		IStockOperationType type = operation.getInstanceType();
		if (type.getHasSource() && operation.getSource() == null) {
			throw new APIException("The operation type (" + type.getName() + ") requires a source stockroom "
			        + "but one has not been defined.");
		}
		if (type.getHasDestination() && operation.getDestination() == null) {
			throw new APIException("The operation type (" + type.getName() + ") requires a destination "
			        + "stockroom but one has not been defined.");
		}
		if (type.getRecipientRequired() && (operation.getPatient() == null && operation.getInstitution() == null)) {
			throw new APIException("The operation type (" + type.getName() + ") requires a patient or institution "
			        + "but one has not been associated.");
		}
	}

	public static void validateOperationItems(StockOperation operation) {
		if (operation.getItems() == null || operation.getItems().size() == 0) {
			return;
		}

		for (StockOperationItem item : operation.getItems()) {
			boolean allowNegativeItemQuantities = operation.getInstanceType().isNegativeItemQuantityAllowed();
			if (item.getQuantity() < 0 && !allowNegativeItemQuantities) {
				throw new APIException("This operation does not allow negative quantities for items.");
			}
		}

		if (operation.getInstanceType().getHasSource()) {
			return;
		}

		// Check operation items
		for (StockOperationItem item : operation.getItems()) {
			if (Boolean.TRUE.equals(item.getItem().getHasExpiration())) {
				if ((item.getExpiration() == null && !Boolean.TRUE.equals(item.getCalculatedExpiration()))) {
					throw new APIException("The item " + item.getItem().getName() + " requires an expiration.");
				} else if (operation.getSource() == null && item.getExpiration() == null) {
					throw new APIException("The expiration for item " + item.getItem().getName() + " must be defined.");
				}
			}
		}
	}

	@Override
	public StockOperation submitOperation(StockOperation operation) {
		return submitOperation(operation, true);
	}

	private StockOperation submitOperation(StockOperation operation, boolean validate) {
		/*
			Submitting the operation will copy the items to the operation reservations (if not already done) and then
			process those reservations based on the operation state.
		 */

		if (validate) {
			validateOperation(operation);
			validateOperationItems(operation);
			checkOperationDate(operation);
		}

		if (operation.getItems() == null || operation.getItems().size() <= 0) {
			throw new APIException("The operation must have at least one operation item defined.");
		}

		// Only allow access to a single caller at a time so that the reservation calculation does not get messed up
		synchronized (OPERATION_LOCK) {
			if (operation.getStatus() == StockOperationStatus.NEW) {
				// If this is a new operation, create the initial reservations based on the operation items
				for (StockOperationItem item : operation.getItems()) {
					ReservedTransaction tx = new ReservedTransaction(item);
					tx.setCreator(Context.getAuthenticatedUser());
					tx.setDateCreated(new Date());

					operation.addReserved(tx);
				}

				// Perform any required calculations to make the reservations valid
				calculateReservations(operation);

				operation.setStatus(StockOperationStatus.PENDING);
			}

			// Roll back any operations with an operation date after the specified operation
			if (operation.getStatus() == StockOperationStatus.COMPLETED
			        || operation.getStatus() == StockOperationStatus.CANCELLED) {
				rollbackFollowingOperations(operation);
			}

			// Trigger the appropriate status-based event so that the operation type can do what needs doing
			//  Note: applyTransactions will be called as part of the event, if needed
			switch (operation.getStatus()) {
				case PENDING:
					operation.getInstanceType().onPending(operation);
					break;
				case CANCELLED:
					operation.getInstanceType().onCancelled(operation);
					break;
				case COMPLETED:
					operation.getInstanceType().onCompleted(operation);
					break;
				default:
					break;
			}

			// Reapply any operations with an operation date after the specified operation
			if (operation.getStatus() == StockOperationStatus.COMPLETED
			        || operation.getStatus() == StockOperationStatus.CANCELLED) {
				reapplyFollowingOperations(operation);
			}

			// Save the operation and all sub-objects
			operation = operationService.save(operation);

			// Check to see if we should autocomplete the operation
			if (operation.getStatus() == StockOperationStatus.PENDING
			        && ModuleSettings.isOperationAutoCompleted()) {
				operation.setStatus(StockOperationStatus.COMPLETED);
				operation = submitOperation(operation, false);
			}

			return operation;
		}
	}

	@Override
	public StockOperation rollbackOperation(StockOperation operation) {
		if (operation == null) {
			throw new IllegalArgumentException("The operation to rollback must be defined.");
		}
		if (operation.getStatus() != StockOperationStatus.COMPLETED) {
			throw new APIException("Only completed operations can be rolled back.");
		}

		// Rollback any following operations
		rollbackFollowingOperations(operation);

		// Rollback the specified operation
		doOperationRollback(operation);

		// Now reapply the following operations
		reapplyFollowingOperations(operation);

		// Update the operation status
		operation.setStatus(StockOperationStatus.ROLLBACK);

		return operationService.save(operation);
	}

	@Override
	public void applyTransactions(Collection<StockOperationTransaction> transactions) {
		if (transactions != null && transactions.size() > 0) {
			StockOperationTransaction[] tx = new StockOperationTransaction[transactions.size() - 1];

			applyTransactions(transactions.toArray(tx));
		}
	}

	@Override
	public void applyTransactions(StockOperationTransaction... transactions) {
		// At a high level, this method analyses the specified transactions to create, update, and/or delete the
		//  appropriate item stock and item stock detail records for the appropriate stockroom

		if (transactions == null || transactions.length == 0) {
			// Nothing to do
			return;
		}

		if (transactions.length == 1 && transactions[0] == null) {
			// A single null parameter was passed in.  Nothing to do.
			return;
		}

		// Lock on the operation lock in case this method is called directly. If called via submitOperation this lock
		//  will already be acquired and simply reenter.
		synchronized (OPERATION_LOCK) {
			// Note that we don't touch the stockroom operations, transactions, or item stock lists because that could result
			//  in loading a large number of records from the database that we don't need for this. This means that
			//  any existing stockroom objects must be refreshed before the data updated below will be seen.

			// Create a map to store the tx grouped by item and stockroom
			Map<Pair<Item, Stockroom>, List<StockOperationTransaction>> grouped = createGroupedTransactions(transactions);
			for (Pair<Item, Stockroom> key : grouped.keySet()) {
				Item item = key.getValue0();
				Stockroom stockroom = key.getValue1();
				List<StockOperationTransaction> itemTxs = grouped.get(key);

				// Get the item stock from the stockroom
				ItemStock stock = stockroomService.getItem(stockroom, item);

				// For each item transaction
				int totalQty = 0;

				for (StockOperationTransaction tx : itemTxs) {
					// Sum the total quantity for this specific item
					totalQty += tx.getQuantity();

					ItemStockDetail detail = null;
					if (stock == null) {
						// Item stock does not exist so create it and then create detail
						stock = new ItemStock();
						stock.setStockroom(tx.getStockroom());
						stock.setItem(tx.getItem());
						stock.setQuantity(0);

						detail = new ItemStockDetail(stock, tx);
						stock.addDetail(detail);
						mergeNullBatchesToOnlyOne(stock);
					} else {
						// The stock already exists so try and find the detail
						detail = findDetail(stock, tx);
						if (detail == null) {
							// Could not find an appropriate detail so create a new one
							detail = new ItemStockDetail(stock, tx);
							stock.addDetail(detail);
							mergeNullBatchesToOnlyOne(stock);
						} else {
							// Found the detail, update the quantity
							long currentQuantity = detail.getQuantity();
							detail.setQuantity(detail.getQuantity() + tx.getQuantity());

							if (currentQuantity < 0 && detail.getQuantity() > 0) {
								// The quantity was previously negative and is now positive so inherit the batch and
								// expiration from the transaction
								detail.setCalculatedBatch(Boolean.TRUE.equals(tx.isCalculatedBatch()));
								detail.setBatchOperation(tx.getBatchOperation());
								detail.setCalculatedExpiration(Boolean.TRUE.equals(tx.isCalculatedExpiration()));
								detail.setExpiration(tx.getExpiration() == null ? null : (Date)tx.getExpiration().clone());
							}
							if (detail.getQuantity() < 0) {
								processNegativeStockDetail(stock, detail);
							}
						}
					}

					// If the detail quantity is zero then remove the record. Note, details with quantities less than zero
					//      still need to be tracked.
					if (detail.getQuantity() == 0) {
						stock.getDetails().remove(detail);
					}
				}

				// Update the item stock quantity with the total across all details for this specific item in the stockroom
				stock.setQuantity(stock.getQuantity() + totalQty);

				if (stock.getQuantity() == 0 && (!stock.hasDetails())) {
					// If the item stock quantity is exactly zero then we can safely delete the record

					// We have to remove the item stock from the stockroom item stock list even though this will load the
					// full list of items otherwise we may get a ObjectDeletedException when reapplying other operations
					stock.getStockroom().removeItem(stock);

					// Make sure the record is purged
					itemStockService.purge(stock);
				} else {
					// Save the stock if the quantity is something other than zero (positive or negative)
					itemStockService.save(stock);
				}
			}
		}
	}

	private void mergeNullBatchesToOnlyOne(ItemStock stock) {
		if (!stock.hasDetails()) {
			return;
		}
		List<ItemStockDetail> nullBatches = new ArrayList<ItemStockDetail>();
		for (ItemStockDetail detail : stock.getDetails()) {
			if (detail.isNullBatch()) {
				nullBatches.add(detail);
			}
		}
		if (nullBatches.size() > 1) {
			ItemStockDetail referenceBatch = nullBatches.get(0);
			for (int i = 1; i < nullBatches.size(); i++) {
				ItemStockDetail batchToMerge = nullBatches.get(i);
				Integer newQuantity = referenceBatch.getQuantity() + batchToMerge.getQuantity();
				referenceBatch.setQuantity(newQuantity);
				stock.removeDetail(batchToMerge);
			}
		}
	}

	/**
	 * THIS SHOULD NOT BE CALLED FROM USER CODE - Code to the interface (
	 * {@link org.openmrs.module.openhmis.inventory.api.IStockroomDataService}) not this class. Calculates the reservation
	 * details for the specified {@link org.openmrs.module.openhmis.inventory.api.model.StockOperation}. This includes
	 * calculating any qualifiers and checking on the details of the source stockroom to create all required transactions to
	 * fulfill the request.
	 * @param operation The stock operation for this transaction
	 * @should use closest expiration from the source stockroom
	 * @should use oldest batch operation with the calculated expiration
	 * @should set the expiration to null if no valid item stock can be found
	 * @should set the batch to null if no valid item stock can be found
	 * @should use date and time for expiration calculation
	 * @should create additional transactions when when multiple details are need to fulfill request
	 * @should create additional null qualifier transaction when there is not enough valid item stock to fulfill request
	 * @should copy source calculation settings into source calculation fields
	 * @should set the batch operation to the specified operation if there is no source stockroom
	 * @should combine transactions for the same item stock and qualifiers
	 * @should handle multiple transactions for the same item but with different qualifiers
	 * @should set the transaction source calculated flags if the source was calculated
	 * @should process non-calculated transactions before calculated transactions
	 * @should set batch operation to past operations before future operations
	 * @should support item change to have expiration after nonexpirable stock exists
	 * @should support item change to not have expiration after expirable stock exists
	 * @should throw IllegalArgumentException if operation is null
	 */
	public void calculateReservations(StockOperation operation) {
		if (operation == null) {
			throw new IllegalArgumentException("The operation must be defined");
		}

		/*
			We want to ensure that duplicated transactions are combined so they don't cause issues when they are processed.
			To do this, we loop through each transaction and build a tuple containing the Item, Expiration Date, and Batch
			Operation and look for it in a map.  If it is not found, we add it and continue to the next transaction. If it
			is found we update the existing transaction to add the quantity and set the calculated batch operation and
			expiration. The rule for the calculated qualifiers is that if either of the transactions (existing or current)
			was set to be calculated then the calculated field is set to true.
		 */
		List<ReservedTransaction> removeList = findDuplicateReservedTransactions(operation);
		for (ReservedTransaction tx : removeList) {
			operation.getReserved().remove(tx);
		}

		// Sort the transactions by item and then non-calculated versus calculated
		List<ReservedTransaction> transactions = sortReservedTransactions(operation);

		/*
			Now we need to check each transaction against the source stockroom item stock (if there is a source stockroom)
			and figure out exactly which specific item stock (called an item stock detail) to take.  This can result in
			new transactions being created if a transaction cannot be fulfilled by a single detail. The calculation
			also needs to take into account others transactions for the same item. To manage this, a Map is created to
			store copies of the item stock detail and then these copies are then updated so that a running tally can be kept
			of what is actually available when processing a specific transaction without modifying the actual detail records.
		 */
		Map<Pair<Stockroom, Item>, ItemStock> stockMap = new HashMap<Pair<Stockroom, Item>, ItemStock>();
		List<ReservedTransaction> newTransactions = new ArrayList<ReservedTransaction>();
		boolean hasSource = operation.getSource() != null;
		boolean isAdjustment = operation.isAdjustmentType();

		for (ReservedTransaction tx : transactions) {
			if (hasSource && (!isAdjustment || (isAdjustment && tx.getQuantity() < 0))) {
				// Clone the item stock and find the detail record
				ItemStock stock = findAndCloneStock(stockMap, operation.getSource(), tx.getItem());
				findAndUpdateSourceDetail(newTransactions, operation, stock, tx);
			} else {
				// Set the batch operation to the current operation because this must be some type of receipt operation
				if (tx.getBatchOperation() == null) {
					tx.setBatchOperation(operation);
					tx.setCalculatedBatch(false);
				}
			}
		}

		// Add any newly created transactions to the operation
		for (ReservedTransaction newTx : newTransactions) {
			operation.addReserved(newTx);
		}
	}

	private void rollbackFollowingOperations(StockOperation operation) {
		// Rolling back an operation reverses any operation transactions and deletes the reservation transactions for the
		// operation. Basically, it sets the operation and associated item stock and stockroom data back to before this
		// operation was performed.

		// Get operations that were created after the specified operation
		List<StockOperation> rollbackOperations = operationService.getFutureOperations(operation, null);

		// Sort the transactions in reverse order by operation date (most recent first)
		Collections.sort(rollbackOperations, new Comparator<StockOperation>() {
			@Override
			public int compare(StockOperation o1, StockOperation o2) {
				return compareOperationsByDateAndOrder(o1, o2) * -1;
			}
		});

		// // Rollback each operation, starting from the newest
		for (StockOperation rollbackOp : rollbackOperations) {
			if (rollbackOp.getStatus() != StockOperationStatus.ROLLBACK) {
				doOperationRollback(rollbackOp);
			}
		}
	}

	private void doOperationRollback(StockOperation operation) {
		// To undo the transaction we are merely going to negate the quantity and then reapply the transactions
		if (operation.getTransactions() != null) {
			Set<StockOperationTransaction> transactions = operation.getTransactions();
			for (StockOperationTransaction tx : transactions) {
				tx.setQuantity(tx.getQuantity() * -1);
			}

			applyTransactions(transactions);

			operation.getTransactions().clear();
		}

		// Now we can delete the transactions and pending transactions
		if (operation.getReserved() != null) {
			operation.getReserved().clear();
		}
	}

	private void reapplyFollowingOperations(StockOperation operation) {
		// Get operations that were created after the specified operation
		List<StockOperation> rollbackOperations = operationService.getFutureOperations(operation, null);

		// Sort the transactions in reverse order by operation date (most recent first)
		Collections.sort(rollbackOperations, new Comparator<StockOperation>() {
			@Override
			public int compare(StockOperation o1, StockOperation o2) {
				return compareOperationsByDateAndOrder(o1, o2);
			}
		});

		// Now reapply each operation, starting from the oldest
		for (StockOperation reapplyOp : rollbackOperations) {
			if (reapplyOp.getStatus() != StockOperationStatus.ROLLBACK) {
				// Ensure that the transactions have been cleared
				if (reapplyOp.getTransactions() != null) {
					reapplyOp.getTransactions().clear();
				}
				if (reapplyOp.getReserved() != null) {
					reapplyOp.getReserved().clear();
				}

				// Recreate the initial set of reserved transactions
				for (StockOperationItem item : reapplyOp.getItems()) {
					ReservedTransaction tx = new ReservedTransaction(item);
					tx.setCreator(Context.getAuthenticatedUser());
					tx.setDateCreated(new Date());

					reapplyOp.addReserved(tx);
				}

				// Now recalculate the reservations
				calculateReservations(reapplyOp);

				// Apply the pending transactions
				reapplyOp.getInstanceType().onPending(reapplyOp);

				// If the status is cancelled or completed then also apply those transactions as well
				if (reapplyOp.getStatus() == StockOperationStatus.CANCELLED) {
					reapplyOp.getInstanceType().onCancelled(reapplyOp);
				} else if (reapplyOp.getStatus() == StockOperationStatus.COMPLETED) {
					reapplyOp.getInstanceType().onCompleted(reapplyOp);
				}
			}
		}

		// No need to save because this that will happen in submitOperation
	}

	private List<ReservedTransaction> findDuplicateReservedTransactions(StockOperation operation) {
		Map<Triplet<Item, Date, StockOperation>, ReservedTransaction> map =
		        new HashMap<Triplet<Item, Date, StockOperation>, ReservedTransaction>();
		List<ReservedTransaction> removeList = new ArrayList<ReservedTransaction>();

		for (ReservedTransaction tx : operation.getReserved()) {
			Triplet<Item, Date, StockOperation> key =
			        Triplet.with(tx.getItem(), tx.getExpiration(), tx.getBatchOperation());
			if (!map.containsKey(key)) {
				map.put(key, tx);
			} else {
				// Update the existing tx with this tx
				ReservedTransaction existingTx = map.get(key);
				existingTx.setQuantity(existingTx.getQuantity() + tx.getQuantity());
				existingTx.setCalculatedBatch(existingTx.isCalculatedBatch() || tx.isCalculatedBatch());
				existingTx.setCalculatedExpiration(existingTx.isCalculatedExpiration() || tx.isCalculatedExpiration());

				removeList.add(tx);
			}
		}

		return removeList;
	}

	private List<ReservedTransaction> sortReservedTransactions(StockOperation operation) {
		List<ReservedTransaction> transactions = new ArrayList<ReservedTransaction>(operation.getReserved());

		Collections.sort(transactions, new Comparator<ReservedTransaction>() {
			@Override
			public int compare(ReservedTransaction tx1, ReservedTransaction tx2) {
				int result = 0;

				result = tx1.getItem().getId().compareTo(tx2.getItem().getId());
				if (result == 0) {
					result = Boolean.valueOf(tx1.isCalculatedExpiration()).compareTo(tx2.isCalculatedExpiration());

					if (result == 0) {
						result = Boolean.valueOf(tx1.isCalculatedBatch()).compareTo(tx2.isCalculatedBatch());

						if (result == 0) {
							if (tx1.getId() != null && tx2.getId() != null) {
								result = tx1.getId().compareTo(tx2.getId());
							} else {
								// Everything is the same and no ids so just return that tx1 is the first
								return -1;
							}

						}
					}
				}

				return result;
			}
		});

		return transactions;
	}

	private ItemStock findAndCloneStock(Map<Pair<Stockroom, Item>, ItemStock> workingMap, Stockroom stockroom, Item item) {
		Pair<Stockroom, Item> pair = Pair.with(stockroom, item);

		ItemStock stock = workingMap.get(pair);
		if (stock == null) {
			stock = stockroomService.getItem(stockroom, item);
			if (stock != null) {
				stock = new ItemStock(stock);

				workingMap.put(pair, stock);
			}
		}

		return stock;
	}

	private void findAndUpdateSourceDetail(List<ReservedTransaction> newTransactions, StockOperation operation,
	        ItemStock stock, ReservedTransaction tx) {
		ItemStockDetail detail = findSourceDetail(operation, stock, tx);

		if (detail == null) {
			// No existing stock could be found to fulfill the request

			if (ModuleSettings.isNegativeStockRestricted()) {
				// Negative item stock is restricted
				if (tx.getQuantity() > 0) {
					throw new APIException("Resource stockroom does not have sufficient stock.");
				}
			}

			tx.setSourceCalculatedExpiration(true);
			tx.setSourceCalculatedBatch(true);
			tx.setExpiration(null);
			tx.setBatchOperation(null);
		} else {
			// Set the tx fields that derive from the source detail
			tx.setSourceCalculatedExpiration(detail.isCalculatedExpiration());
			tx.setSourceCalculatedBatch(detail.isCalculatedBatch());
			tx.setExpiration(detail.getExpiration());
			tx.setBatchOperation(detail.getBatchOperation());

			// get the cumulative total quantity for item stocks with the same expiration date.
			int cumulativeQuantity = calculateTotalItemStockQuantity(stock, detail);
			int remainingQuantity = 0;

			if (cumulativeQuantity == 0) {
				// DO NOT use this item stock or any other with the same expiration date.
				deleteItemStockDetailRecords(stock, detail);
				// Find other details to fulfill this tx
				findAndUpdateSourceDetail(newTransactions, operation, stock, tx);
			} else {
				if ((cumulativeQuantity + tx.getQuantity()) < 0) {
					if (cumulativeQuantity > 0) {
						// UPDATE the current transaction quantity with the cumulative quantity
						// and create another transaction (after processing the current transaction)
						// with the remainingQuantity.
						// FOR INSTANCE: if the cumulativeQuantity = 5, transaction quantity = -6,
						// set transaction quantity to -5,
						// and create another transaction with quantity = -1
						remainingQuantity = cumulativeQuantity + tx.getQuantity();
						tx.setQuantity(cumulativeQuantity * -1);
					}
				}

				if (detail.getQuantity() < 0) {
					// The detail quantity is already negative so just subtract more
					detail.setQuantity(detail.getQuantity() - tx.getQuantity());
				} else {
					// Subtract the tx quantity from the detail and ensure that it has enough to fulfill the request
					detail.setQuantity(detail.getQuantity() - Math.abs(tx.getQuantity()));

					if (detail.getQuantity() == 0) {
						// If the quantity is exactly zero than we can simply remove the detail record
						stock.getDetails().remove(detail);
					} else if (detail.getQuantity() < 0) {
						stock.getDetails().remove(detail);

						// Set the tx quantity to the number actually deduced from the detail
						//Math.abs is needed to handle negative adjustments correctly
						tx.setQuantity(Math.abs(tx.getQuantity()) + detail.getQuantity());

						//if adjustment make sure that the quantity is negative (this method is only dealing with
						// negative adjustments)
						if (operation.isAdjustmentType()) {
							tx.setQuantity(tx.getQuantity() * -1);
						}

						remainingQuantity +=
						        operation.isAdjustmentType() ? detail.getQuantity() : Math.abs(detail.getQuantity());
					}
				}
			}

			// Create a new tx to handle the remaining stock request
			if (remainingQuantity != 0) {
				ReservedTransaction newTx = new ReservedTransaction(tx);
				newTx.setQuantity(remainingQuantity);

				// Add the new tx to the list of transactions to add to the operations
				newTransactions.add(newTx);

				// Find the details to fulfill this new tx
				findAndUpdateSourceDetail(newTransactions, operation, stock, newTx);
			}
		}
	}

	/**
	 * Calculates the total quantities for item stocks with the same expiration date.
	 * @param stock
	 * @param detail
	 * @return
	 */
	private int calculateTotalItemStockQuantity(ItemStock stock, ItemStockDetail detail) {
		int cumulativeQuantity = 0;
		for (ItemStockDetail stockDetail : stock.getDetails()) {
			Date stockDetailExp = stockDetail.getExpiration();
			Date detailExp = detail.getExpiration();
			if (stockDetailExp == null && detailExp == null) {
				cumulativeQuantity += stockDetail.getQuantity();
			} else if (stockDetailExp != null && detailExp != null) {
				if (stockDetailExp.getTime() == detail.getExpiration().getTime()) {
					cumulativeQuantity += stockDetail.getQuantity();
				}
			}
		}
		return cumulativeQuantity;
	}

	/**
	 * Deletes item stocks with given expiration date.
	 * @param stock
	 * @param detail
	 */
	private void deleteItemStockDetailRecords(ItemStock stock, ItemStockDetail detail) {
		List<ItemStockDetail> deleteStockDetails = findDetailByExpiration(stock, detail.getExpiration());

		if (deleteStockDetails.size() > 0) {
			for (ItemStockDetail deleteDetail : deleteStockDetails) {
				stock.getDetails().remove(deleteDetail);
			}
		}
	}

	private ItemStockDetail findSourceDetail(StockOperation operation, ItemStock stock, ReservedTransaction tx) {
		// This method finds the item stock detail to satisfy the reservation

		if (stock == null) {
			return null;
		}

		ItemStockDetail detail = null;

		/* The following scenarios must be considered:
			The tx has a specific exp and batch so find that specific detail record
			The tx has a calculated exp
				OR/AND
			The tx has a calculated batch

			Also, we need to handle items that have changed whether they have an expiration or not. Because operations can
			 be reapplied at a later date, the search needs to be flexible enough to work with item stock that has some
			 details with an expiration and some without.

			Previous	Current
			----------------------------------------
			No Exp		Exp	(Calc)	-> Return No-Exp
			No Exp		Exp			-> Return Null (Create new negative detail)
			Exp			No Exp		-> Return Exp
			None		No Exp		-> Return Null
			None		Exp			-> Return Null
		*/

		List<ItemStockDetail> results = null;
		if (Boolean.TRUE.equals(tx.isCalculatedExpiration()) && Boolean.TRUE.equals(tx.isCalculatedBatch())) {
			// Find the detail that will expire the soonest/ furthest (could be multiple, each with a different batch op)
			results =
			        findDetailByClosestOrFurthestExpiration(ModuleSettings.autoSelectItemStockWithFurthestExpirationDate(),
			            stock.getDetails(), new DateTime(operation.getOperationDate()));

			if (results == null || results.size() == 0) {
				detail = null;
			} else if (results.size() == 1) {
				detail = results.get(0);
			} else if (results.size() > 1) {
				detail = findOldestBatch(operation, results);
			}
		} else if (Boolean.TRUE.equals(tx.isCalculatedExpiration())) {
			// Find the detail with the specific batch and pick the best expiration if there are multiple
			results = findDetailByBatch(stock, tx.getBatchOperation());
			results =
			        findDetailByClosestOrFurthestExpiration(ModuleSettings.autoSelectItemStockWithFurthestExpirationDate(),
			            results, new DateTime(operation.getOperationDate()));

			detail = results.size() == 0 ? null : results.get(0);
		} else if (Boolean.TRUE.equals(tx.isCalculatedBatch())) {
			// Find the detail with the specific exp and the best batch if there are multiple
			results = findDetailByExpiration(stock, tx.getExpiration());
			detail = findOldestBatch(operation, results);
		} else {
			// Find the detail with the specific exp and specific batch
			detail = findDetail(stock, tx);
		}

		return detail;
	}

	private ItemStockDetail findDetail(ItemStock stock, TransactionBase tx) {
		if (stock == null || stock.getDetails() == null || stock.getDetails().size() == 0) {
			return null;
		}

		// Check if there is only a single detail with a negative quantity
		if (stock.getDetails().size() == 1) {
			ItemStockDetail detail = Iterators.getOnlyElement(stock.getDetails().iterator());
			if (detail.getQuantity() < 0) {
				// This detail can be used for all transactions, regardless of batch and expiration
				return detail;
			}
		}

		// Loop through each detail record and find the first detail with the same expiration and batch operation, matching
		// nulls with nulls
		for (ItemStockDetail detail : stock.getDetails()) {
			if (ObjectUtils.equals(detail.getExpiration(), tx.getExpiration())
			        && ObjectUtils.equals(detail.getBatchOperation(), tx.getBatchOperation())) {
				return detail;
			}
		}

		return null;
	}

	private List<ItemStockDetail> findDetailByExpiration(ItemStock stock, final Date date) {
		if (stock == null || stock.getDetails() == null || stock.getDetails().size() == 0) {
			return null;
		}

		List<ItemStockDetail> results = new ArrayList<ItemStockDetail>();
		results.addAll(Collections2.filter(stock.getDetails(), new Predicate<ItemStockDetail>() {
			@Override
			public boolean apply(ItemStockDetail detail) {
				return (detail.getExpiration() == null && date == null)
				        || (detail.getExpiration() != null && date != null && detail.getExpiration().compareTo(date) == 0);
			}
		}));

		return results;
	}

	private List<ItemStockDetail> findDetailByBatch(ItemStock stock, final StockOperation batchOperation) {
		if (stock == null || stock.getDetails() == null || stock.getDetails().size() == 0) {
			return null;
		}

		List<ItemStockDetail> results = new ArrayList<ItemStockDetail>();
		results.addAll(Collections2.filter(stock.getDetails(), new Predicate<ItemStockDetail>() {
			@Override
			public boolean apply(ItemStockDetail detail) {
				return (detail.getBatchOperation() == null && batchOperation == null)
				        || (detail.getBatchOperation() != null && detail.getBatchOperation() == batchOperation);
			}
		}));

		return results;
	}

	private List<ItemStockDetail> findDetailByClosestOrFurthestExpiration(
	        boolean furthestExpirationDate, Collection<ItemStockDetail> details, DateTime date) {
		if (details == null || details.size() == 0) {
			return null;
		}

		List<ItemStockDetail> results = new ArrayList<ItemStockDetail>();

		if (details.size() == 1) {
			// If there is only a single detail record then we can just use that
			results.addAll(details);
		} else {
			long range = 0;
			for (ItemStockDetail detail : details) {
				long temp;
				if (detail.getExpiration() == null) {
					temp = Long.MAX_VALUE;
				} else {
					temp = new Duration(date, new DateTime(detail.getExpiration())).getStandardSeconds();
				}

				if (results.size() == 0) {
					results.add(detail);
					range = temp;
				} else {
					if (temp == range) {
						results.add(detail);
					} else if ((temp < range && !furthestExpirationDate) || (temp > range && furthestExpirationDate)) {
						results.clear();
						results.add(detail);

						range = temp;
					}
				}
			}
		}

		return results;
	}

	private ItemStockDetail findOldestBatch(StockOperation operation, Collection<ItemStockDetail> details) {
		if (details == null || details.size() == 0) {
			return null;
		} else if (details.size() == 1) {
			return Iterators.getOnlyElement(details.iterator());
		}

		final DateTime operationTime = new DateTime(operation.getOperationDate());

		return Collections.min(details, new Comparator<ItemStockDetail>() {
			@Override
			public int compare(ItemStockDetail o1, ItemStockDetail o2) {
				DateTime o1Time =
				        o1.getBatchOperation() == null ? operationTime : new DateTime(o1.getBatchOperation()
				                .getOperationDate());
				DateTime o2Time =
				        o2.getBatchOperation() == null ? operationTime : new DateTime(o2.getBatchOperation()
				                .getOperationDate());

				return ((Integer)Seconds.secondsBetween(operationTime, o1Time).getSeconds()).compareTo(Seconds
				        .secondsBetween(operationTime, o2Time).getSeconds());
			}
		});
	}

	private Map<Pair<Item, Stockroom>, List<StockOperationTransaction>> createGroupedTransactions(
	        StockOperationTransaction[] transactions) {
		Map<Pair<Item, Stockroom>, List<StockOperationTransaction>> grouped =
		        new HashMap<Pair<Item, Stockroom>, List<StockOperationTransaction>>();
		for (StockOperationTransaction tx : transactions) {
			if (tx == null) {
				continue;
			}

			Pair<Item, Stockroom> key = Pair.with(tx.getItem(), tx.getStockroom());
			if (!grouped.containsKey(key)) {
				grouped.put(key, new ArrayList<StockOperationTransaction>());
			}

			grouped.get(key).add(tx);
		}
		return grouped;
	}

	private void processNegativeStockDetail(ItemStock stock, ItemStockDetail detail) {
		ItemStockDetail nullBatchNullExpirationItemStockDetail = findNullBatch(stock);
		if (detail.isNullBatch()) {
			//deduction has already taken place in applyTransactions method and there is no obsolete detail to delete
			return;
		}
		if (nullBatchNullExpirationItemStockDetail != null) {
			// there is an itemStockDetail without batch and expiration already so just further reduce the quantity
			Integer nullBatchQuantity = nullBatchNullExpirationItemStockDetail.getQuantity();
			Integer newQuantity = nullBatchQuantity + detail.getQuantity();
			nullBatchNullExpirationItemStockDetail.setQuantity(newQuantity);
		} else {
			//no such detail yet - create one
			ItemStockDetail newDetail = new ItemStockDetail();
			newDetail.setItemStock(stock);
			newDetail.setStockroom(stock.getStockroom());
			newDetail.setItem(stock.getItem());
			newDetail.setExpiration(null);
			newDetail.setBatchOperation(null);
			newDetail.setCalculatedExpiration(true);
			newDetail.setCalculatedBatch(true);
			newDetail.setQuantity(detail.getQuantity());
			stock.addDetail(newDetail);
		}

		//delete the "old" detail that is responsible for reduction if this is not a nullBatch as well
		if (!detail.isNullBatch()) {
			stock.removeDetail(detail);
		}

	}

	private ItemStockDetail findNullBatch(ItemStock stock) {
		ItemStockDetail nullBatch = null;
		if (stock.getDetails() != null && stock.getDetails().size() > 0) {
			for (ItemStockDetail detail : stock.getDetails()) {
				if (detail.isNullBatch()) {
					nullBatch = detail;
				}
			}
		}
		return nullBatch;
	}

	private int compareOperationsByDateAndOrder(StockOperation o1, StockOperation o2) {
		cal1.setTime(o1.getOperationDate());
		Utility.clearCalendarTime(cal1);

		cal2.setTime(o2.getOperationDate());
		Utility.clearCalendarTime(cal2);

		int result = cal1.compareTo(cal2);
		if (result == 0) {
			result = o1.getOperationOrder().compareTo(o2.getOperationOrder());
		}

		return result;
	}

	private void checkOperationDate(StockOperation operation) {
		// Ensure that the operation date and order are properly set
		if (operation.getStatus() == StockOperationStatus.NEW || operation.getStatus() == StockOperationStatus.PENDING) {
			if (operation.getOperationDate() == null) {
				operation.setOperationDate(new Date());
			}

			if (operation.getOperationOrder() == null) {
				// Get the last operation for the operation day
				StockOperation lastOp = operationService.getLastOperationByDate(operation.getOperationDate());

				if (lastOp == null || lastOp.getOperationOrder() == null) {
					operation.setOperationOrder(0);
				} else {
					operation.setOperationOrder(lastOp.getOperationOrder() + 1);
				}
			} else {
				// The operation order has been explicitly set. Ensure that any subsequent operations on the same day have
				//	their operation order incremented
				List<StockOperation> operations = operationService.getOperationsByDate(operation.getOperationDate(), null);

				for (StockOperation op : operations) {
					if (op != operation && op.getOperationOrder() >= operation.getOperationOrder()) {
						op.setOperationOrder(op.getOperationOrder() + 1);

						operationService.save(op);
					}
				}
			}
		}
	}
}
