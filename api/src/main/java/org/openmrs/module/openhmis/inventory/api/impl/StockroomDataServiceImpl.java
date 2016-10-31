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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.OpenmrsObject;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTransaction;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch;
import org.openmrs.module.openhmis.inventory.api.util.HibernateCriteriaConstants;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data service implementation class for {@link Stockroom}.
 */
public class StockroomDataServiceImpl extends BaseMetadataDataServiceImpl<Stockroom>
        implements IStockroomDataService, IMetadataAuthorizationPrivileges {
	private static final int MAX_STOCKROOM_CODE_LENGTH = 255;

	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return this;
	}

	@Override
	protected void validate(Stockroom object) {}

	@Override
	protected Collection<? extends OpenmrsObject> getRelatedObjects(Stockroom entity) {
		ArrayList<OpenmrsObject> results = new ArrayList<OpenmrsObject>();

		results.addAll(entity.getOperations());
		results.addAll(entity.getItems());

		return results;
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	public List<ItemStock> getItemsByRoom(final Stockroom stockroom, PagingInfo paging) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined");
		}

		return executeCriteria(ItemStock.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.createAlias("item", "i");
				criteria.setResultTransformer(Criteria.ROOT_ENTITY);
				criteria.add(Restrictions.eq(HibernateCriteriaConstants.STOCKROOM, stockroom));
			}
		}, Order.asc("i.name"));
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	public List<StockOperationTransaction> getTransactionsByRoom(final Stockroom stockroom, PagingInfo paging) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined");
		}

		return executeCriteria(StockOperationTransaction.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq(HibernateCriteriaConstants.STOCKROOM, stockroom));
			}
		}, Order.desc(HibernateCriteriaConstants.DATE_CREATED), Order.desc(HibernateCriteriaConstants.ID));
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	public List<StockOperationTransaction> getTransactionsByRoomAndItem(final Stockroom stockroom, final Item item,
	        PagingInfo paging) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined");
		}

		if (item == null) {
			throw new IllegalArgumentException("The item must be defined");
		}

		return executeCriteria(StockOperationTransaction.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq(HibernateCriteriaConstants.STOCKROOM, stockroom));
				criteria.add(Restrictions.eq(HibernateCriteriaConstants.ITEM, item));
			}
		}, Order.desc(HibernateCriteriaConstants.DATE_CREATED), Order.desc(HibernateCriteriaConstants.ID));
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	public List<ItemStock> getItems(final Stockroom stockroom, final ItemSearch itemSearch, PagingInfo paging) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined.");
		}
		if (itemSearch == null) {
			throw new IllegalArgumentException("The item search must be defined.");
		}

		// To allow a method to exclude retired items from the result callers can set IncludeRetired to null
		//  and specify the retired status on the template object itself
		if (itemSearch.getIncludeRetired() != null) {
			// We want all the stockroom items regardless of if they are retired
			itemSearch.setIncludeRetired(true);
		}

		return executeCriteria(ItemStock.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq(HibernateCriteriaConstants.STOCKROOM, stockroom));
				itemSearch.updateCriteria(criteria.createCriteria("item", "i"));
			}
		}, Order.asc("i.name"));
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	public List<StockOperation> getOperations(final Stockroom stockroom, final StockOperationSearch search,
	        PagingInfo paging) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined");
		}

		return executeCriteria(StockOperation.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				if (search != null) {
					search.updateCriteria(criteria);
				}
				criteria.add(Restrictions.or(Restrictions.eq(HibernateCriteriaConstants.SOURCE, stockroom),
				    Restrictions.eq(HibernateCriteriaConstants.DESTINATION, stockroom)));
			}
		}, Order.desc("dateChanged"), Order.desc("dateCreated"));
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	public ItemStock getItem(Stockroom stockroom, Item item) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined.");
		}
		if (item == null) {
			throw new IllegalArgumentException("The item must be defined.");
		}

		Criteria criteria = getRepository().createCriteria(ItemStock.class);
		criteria.add(Restrictions.eq(HibernateCriteriaConstants.STOCKROOM, stockroom));
		criteria.add(Restrictions.eq(HibernateCriteriaConstants.ITEM, item));

		return getRepository().selectSingle(ItemStock.class, criteria);
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	public ItemStockDetail getStockroomItemDetail(Stockroom stockroom, Item item, Date expiration,
	        StockOperation batchOperation) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined.");
		}
		if (item == null) {
			throw new IllegalArgumentException("The item must be defined.");
		}

		Criteria criteria = getRepository().createCriteria(ItemStockDetail.class);
		criteria.add(Restrictions.eq(HibernateCriteriaConstants.STOCKROOM, stockroom));
		criteria.add(Restrictions.eq(HibernateCriteriaConstants.ITEM, item));

		if (expiration == null) {
			criteria.add(Restrictions.isNull(HibernateCriteriaConstants.EXPIRATION));
		} else {
			criteria.add(Restrictions.eq(HibernateCriteriaConstants.EXPIRATION, expiration));
		}

		if (batchOperation == null) {
			criteria.add(Restrictions.isNull(HibernateCriteriaConstants.BATCH_OPERATION));
		} else {
			criteria.add(Restrictions.eq(HibernateCriteriaConstants.BATCH_OPERATION, batchOperation));
		}

		return getRepository().selectSingle(ItemStockDetail.class, criteria);
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	public List<Stockroom> getStockroomsByLocation(Location location, boolean includeRetired) {
		return getStockroomsByLocation(location, includeRetired, null);
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	public List<Stockroom> getStockroomsByLocation(final Location location, final boolean includeRetired,
	        PagingInfo pagingInfo) {
		if (location == null) {
			throw new NullPointerException("The location must be defined");
		}

		return executeCriteria(Stockroom.class, pagingInfo, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq(HibernateCriteriaConstants.LOCATION, location));
				if (!includeRetired) {
					criteria.add(Restrictions.eq(HibernateCriteriaConstants.RETIRED, false));
				}
			}
		}, getDefaultSort());
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	public List<Stockroom> getStockrooms(Location location, String name, boolean includeRetired) {
		return getStockrooms(location, name, includeRetired, null);
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	public List<Stockroom> getStockrooms(final Location location, final String name, final boolean includeRetired,
	        PagingInfo pagingInfo) {
		if (location == null) {
			throw new NullPointerException("The department must be defined");
		}
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("The stockroom code must be defined.");
		}
		if (name.length() > MAX_STOCKROOM_CODE_LENGTH) {
			throw new IllegalArgumentException("The stockroom code must be less than 256 characters.");
		}

		return executeCriteria(Stockroom.class, pagingInfo, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq(HibernateCriteriaConstants.LOCATION, location)).add(
				    Restrictions.ilike(HibernateCriteriaConstants.NAME, name, MatchMode.START));

				if (!includeRetired) {
					criteria.add(Restrictions.eq(HibernateCriteriaConstants.RETIRED, false));
				}
			}
		}, getDefaultSort());
	}

	@Override
	public String getRetirePrivilege() {
		return PrivilegeConstants.MANAGE_STOCKROOMS;
	}

	@Override
	public String getSavePrivilege() {
		return PrivilegeConstants.MANAGE_STOCKROOMS;
	}

	@Override
	public String getPurgePrivilege() {
		return PrivilegeConstants.PURGE_STOCKROOMS;
	}

	@Override
	public String getGetPrivilege() {
		return PrivilegeConstants.VIEW_STOCKROOMS;
	}
}
