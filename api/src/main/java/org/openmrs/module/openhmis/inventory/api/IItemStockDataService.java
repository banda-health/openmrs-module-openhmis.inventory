package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;

import java.util.List;

public interface IItemStockDataService extends IObjectDataService<ItemStock> {
	/**
	 * Returns the {@link ItemStock} for the specified {@link Item}.
	 * @param item The item to find the item stock of.
	 * @param pagingInfo The paging information.
	 * @return The item stock for the specified item.
	 * @should return all item stock for the item ordered by stockroom name
	 * @should return paged item stock when paging is specified
	 * @should return an empty list if there is no item stock
	 * @should throw IllegalArgumentException if item is null
	 */
	List<ItemStock> getItemStockByItem(Item item, PagingInfo pagingInfo);
}
