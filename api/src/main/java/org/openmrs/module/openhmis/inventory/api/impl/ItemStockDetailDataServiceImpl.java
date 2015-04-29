package org.openmrs.module.openhmis.inventory.api.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.google.common.primitives.Ints;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.impl.CriteriaImpl;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseObjectDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IItemStockDetailDataService;
import org.openmrs.module.openhmis.inventory.api.model.IStockOperationType;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockSummary;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.api.security.BasicObjectAuthorizationPrivileges;

public class ItemStockDetailDataServiceImpl
		extends BaseObjectDataServiceImpl<ItemStockDetail, BasicObjectAuthorizationPrivileges>
		implements IItemStockDetailDataService {
	@Override
	public List<ItemStockDetail> getItemStockDetailsByStockroom(final Stockroom stockroom, PagingInfo pagingInfo) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined.");
		}

		return executeCriteria(ItemStockDetail.class, pagingInfo, new Action1<Criteria>() {
			@Override public void apply(Criteria criteria) {
				criteria.createAlias("item", "i");
				criteria.add(Restrictions.eq("stockroom", stockroom));
			}
		}, Order.asc("i.name"));
    }

	@Override
	public List<ItemStockSummary> getItemStockSummaryByStockroom(final Stockroom stockroom, PagingInfo pagingInfo) {
		if (stockroom == null) {
			throw new IllegalArgumentException("The stockroom must be defined.");
		}

		// Because this is an aggregate query we cannot use the normal executeCriteria method and instead have to do it
		// manually.
		Criteria criteria = getRepository().createCriteria(ItemStockDetail.class);
		criteria.createAlias("item", "i");
		criteria.add(Restrictions.eq("stockroom", stockroom));

		criteria.setProjection(
				Projections.projectionList()
						.add(Projections.groupProperty("item"))
						.add(Projections.groupProperty("expiration"))
						.add(Projections.sum("quantity"))
		);

		// Load the record count (for paging)
		if (pagingInfo != null && pagingInfo.shouldLoadRecordCount()) {
			// Because we're already doing a group by query, we can't just use the loadPagingTotal method.

			// This is horrible, it just executes the full query to get the count.
			//		TODO: Figure out how to get hibernate to do a count on a subquery (via HQL or criteria)
			List countList = criteria.list();
			Integer count = countList.size();

			pagingInfo.setTotalRecordCount(count.longValue());
			pagingInfo.setLoadRecordCount(false);
		}

		// Add the ordering
		criteria.addOrder(Order.asc("i.name"));
		criteria.addOrder(Order.asc("expiration"));

		// Add the paging stuff
		criteria = this.createPagingCriteria(pagingInfo, criteria);

		// Load the criteria into an untyped list
		List list = criteria.list();

		// Parse the aggregate query into an ItemStockSummary object
		List<ItemStockSummary> results = new ArrayList<ItemStockSummary>(list.size());
		for (Object obj : list) {
			Object[] row = (Object[])obj;

			ItemStockSummary summary = new ItemStockSummary();
			summary.setItem((Item)row[0]);

			// If the expiration column is null is does not appear to be included in the row array
			if (row.length == 2) {
				summary.setExpiration(null);
				summary.setQuantity(Ints.checkedCast((Long)row[1]));
			} else {
				summary.setExpiration((Date)row[1]);
				summary.setQuantity(Ints.checkedCast((Long)row[2]));
			}

			results.add(summary);
		}

		// We done.
		return results;
	}

	@Override
	protected BasicObjectAuthorizationPrivileges getPrivileges() {
		return new BasicObjectAuthorizationPrivileges();
    }

	@Override
	protected void validate(ItemStockDetail object) {

	}
}
