package org.openmrs.module.openhmis.inventory.api.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseObjectDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IItemStockDetailDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.api.security.BasicObjectAuthorizationPrivileges;

public class ItemStockDetailDataServiceImpl extends BaseObjectDataServiceImpl<ItemStockDetail, BasicObjectAuthorizationPrivileges> implements IItemStockDetailDataService {


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
    protected BasicObjectAuthorizationPrivileges getPrivileges() {
		return new BasicObjectAuthorizationPrivileges();
    }

	@Override
    protected void validate(ItemStockDetail object) {

    }

}
