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

import org.apache.commons.lang.StringUtils;
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
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.IStockRoomDataService;
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch;
import org.openmrs.module.openhmis.inventory.api.security.BasicMetadataAuthorizationPrivileges;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class StockOperationDataServiceImpl
		extends BaseCustomizableMetadataDataServiceImpl<StockOperation>
		implements IStockOperationDataService {

	@Override
	protected BasicMetadataAuthorizationPrivileges getPrivileges() {
		return new BasicMetadataAuthorizationPrivileges();
	}

	@Override
	public StockOperation submitOperation(StockOperation operation) throws IllegalArgumentException, APIException {
		validate(operation);

		if (operation.getReserved() == null || operation.getReserved().size() <= 0) {
			throw new APIException("The operation must have at least one reserved transaction item defined.");
		}

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

		IStockRoomDataService stockroomService = Context.getService(IStockRoomDataService.class);

		for (StockOperationTransaction tx : transactions) {
			if (tx == null) {
				// Skip any null transactions
				continue;
			}

			// Get the stockroom for the tx
			StockRoom stockroom = tx.getStockRoom();

			// See if the item exists
			StockRoomItem stock = stockroomService.getItem(stockroom, tx.getItem(), tx.getExpiration());

			// Add or update the item stock
			if (stock == null) {
				// Add a new item stock record
				stock = new StockRoomItem();
				stock.setItem(tx.getItem());
				stock.setQuantity(tx.getQuantity());
				stock.setExpiration(tx.getExpiration());
				stock.setStockRoom(stockroom);

				stockroom.addItem(stock);
			} else if (stock.getQuantity() + tx.getQuantity() > 0) {
				// Update the item stock record
				stock.setQuantity(stock.getQuantity() + tx.getQuantity());
			} else {
				// Delete the item stock record because the quantity is now zero
				stockroom.removeItem(stock);
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
				DetachedCriteria subQuery = DetachedCriteria.forClass(IStockOperationType.class);
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
}
