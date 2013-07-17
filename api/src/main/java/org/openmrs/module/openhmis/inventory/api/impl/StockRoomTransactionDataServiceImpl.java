package org.openmrs.module.openhmis.inventory.api.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseCustomizableObjectDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IStockRoomTransactionDataService;
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.openmrs.module.openhmis.inventory.api.search.StockRoomTransactionSearch;
import org.openmrs.module.openhmis.inventory.api.security.TransactionAuthorizationPrivileges;

import java.util.List;
import java.util.Set;

public class StockRoomTransactionDataServiceImpl
		extends BaseCustomizableObjectDataServiceImpl<StockRoomTransaction, TransactionAuthorizationPrivileges>
		implements IStockRoomTransactionDataService {
	@Override
	protected TransactionAuthorizationPrivileges getPrivileges() {
		return new TransactionAuthorizationPrivileges();
	}

	@Override
	protected void validate(StockRoomTransaction transaction) throws APIException {
	}

	@Override
	public void purge(StockRoomTransaction transaction) throws APIException {
		// Ensure that the source and destination stock rooms don't hang onto a reference to this transaction
		if (transaction != null && transaction.getSource() != null) {
			transaction.getSource().removeTransaction(transaction);
		}
		if (transaction != null && transaction.getDestination() != null) {
			transaction.getDestination().removeTransaction(transaction);
		}

		super.purge(transaction);
	}

	@Override
	public StockRoomTransaction getTransactionByNumber(String transactionNumber) {
		if (StringUtils.isEmpty(transactionNumber)) {
			throw new IllegalArgumentException("The transaction number to find must be defined.");
		}
		if (transactionNumber.length() > 50) {
			throw new IllegalArgumentException("The transaction number must be less than 51 characters.");
		}

		Criteria criteria = repository.createCriteria(getEntityClass());
		criteria.add(Restrictions.eq("transactionNumber", transactionNumber));

		return repository.selectSingle(getEntityClass(), criteria);
	}

	@Override
	public List<StockRoomTransaction> getTransactionsByRoom(final StockRoom stockRoom, PagingInfo paging) {
		if (stockRoom == null) {
			throw new NullPointerException("The stock room must be defined.");
		}

		return executeCriteria(StockRoomTransaction.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.or(
						Restrictions.eq("source", stockRoom),
						Restrictions.eq("destination", stockRoom)
				));
			}
		});
	}

	@Override
	public List<StockRoomTransaction> getUserPendingTransactions(final User user, PagingInfo paging) {
		if (user == null) {
			throw new NullPointerException("The user must be defined.");
		}

		// Get all the roles for this user (this traverses the role relationships to get any parent roles)
		final Set<Role> roles = user.getAllRoles();

		return executeCriteria(StockRoomTransaction.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				// First find any transactions types that have attribute types which are for the specified user or role
				DetachedCriteria subQuery = DetachedCriteria.forClass(StockRoomTransactionType.class);
				subQuery.createAlias("attributeTypes", "at");
				subQuery.setProjection(Property.forName("id"));

				if (roles != null && roles.size() > 0) {
					subQuery.add(Restrictions.or(
						// Transaction types that require user approval
						Restrictions.eq("at.user", user),
						// Transaction types that require role approval
						Restrictions.in("at.role", roles)
					));
				} else {
					// Transaction types that require user approval
					subQuery.add(Restrictions.eq("at.user", user));
				}

				// Join the above criteria as a sub-query to the transaction transaction type
				criteria.add(Restrictions.and(
						Restrictions.eq("status", StockRoomTransactionStatus.PENDING),
						Restrictions.or(
								// Transactions created by the user
								Restrictions.eq("creator", user),
								Property.forName("transactionType").in(subQuery)
						)
				));
			}
		});
	}

	@Override
	public List<StockRoomTransaction> findTransactions(StockRoomTransactionSearch transactionSearch) {
		return findTransactions(transactionSearch, null);
	}

	@Override
	public List<StockRoomTransaction> findTransactions(final StockRoomTransactionSearch transactionSearch, PagingInfo paging) {
		if (transactionSearch == null) {
			throw new NullPointerException("The item search must be defined.");
		} else if (transactionSearch.getTemplate() == null) {
			throw new NullPointerException("The item search template must be defined.");
		}

		return executeCriteria(StockRoomTransaction.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				transactionSearch.updateCriteria(criteria);
			}
		});
	}
}
