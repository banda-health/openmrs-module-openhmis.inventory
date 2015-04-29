package org.openmrs.module.openhmis.inventory.api;

import java.util.List;

import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockSummary;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;

public interface IItemStockDetailDataService extends IObjectDataService<ItemStockDetail> {
	List<ItemStockDetail> getItemStockDetailsByStockroom(Stockroom stockroom, PagingInfo pagingInfo);

	/**
	 * Returns the aggregate {@link ItemStockSummary} records for the specified stockroom.
	 * @param stockroom The stockroom.
	 * @param pagingInfo The paging information.
	 * @return The item stock summary records or an empty list when none are found
	 * @should throw IllegalArgumentException if the stockroom is null
	 * @should return an empty list if no records are found
	 * @should return the item stock summary records
	 * @should return paged results if paging is specified
	 */
	List<ItemStockSummary> getItemStockSummaryByStockroom(Stockroom stockroom, PagingInfo pagingInfo);
}
