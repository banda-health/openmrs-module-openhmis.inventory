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
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseObjectDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IItemStockDetailDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockSummary;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.api.security.BasicObjectAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.primitives.Ints;

/**
 * Data service implementation class for {@link ItemStockDetail}.
 */
@Transactional
public class ItemStockDetailDataServiceImpl
        extends BaseObjectDataServiceImpl<ItemStockDetail, BasicObjectAuthorizationPrivileges>
        implements IItemStockDetailDataService {
	@Override
	protected BasicObjectAuthorizationPrivileges getPrivileges() {
		return new BasicObjectAuthorizationPrivileges();
	}

	@Override
	protected void validate(ItemStockDetail object) {

	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_METADATA })
	public List<ItemStockDetail> getItemStockDetailsByStockroom(final Stockroom stockroom, PagingInfo pagingInfo) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined.");
		}

		return executeCriteria(ItemStockDetail.class, pagingInfo, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.createAlias("item", "i");
				criteria.add(Restrictions.eq("stockroom", stockroom));
			}
		}, Order.asc("i.name"));
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_METADATA })
	public List<ItemStockSummary> getItemStockSummaryByStockroom(final Stockroom stockroom, PagingInfo pagingInfo) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined.");
		}

		// We cannot use a normal Criteria query here because criteria does not support a group by with a having statement
		// so HQL it is!

		if (pagingInfo != null && pagingInfo.shouldLoadRecordCount()) {
			// Load the record count (for paging)
			String countHql = "select 1 "
			        + "from ItemStockDetail as detail "
			        + "where stockroom.id = " + stockroom.getId() + " "
			        + "group by item, expiration "
			        + "having sum(detail.quantity) <> 0";
			Query countQuery = getRepository().createQuery(countHql);

			Integer count = countQuery.list().size();

			pagingInfo.setTotalRecordCount(count.longValue());
			pagingInfo.setLoadRecordCount(false);
		}

		// Create the query and optionally add paging
		String hql = "select i, detail.expiration, sum(detail.quantity) as sumQty "
		        + "from ItemStockDetail as detail inner join detail.item as i "
		        + "where detail.stockroom.id = " + stockroom.getId() + " "
		        + "group by i, detail.expiration "
		        + "having sum(detail.quantity) <> 0"
		        + "order by i.name asc, detail.expiration asc";
		Query query = getRepository().createQuery(hql);
		query = this.createPagingQuery(pagingInfo, query);

		List list = query.list();

		// Parse the aggregate query into an ItemStockSummary object
		List<ItemStockSummary> results = new ArrayList<ItemStockSummary>(list.size());
		for (Object obj : list) {
			Object[] row = (Object[])obj;

			ItemStockSummary summary = new ItemStockSummary();
			summary.setItem((Item)row[0]);

			// If the expiration column is null it does not appear to be included in the row array
			if (row.length == 2) {
				summary.setExpiration(null);
				Integer quantity = Ints.checkedCast((Long)row[1]);
				// skip record if the sum of item stock quantities == 0
				if (quantity != 0) {
					summary.setQuantity(quantity);
				} else {
					continue;
				}
			} else {
				summary.setExpiration((Date)row[1]);
				Integer quantity = Ints.checkedCast((Long)row[2]);
				if (quantity != 0) {
					summary.setQuantity(quantity);
				} else {
					continue;
				}
			}

			results.add(summary);
		}

		// We done.
		return results;
	}
}
