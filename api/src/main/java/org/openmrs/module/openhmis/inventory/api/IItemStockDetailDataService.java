package org.openmrs.module.openhmis.inventory.api;

import java.util.List;

import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;

public interface IItemStockDetailDataService extends IObjectDataService<ItemStockDetail> {

	List<ItemStockDetail> getItemStockDetailsByStockroom(Stockroom stockroom, PagingInfo pagingInfo);
}
