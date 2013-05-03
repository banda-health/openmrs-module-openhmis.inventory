package org.openmrs.module.openhmis.inventory.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.openhmis.inventory.api.IStockRoomDataService;
import org.openmrs.module.openhmis.inventory.api.IStockRoomService;
import org.openmrs.module.openhmis.inventory.api.IStockRoomTransactionDataService;
import org.openmrs.module.openhmis.inventory.api.IStockRoomTransactionTypeDataService;
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class StockRoomServiceImpl
		extends BaseOpenmrsService
		implements IStockRoomService {
	protected Log log = LogFactory.getLog(getClass());

	private IStockRoomDataService stockRoomService;
	private IStockRoomTransactionDataService transactionService;
	private IStockRoomTransactionTypeDataService transactionTypeService;

	@Autowired
	public StockRoomServiceImpl(IStockRoomDataService stockRoomService,
	                            IStockRoomTransactionDataService transactionService,
	                            IStockRoomTransactionTypeDataService transactionTypeService) {
		this.stockRoomService = stockRoomService;
		this.transactionService = transactionService;
		this.transactionTypeService = transactionTypeService;
	}

	@Override
	public List<StockRoom> getStockRooms() {
		return stockRoomService.getAll();
	}

	@Override
	public List<StockRoomTransactionType> getTransactionTypes() {
		return transactionTypeService.getAll();
	}

	@Override
	public List<StockRoomTransaction> getTransactions(StockRoom stockRoom) {
		return this.transactionService.getTransactionsByRoom(stockRoom, null);
	}

	@Override
	public StockRoomTransaction createTransaction(StockRoomTransactionType type, StockRoom source, StockRoom destination) {
		if (type == null) {
			throw new NullPointerException("The transaction type must be defined.");
		}

		StockRoomTransaction tx = new StockRoomTransaction();
		tx.setCreator(Context.getAuthenticatedUser());
		tx.setDateCreated(new Date());
		tx.setStatus(StockRoomTransactionStatus.PENDING);
		tx.setTransactionType(type);
		tx.setSource(source);
		tx.setDestination(destination);
		tx.setImportTransaction(source == null);

		validate(tx, false);

		return tx;
	}

	@Override
	public void submitTransaction(StockRoomTransaction transaction) throws APIException {
		/*
		This method:
			- Validates the transaction
			- Updates the source stock room quantity for all items
			- Sets the correct transaction item quantity
			- Saves the updated source stock room items
			- Saves the transaction to the database
		*/

		if (transaction == null) {
			throw new NullPointerException("The transaction to submit must be defined.");
		}

		validate(transaction, true);

		StockRoomTransactionType txType = transaction.getTransactionType();
		if (transaction.getSource() != null) {
			StockRoom source = transaction.getSource();

			// Make sure the source stock room items have been loaded
			source.getItems();

			for (StockRoomTransactionItem item : transaction.getItems()) {
				StockRoomItem stockRoomItem  = stockRoomService.getItem(source, item.getItem(), item.getExpiration());
				if (stockRoomItem == null) {
					throw new APIException("The stock room item '" + item.getItem().getName() +
							"' could not be found in the '" + source.getName() + "' stock room.");
				}

				// Remove the item quantity from the source stock room
				int qty = item.getQuantityOrdered();
				if (stockRoomItem.getQuantity() - qty > 0) {
					stockRoomItem.setQuantity(stockRoomItem.getQuantity() - qty);
				} else {
					// If this removes the last of the item from the stock room then remove the item from the stock room list
					source.removeItem(stockRoomItem);
				}

				// Update the transaction item quantity of the proper transaction quantity
				if (txType.getQuantityType() == PendingTransactionItemQuantityType.RESERVED) {
					item.setQuantityReserved(qty);
				} else {
					item.setQuantityTransferred(qty);
				}
			}

			// Save the changes to the source stock room
			stockRoomService.save(source);
		} else {
			// Update the transaction item quantity of the proper transaction quantity
			for (StockRoomTransactionItem item : transaction.getItems()) {
				if (txType.getQuantityType() == PendingTransactionItemQuantityType.RESERVED) {
					item.setQuantityReserved(item.getQuantityOrdered());
				} else {
					item.setQuantityTransferred(item.getQuantityOrdered());
				}
			}
		}

		// Update the transaction status
		transaction.setStatus(StockRoomTransactionStatus.PENDING);

		// Save the transaction
		transactionService.save(transaction);
	}

	@Override
	public void completeTransaction(StockRoomTransaction transaction) throws APIException {
		/*
		This method:
			- Validates the transaction
			- Adds the item quantity to the destination stock room
			- Removes the transaction item quantity
			- Updates the transaction status
			- Saves the updated destination stock room items
			- Saves the transaction
		*/

		if (transaction == null) {
			throw new NullPointerException("The transaction must be defined.");
		}

		validate(transaction, true);

		StockRoom destination = transaction.getDestination();
		if (destination != null) {
			// Make sure the destination stock room items have been loaded
			destination.getItems();

			for (StockRoomTransactionItem item : transaction.getItems()) {
				StockRoomItem stockRoomItem  = stockRoomService.getItem(destination, item.getItem(), item.getExpiration());
				if (stockRoomItem == null) {
					// Create item in destination stock room item list
					stockRoomItem = destination.addItem(item);
				} else {
					// Add the item quantity to the destination stock room
					stockRoomItem.setQuantity(stockRoomItem.getQuantity() + item.getQuantityOrdered());
				}

				// Remove the transaction item quantity
				item.setQuantityReserved(0);
				item.setQuantityTransferred(0);
			}

			// Save the changes to the source stock room
			stockRoomService.save(destination);
		} else {
			// Set each transaction item quantity to zero
			for (StockRoomTransactionItem item : transaction.getItems()) {
				item.setQuantityReserved(0);
				item.setQuantityTransferred(0);
			}
		}

		// Update the transaction status
		transaction.setStatus(StockRoomTransactionStatus.COMPLETED);

		// Save the transaction
		transactionService.save(transaction);
	}

	protected void validate(StockRoomTransaction transaction, boolean checkHasItems) throws APIException {
		if (transaction.getTransactionType() == null) {
			throw new APIException("The transaction type must be defined.");
		}

		if (transaction.getTransactionType().isSourceRequired() && transaction.getSource() == null) {
			throw new APIException("The specified transaction type " + transaction.getTransactionType().getName() +
				" requires a source stock room.");
		}

		if (transaction.getTransactionType().isDestinationRequired() && transaction.getDestination() == null) {
			throw new APIException("The specified transaction type " + transaction.getTransactionType().getName() +
					" requires a destination stock room.");
		}

		if (checkHasItems) {
			if (transaction.getItems() == null || transaction.getItems().size() == 0) {
				throw new APIException("The transaction must have at least one item associated with it.");
			}
		}
	}
}
