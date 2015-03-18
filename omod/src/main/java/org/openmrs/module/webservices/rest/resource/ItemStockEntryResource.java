package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.model.ItemStockEntry;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Resource(name = ModuleRestConstants.ITEM_STOCK_ENTRY_RESOURCE, supportedClass=ItemStockEntry.class,
		supportedOpenmrsVersions={"1.9.*", "1.10.*", "1.11.*" })
public class ItemStockEntryResource extends ItemStockDetailBaseResource<ItemStockEntry> {
	@Override
	public ItemStockEntry save(ItemStockEntry item) {
		// Parse the item stock and create the required item stock and item stock detail objects

		throw new NotImplementedException();
	}

	@Override
	public ItemStockEntry newDelegate() {
		return new ItemStockEntry();
	}

	@Override
	public Class<? extends IObjectDataService<ItemStockEntry>> getServiceClass() {
		return null;
	}
}
