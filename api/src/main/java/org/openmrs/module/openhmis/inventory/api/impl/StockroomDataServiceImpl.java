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
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class StockroomDataServiceImpl
		extends BaseMetadataDataServiceImpl<Stockroom>
		implements IStockroomDataService, IMetadataAuthorizationPrivileges {
	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return this;
	}

	@Override
	protected void validate(Stockroom object) throws APIException {}

	@Override
	protected Collection<? extends OpenmrsObject> getRelatedObjects(Stockroom entity) {
		ArrayList<OpenmrsObject> results = new ArrayList<OpenmrsObject>();

		results.addAll(entity.getOperations());
		results.addAll(entity.getItems());

		return results;
	}

	@Override
	public List<ItemStock> getItemsByRoom(final Stockroom stockroom, PagingInfo paging) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined");
		}

		return executeCriteria(ItemStock.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.createAlias("item", "i");
				criteria.setResultTransformer(Criteria.ROOT_ENTITY);
				criteria.add(Restrictions.eq("stockroom", stockroom));
			}
		}, Order.asc("i.name"));
	}

	@Override
	public List<ItemStock> findItems(final Stockroom stockroom, final ItemSearch itemSearch, PagingInfo paging) {
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
				criteria.add(Restrictions.eq("stockroom", stockroom));
				itemSearch.updateCriteria(criteria.createCriteria("item", "i"));
			}
		}, Order.asc("i.name"));
	}

	@Override
	public ItemStock getItem(Stockroom stockroom, Item item) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined.");
		}
		if (item == null) {
			throw new IllegalArgumentException("The item must be defined.");
		}

		Criteria criteria = repository.createCriteria(ItemStock.class);
		criteria.add(Restrictions.eq("stockroom", stockroom));
		criteria.add(Restrictions.eq("item", item));

		return repository.selectSingle(ItemStock.class, criteria);
	}

	@Override
	public ItemStockDetail getStockroomItemDetail(Stockroom stockroom, Item item, Date expiration, StockOperation batchOperation) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined.");
		}
		if (item == null) {
			throw new IllegalArgumentException("The item must be defined.");
		}

		Criteria criteria = repository.createCriteria(ItemStockDetail.class);
		criteria.add(Restrictions.eq("stockroom", stockroom));
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
    public List<Stockroom> getStockroomsByLocation(Location location, boolean includeRetired) throws APIException {
        return getStockroomsByLocation(location, includeRetired, null);
    }

    @Override
    public List<Stockroom> getStockroomsByLocation(final Location location, final boolean includeRetired, PagingInfo pagingInfo) throws APIException {
        if (location == null) {
            throw new NullPointerException("The location must be defined");
        }

        return executeCriteria(Stockroom.class, pagingInfo, new Action1<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("location", location));
                if (!includeRetired) {
                    criteria.add(Restrictions.eq("retired", false));
                }
            }
        });
    }

    @Override
    public List<Stockroom> findStockrooms(Location location, String name, boolean includeRetired) throws APIException {
        return findStockrooms(location, name, includeRetired, null);
    }

    @Override
    public List<Stockroom> findStockrooms(final Location location, final String name, final boolean includeRetired, PagingInfo pagingInfo) throws APIException {
        if (location == null) {
            throw new NullPointerException("The department must be defined");
        }
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("The stockroom code must be defined.");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("The stockroom code must be less than 256 characters.");
        }

        return executeCriteria(Stockroom.class, pagingInfo, new Action1<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("location", location))
                        .add(Restrictions.ilike("name", name, MatchMode.START));

                if (!includeRetired) {
                    criteria.add(Restrictions.eq("retired", false));
                }
            }
        });
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

