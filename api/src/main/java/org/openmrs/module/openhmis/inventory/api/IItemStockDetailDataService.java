package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;

import java.util.List;

public interface IItemStockDetailDataService extends IObjectDataService<ItemStockDetail> {

	List<ItemStockDetail> getItemStockDetailsByStockroom(Stockroom stockroom, PagingInfo pagingInfo);

	List<ItemStockDetail> getAllItems(PagingInfo pagingInfo);

}
