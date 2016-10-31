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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseObjectDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IItemStockDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.security.BasicObjectAuthorizationPrivileges;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data service implementation class for {@link ItemStock}.
 */
@Transactional
public class ItemStockDataServiceImpl extends BaseObjectDataServiceImpl<ItemStock, BasicObjectAuthorizationPrivileges>
        implements IItemStockDataService {

	@Override
	protected BasicObjectAuthorizationPrivileges getPrivileges() {
		return new BasicObjectAuthorizationPrivileges();
	}

	@Override
	protected void validate(ItemStock object) {

	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemStock> getItemStockByItem(final Item item, PagingInfo pagingInfo) {
		if (item == null) {
			throw new IllegalArgumentException("The item must be defined.");
		}

		return executeCriteria(ItemStock.class, pagingInfo, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.createAlias("stockroom", "s");
				criteria.add(Restrictions.eq("item", item));
			}
		}, Order.asc("s.name"));
	}
}
