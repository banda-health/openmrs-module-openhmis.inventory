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

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IStockRoomDataService;
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class StockRoomDataServiceImpl
		extends BaseMetadataDataServiceImpl<StockRoom>
		implements IStockRoomDataService, IMetadataAuthorizationPrivileges {
	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return this;
	}

	@Override
	protected void validate(StockRoom object) throws APIException {
	}

	@Override
	protected Collection<? extends OpenmrsObject> getRelatedObjects(StockRoom entity) {
		ArrayList<OpenmrsObject> results = new ArrayList<OpenmrsObject>();

		results.addAll(entity.getOperations());
		results.addAll(entity.getItems());

		return results;
	}

	@Override
	public List<ItemStock> getItemsByRoom(final StockRoom stockRoom, PagingInfo paging) {
		if (stockRoom == null) {
			throw new IllegalArgumentException("The stock room must be defined");
		}

		return executeCriteria(ItemStock.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.createAlias("item", "i");
				criteria.add(Restrictions.eq("stockRoom", stockRoom));
			}
		}, Order.asc("i.name"));
	}

	@Override
	public List<ItemStock> findItems(final StockRoom stockRoom, final ItemSearch itemSearch, PagingInfo paging) {
		if (stockRoom == null) {
			throw new IllegalArgumentException("The stock room must be defined.");
		}
		if (itemSearch == null) {
			throw new IllegalArgumentException("The item search must be defined.");
		}

		// To allow a method to exclude retired items from the result callers can set IncludeRetired to null
		//  and specify the retired status on the template object itself
		if (itemSearch.getIncludeRetired() != null) {
			// We want all the stock room items regardless of if they are retired
			itemSearch.setIncludeRetired(true);
		}

		return executeCriteria(ItemStock.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq("stockRoom", stockRoom));

				itemSearch.updateCriteria(criteria.createCriteria("item", "i"));
			}
		}, Order.asc("i.name"));
	}

	@Override
	public ItemStock getItem(StockRoom stockRoom, Item item) {
		if (stockRoom == null) {
			throw new IllegalArgumentException("The stockroom must be defined.");
		}
		if (item == null) {
			throw new IllegalArgumentException("The item must be defined.");
		}

		Criteria criteria = repository.createCriteria(ItemStock.class);
		criteria.add(Restrictions.eq("stockRoom", stockRoom));
		criteria.add(Restrictions.eq("item", item));

		return repository.selectSingle(ItemStock.class, criteria);
	}

	@Override
	public ItemStockDetail getStockroomItemDetail(StockRoom stockroom, Item item, Date expiration, StockOperation batchOperation) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined.");
		}
		if (item == null) {
			throw new IllegalArgumentException("The item must be defined.");
		}

		Criteria criteria = repository.createCriteria(ItemStockDetail.class);
		criteria.add(Restrictions.eq("stockRoom", stockroom));
		criteria.add(Restrictions.eq("item", item));

		if (expiration == null) {
			criteria.add(Restrictions.isNull("expiration"));
		} else {
			criteria.add(Restrictions.eq("expiration", expiration));
		}

		if (batchOperation == null) {
			criteria.add(Restrictions.isNull("batchOperation"));
		} else {
			criteria.add(Restrictions.eq("batchOperation", batchOperation));
		}

		return repository.selectSingle(ItemStockDetail.class, criteria);
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

