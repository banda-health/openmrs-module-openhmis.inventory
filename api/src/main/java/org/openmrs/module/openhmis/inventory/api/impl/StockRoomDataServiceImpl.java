package org.openmrs.module.openhmis.inventory.api.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IStockRoomDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.StockRoom;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomItem;
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

		results.addAll(entity.getTransactions());
		results.addAll(entity.getItems());

		return results;
	}

	@Override
	public List<StockRoomItem> getItemsByRoom(final StockRoom stockRoom, PagingInfo paging) {
		if (stockRoom == null) {
			throw new NullPointerException("The stock room must be defined");
		}

		List<StockRoomItem> result = executeCriteria(StockRoomItem.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq("stockRoom", stockRoom));
			}
		});

		// Force the results to be sorted by the stock room item name (see StockRoomItem.compareTo)
		java.util.Collections.sort(result);
		return result;
	}

	@Override
	public List<StockRoomItem> findItems(final StockRoom stockRoom, final ItemSearch itemSearch, PagingInfo paging) {
		if (stockRoom == null) {
			throw new NullPointerException("The stock room must be defined.");
		}
		if (itemSearch == null) {
			throw new NullPointerException("The item search must be defined.");
		}

		// To allow a method to exclude retired items from the result callers can set IncludeRetired to null
		//  and specify the retired status on the template object itself
		if (itemSearch.getIncludeRetired() != null) {
			// We want all the stock room items regardless of if they are retired
			itemSearch.setIncludeRetired(true);
		}

		return executeCriteria(StockRoomItem.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq("stockRoom", stockRoom));

				itemSearch.updateCriteria(criteria.createCriteria("item"));
			}
		});
	}

	@Override
	public StockRoomItem getItem(StockRoom stockRoom, Item item, Date expiration) {
		if (stockRoom == null) {
			throw new NullPointerException("The stock room must be defined.");
		}
		if (item == null) {
			throw new NullPointerException("The item must be defined.");
		}

		Criteria criteria = repository.createCriteria(StockRoomItem.class);
		criteria.add(Restrictions.eq("stockRoom", stockRoom));
		criteria.add(Restrictions.eq("item", item));

		if (expiration == null) {
			criteria.add(Restrictions.isNull("expiration"));
		} else {
			criteria.add(Restrictions.eq("expiration", expiration));
		}

		return repository.selectSingle(StockRoomItem.class, criteria);
	}

	@Override
	public String getRetirePrivilege() {
		return PrivilegeConstants.MANAGE_STOCK_ROOMS;
	}

	@Override
	public String getSavePrivilege() {
		return PrivilegeConstants.MANAGE_STOCK_ROOMS;
	}

	@Override
	public String getPurgePrivilege() {
		return PrivilegeConstants.PURGE_STOCK_ROOMS;
	}

	@Override
	public String getGetPrivilege() {
		return PrivilegeConstants.VIEW_STOCK_ROOMS;
	}
}
