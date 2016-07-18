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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.CustomizedOrderBy;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.Utility;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseCustomizableMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.model.IStockOperationType;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch;
import org.openmrs.module.openhmis.inventory.api.security.BasicMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.util.HibernateCriteriaConstants;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data service implementation class for {@link StockOperation}.
 */
public class StockOperationDataServiceImpl extends BaseCustomizableMetadataDataServiceImpl<StockOperation>
        implements IStockOperationDataService {
	private static final int MAX_OPERATION_NUMBER_LENGTH = 255;

	@Override
	protected BasicMetadataAuthorizationPrivileges getPrivileges() {
		return new BasicMetadataAuthorizationPrivileges();
	}

	@Override
	protected void validate(StockOperation operation) {
		StockOperationServiceImpl.validateOperation(operation);
	}

	@Override
	protected Order[] getDefaultSort() {
		// Return operations ordered by creation date, desc
		return new Order[] { Order.desc(HibernateCriteriaConstants.DATE_CREATED) };
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	public StockOperation getOperationByNumber(String number) {
		if (StringUtils.isEmpty(number)) {
			throw new IllegalArgumentException("The operation number to find must be defined.");
		}
		if (number.length() > MAX_OPERATION_NUMBER_LENGTH) {
			throw new IllegalArgumentException("The operation number must be less than 256 characters.");
		}

		Criteria criteria = getRepository().createCriteria(getEntityClass());
		criteria.add(Restrictions.eq(HibernateCriteriaConstants.OPERATION_NUMBER, number));

		return getRepository().selectSingle(getEntityClass(), criteria);
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS, PrivilegeConstants.VIEW_STOCKROOMS })
	public List<StockOperation> getOperationsByRoom(final Stockroom stockroom, PagingInfo paging) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined.");
		}

		return executeCriteria(StockOperation.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.or(Restrictions.eq(HibernateCriteriaConstants.SOURCE, stockroom),
				    Restrictions.eq(HibernateCriteriaConstants.DESTINATION, stockroom)));
			}
		}, getDefaultSort());
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	public List<StockOperationItem> getItemsByOperation(final StockOperation operation, PagingInfo paging) {
		if (operation == null) {
			throw new IllegalArgumentException("The operation must be defined.");
		}

		return executeCriteria(StockOperationItem.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq(HibernateCriteriaConstants.OPERATION, operation));
				criteria.createCriteria(HibernateCriteriaConstants.ITEM, "i");
			}
		}, Order.asc("i.name"));
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	public List<StockOperation> getUserOperations(User user, PagingInfo paging) {
		return getUserOperations(user, null, null, null, null, paging);
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	public List<StockOperation> getUserOperations(final User user, final StockOperationStatus status,
	        final IStockOperationType stockOperationType, final Item item, final Stockroom stockroom, PagingInfo paging) {
		if (user == null) {
			throw new IllegalArgumentException("The user must be defined.");
		}

		// Get all the roles for this user (this traverses the role relationships to get any parent roles)
		final Set<Role> roles = user.getAllRoles();

		return executeCriteria(StockOperation.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				DetachedCriteria subQuery = DetachedCriteria.forClass(IStockOperationType.class);
				subQuery.setProjection(Property.forName(HibernateCriteriaConstants.ID));

				// Add user/role filter
				if (roles != null && roles.size() > 0) {
					subQuery.add(Restrictions.or(
					    // Types that require user approval
					    Restrictions.eq(HibernateCriteriaConstants.USER, user),
					    // Types that require role approval
					    Restrictions.in(HibernateCriteriaConstants.ROLE, roles)));
				} else {
					// Types that require user approval
					subQuery.add(Restrictions.eq(HibernateCriteriaConstants.USER, user));
				}
				if (status != null || stockOperationType != null || item != null || stockroom != null) {
					if (item != null) {
						criteria.createAlias("items", "items").add(
						    Restrictions.and(
						        Restrictions.eq("items.item", item),
						        Restrictions.or(
						            // Operations created by the user
						            Restrictions.eq(HibernateCriteriaConstants.CREATOR, user),
						            Property.forName(HibernateCriteriaConstants.INSTANCE_TYPE).in(subQuery))));
					}
					if (status != null) {
						criteria.add(Restrictions.and(
						    Restrictions.eq(HibernateCriteriaConstants.STATUS, status),
						    Restrictions.or(
						        // Operations created by the user
						        Restrictions.eq(HibernateCriteriaConstants.CREATOR, user),
						        Property.forName(HibernateCriteriaConstants.INSTANCE_TYPE).in(subQuery))));
					}
					if (stockOperationType != null) {
						criteria.add(Restrictions.and(
						    Restrictions.eq(HibernateCriteriaConstants.INSTANCE_TYPE, stockOperationType),
						    Restrictions.or(
						        // Operations created by the user
						        Restrictions.eq(HibernateCriteriaConstants.CREATOR, user),
						        Property.forName(HibernateCriteriaConstants.INSTANCE_TYPE).in(subQuery))));
					}
					if (stockroom != null) {
						criteria.add(Restrictions.and(
						    Restrictions.or(Restrictions.eq(HibernateCriteriaConstants.SOURCE, stockroom),
						        Restrictions.eq(HibernateCriteriaConstants.DESTINATION, stockroom)),
						    Restrictions.or(
						        // Operations created by the user
						        Restrictions.eq(HibernateCriteriaConstants.CREATOR, user),
						        Property.forName(HibernateCriteriaConstants.INSTANCE_TYPE).in(subQuery))));
					}
				} else {
					criteria.add(Restrictions.or(
					    // Operations created by the user
					    Restrictions.eq(HibernateCriteriaConstants.CREATOR, user),
					    Property.forName(HibernateCriteriaConstants.INSTANCE_TYPE).in(subQuery)));
				}
			}
		}, Order.desc(HibernateCriteriaConstants.DATE_CREATED));
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	public List<StockOperation> getOperations(StockOperationSearch search) {
		return getOperations(search, null);
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	public List<StockOperation> getOperations(final StockOperationSearch search, PagingInfo paging) {
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
		}, getDefaultSort());
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	public List<StockOperation> getOperationsSince(final Date operationDate, PagingInfo paging) {
		if (operationDate == null) {
			throw new IllegalArgumentException("The operation date must be defined.");
		}

		return executeCriteria(StockOperation.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.gt(HibernateCriteriaConstants.OPERATION_DATE, operationDate));
			}
		}, Order.asc(HibernateCriteriaConstants.OPERATION_DATE));
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	public List<StockOperation> getFutureOperations(final StockOperation operation, PagingInfo paging) {
		if (operation == null) {
			throw new IllegalArgumentException("The operation must be defined.");
		}

		return executeCriteria(StockOperation.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.or(
				    Restrictions.and(createDateRestriction(operation.getOperationDate()),
				        Restrictions.gt(HibernateCriteriaConstants.OPERATION_ORDER, operation.getOperationOrder())),
				    Restrictions.gt(HibernateCriteriaConstants.OPERATION_DATE, operation.getOperationDate())));
				// Note that this ordering may not support all databases
			}
		}, CustomizedOrderBy.asc("convert(operation_date, date)"), Order.asc("operationOrder"),
		    Order.asc(HibernateCriteriaConstants.OPERATION_DATE));
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	public List<StockOperation> getOperationsByDate(final Date date, PagingInfo paging) {
		return getOperationsByDate(date, paging, null, Order.asc(HibernateCriteriaConstants.OPERATION_ORDER),
		    Order.asc(HibernateCriteriaConstants.OPERATION_DATE));
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	public StockOperation getLastOperationByDate(final Date date) {
		List<StockOperation> results =
		        getOperationsByDate(date, null, 1, Order.desc(HibernateCriteriaConstants.OPERATION_ORDER),
		            Order.desc(HibernateCriteriaConstants.DATE_CREATED));

		if (results == null || results.size() == 0) {
			return null;
		} else {
			return results.get(0);
		}
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	public StockOperation getFirstOperationByDate(final Date date) {
		List<StockOperation> results =
		        getOperationsByDate(date, null, 1, Order.asc("operationOrder"),
		            Order.asc(HibernateCriteriaConstants.DATE_CREATED));

		if (results == null || results.size() == 0) {
			return null;
		} else {
			return results.get(0);
		}
	}

	private List<StockOperation> getOperationsByDate(final Date date, PagingInfo paging, final Integer maxResults,
	        Order... orders) {
		if (date == null) {
			throw new IllegalArgumentException("The date to search for must be defined.");
		}

		return executeCriteria(StockOperation.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(createDateRestriction(date));
				if (maxResults != null && maxResults > 0) {
					criteria.setMaxResults(maxResults);
				}
			}
		}, orders);
	}

	private Criterion createDateRestriction(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Utility.clearCalendarTime(cal);
		final Date start = cal.getTime();

		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MILLISECOND, -1);
		final Date end = cal.getTime();

		return Restrictions.between(HibernateCriteriaConstants.OPERATION_DATE, start, end);
	}

	@Override
	public void purge(StockOperation operation) {
		if (operation != null && ((operation.hasReservedTransactions()) || operation.hasTransactions())) {
			throw new APIException("Stock operations can not be deleted if there are any associated transactions.");
		}

		super.purge(operation);
	}
}
