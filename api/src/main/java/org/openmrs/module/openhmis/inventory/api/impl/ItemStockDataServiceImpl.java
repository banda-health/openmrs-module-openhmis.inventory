package org.openmrs.module.openhmis.inventory.api.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseObjectDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IItemStockDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.security.BasicObjectAuthorizationPrivileges;

import java.util.List;

public class ItemStockDataServiceImpl
		extends BaseObjectDataServiceImpl<ItemStock, BasicObjectAuthorizationPrivileges>
		implements IItemStockDataService {

	@Override
	protected BasicObjectAuthorizationPrivileges getPrivileges() {
		return new BasicObjectAuthorizationPrivileges();
	}

	@Override
	protected void validate(ItemStock object) throws APIException {

	}

	@Override public List<ItemStock> getItemStockByItem(final Item item, PagingInfo pagingInfo) {
		if (item == null) {
			throw new IllegalArgumentException("The item must be defined.");
		}

		return executeCriteria(ItemStock.class, pagingInfo, new Action1<Criteria>() {
			@Override public void apply(Criteria criteria) {
				criteria.createAlias("stockroom", "s");
				criteria.add(Restrictions.eq("item", item));
			}
		}, Order.asc("s.name"));
	}
}
