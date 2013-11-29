/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.api.impl;

import com.google.common.collect.Iterators;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseCustomizableMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IItemStockDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.IStockRoomDataService;
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch;
import org.openmrs.module.openhmis.inventory.api.security.BasicMetadataAuthorizationPrivileges;

import java.util.*;

public class StockOperationDataServiceImpl
		extends BaseCustomizableMetadataDataServiceImpl<StockOperation>
		implements IStockOperationDataService {

	@Override
	protected BasicMetadataAuthorizationPrivileges getPrivileges() {
		return new BasicMetadataAuthorizationPrivileges();
	}

	/**
	 * Validates the stock operation.
	 * @param operation The stock operation to validate.
	 * @throws APIException
	 * @should throw an APIException if the type requires a source and the source is null
	 * @should throw an APIException if the type requires a destination and the destination is null
	 * @should throw an APIException if the type requires a patient and the patient is null
	 */
	@Override
	protected void validate(StockOperation operation) throws IllegalArgumentException, APIException {
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
			throw new APIException("The operation type (" + type.getName() + ") requires a source stockroom " +
				"but one has not been defined.");
		}
		if (type.getHasDestination() && operation.getDestination() == null) {
			throw new APIException("The operation type (" + type.getName() + ") requires a destination " +
					"stockroom but one has not been defined.");
		}
		if (type.getPatientRequired() && operation.getPatient() == null) {
			throw new APIException("The operation type (" + type.getName() + ") requires a patient " +
					"but one has not been associated.");
		}
	}

	@Override
	public StockOperation submitOperation(StockOperation operation) throws IllegalArgumentException, APIException {
		validate(operation);

		if (operation.getReserved() == null || operation.getReserved().size() <= 0) {
			throw new APIException("The operation must have at least one reserved transaction item defined.");
		}

		// TODO: Calculate any required expiration or batch operation qualifiers

		// TODO: Provide locking (on source stockroom uuid?) to ensure that concurrent operations don't screw things up

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
		}

		return save(operation);
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
		if (transactions == null || transactions.length == 0) {
			// Nothing to do
			return;
		} else if (transactions.length == 1 && transactions[0] == null) {
			// A single null parameter was passed in.  Nothing to do.
			return;
		}

		// TODO: Refactor method, it is too complex and long right now
		// TODO: Work properly with locking in submit stockroom

		// Note that we don't touch the stockroom operations, transactions, or item stock because that could result
		//  in loading a large number of records from the database that we don't need for this. This means that
		//  any existing stockroom objects must be refreshed before the data updated below will be seen.

		IStockRoomDataService stockroomService = Context.getService(IStockRoomDataService.class);
		IItemStockDataService itemStockService = Context.getService(IItemStockDataService.class);

		// Create a map to store the tx grouped by item and stockroom
		Map<Pair<Item, StockRoom>, List<StockOperationTransaction>> grouped = new HashMap<Pair<Item, StockRoom>, List<StockOperationTransaction>>();
		for (StockOperationTransaction tx : transactions) {
			if (tx == null) {
				continue;
			}

			Pair<Item, StockRoom> key = new ImmutablePair<Item, StockRoom>(tx.getItem(), tx.getStockRoom());
			if (!grouped.containsKey(key)) {
				grouped.put(key, new ArrayList<StockOperationTransaction>());
			}

			grouped.get(key).add(tx);
		}

		for (Pair<Item, StockRoom> key : grouped.keySet()) {
			Item item = key.getKey();
			StockRoom stockRoom = key.getValue();
			List<StockOperationTransaction> itemTxs = grouped.get(key);

			// Get the item stock from the stockroom
			ItemStock stock = stockroomService.getItem(stockRoom, item);

			// For each item transaction
			int totalQty = 0;
			for (StockOperationTransaction tx : itemTxs) {
				// Sum the total quantity for the item
				totalQty += tx.getQuantity();

				ItemStockDetail detail = null;
				if (stock == null) {
					// Item stock does not exist so create it and then create detail
					stock = new ItemStock();
					stock.setStockRoom(tx.getStockRoom());
					stock.setItem(tx.getItem());
					stock.setQuantity(0);

					detail = new ItemStockDetail(stock, tx);
					stock.addDetail(detail);
				} else {
					// The stock already exists so try and find the detail
					detail = findDetail(stock, tx);
					if (detail == null) {
						// Could not find the detail so create a new one
						detail = new ItemStockDetail(stock, tx);
						stock.addDetail(detail);
					} else {
						// Found the detail, just update the quantity
						detail.setQuantity(detail.getQuantity() + tx.getQuantity());
					}
				}

				// If the detail quantity is zero then remove the record. Note, details with quantities less than zero
				//      still need to be tracked.
				if(detail.getQuantity() == 0) {
					stock.getDetails().remove(detail);
				}
			}

			// Update the item quantity
			stock.setQuantity(stock.getQuantity() + totalQty);

			// Handle the special-case where the stock quantity is negative and ensure that there is only a single
			//  detail with no qualifiers and the negative quantity
			if (stock.getQuantity() < 0) {
				ItemStockDetail detail = null;
				if (stock.getDetails().size() > 1) {
					// Other detail records exist that should not be around anymore.  Clear them and create a single
					//  detail record for the unknown stock that has been removed from the stockroom.
					stock.getDetails().clear();

					detail = new ItemStockDetail();
					stock.addDetail(detail);
				} else {
					// Use this single record as the unqualified detail record
					detail = Iterators.get(stock.getDetails().iterator(), 0);
				}

				detail.setItemStock(stock);
				detail.setStockRoom(stock.getStockRoom());
				detail.setItem(stock.getItem());
				detail.setExpiration(null);
				detail.setBatchOperation(null);
				detail.setCalculatedExpiration(true);
				detail.setCalculatedBatch(true);
				detail.setQuantity(stock.getQuantity());
			}

			if (stock.getQuantity() == 0) {
				// Remove the stock if the quantity is zero
				itemStockService.purge(stock);
			} else {
				// Save the stock if the quantity is something other than zero (positive or negative)
				itemStockService.save(stock);
			}
		}
	}

	@Override
	public StockOperation getOperationByNumber(String number) {
		if (StringUtils.isEmpty(number)) {
			throw new IllegalArgumentException("The operation number to find must be defined.");
		}
		if (number.length() > 255) {
			throw new IllegalArgumentException("The operation number must be less than 256 characters.");
		}

		Criteria criteria = repository.createCriteria(getEntityClass());
		criteria.add(Restrictions.eq("operationNumber", number));

		return repository.selectSingle(getEntityClass(), criteria);
	}

	@Override
	public List<StockOperation> getOperationsByRoom(final StockRoom stockroom, PagingInfo paging) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined.");
		}

		return executeCriteria(StockOperation.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.or(
						Restrictions.eq("source", stockroom),
						Restrictions.eq("destination", stockroom)
				));
			}
		});
	}

	@Override
	public List<StockOperation> getUserOperations(User user, PagingInfo paging) {
		return getUserOperations(user, null, paging);
	}

	@Override
	public List<StockOperation> getUserOperations(final User user, final StockOperationStatus status, PagingInfo paging) {
		if (user == null) {
			throw new IllegalArgumentException("The user must be defined.");
		}

		// Get all the roles for this user (this traverses the role relationships to get any parent roles)
		final Set<Role> roles = user.getAllRoles();

		return executeCriteria(StockOperation.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				DetachedCriteria subQuery = DetachedCriteria.forClass(StockOperationTypeBase.class);
				subQuery.setProjection(Property.forName("id"));

				// Add user/role filter
				if (roles != null && roles.size() > 0) {
					subQuery.add(Restrictions.or(
							// Types that require user approval
							Restrictions.eq("user", user),
							// Types that require role approval
							Restrictions.in("role", roles)
					));
				} else {
					// Types that require user approval
					subQuery.add(Restrictions.eq("user", user));
				}

				if (status != null) {
					criteria.add(Restrictions.and(
							Restrictions.eq("status", status),
							Restrictions.or(
									// Transactions created by the user
									Restrictions.eq("creator", user),
									Property.forName("instanceType").in(subQuery)
							)
					));
				} else {
					criteria.add(Restrictions.or(
							// Transactions created by the user
							Restrictions.eq("creator", user),
							Property.forName("instanceType").in(subQuery)
					)
					);
				}
			}
		}, Order.desc("dateCreated")
		);
	}

	@Override
	public List<StockOperation> findOperations(StockOperationSearch search) {
		return findOperations(search, null);
	}

	@Override
	public List<StockOperation> findOperations(final StockOperationSearch search, PagingInfo paging) {
		if (search == null) {
			throw new IllegalArgumentException("The operation search must be defined.");
		} else if (search.getTemplate() == null) {
			throw new IllegalArgumentException("The operation search template must be defined.");
		}

		return executeCriteria(StockOperation.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				search.updateCriteria(criteria);
			}
		});
	}

	@Override
	public void purge(StockOperation operation) throws APIException {
		if (operation != null && (
				(operation.getReserved() != null && operation.getReserved().size() > 0) ||
				(operation.getTransactions() != null && operation.getTransactions().size() > 0))
			) {
			throw new APIException("Stock operations can not be deleted if there are any associated transactions.");
		}

		super.purge(operation);
	}

	private ItemStockDetail findDetail(ItemStock stock, StockOperationTransaction tx) {
		if (stock == null || stock.getDetails() == null || stock.getDetails().size() == 0) {
			return null;
		}

		// Loop through each detail record and find the first detail with the same expiration and batch operation
		for (ItemStockDetail detail : stock.getDetails()) {
			if  (
					(
						(detail.getExpiration() == null && tx.getExpiration() == null) || detail.getExpiration().equals(tx.getExpiration())
					) && (
						(detail.getBatchOperation() == null && tx.getBatchOperation() == null) ||
						(tx.getBatchOperation() != null && detail.getBatchOperation().getId().equals(tx.getBatchOperation().getId()))
					)
				){
				return detail;
			}
		}

		return null;
	}
}

