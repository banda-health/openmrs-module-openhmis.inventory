package org.openmrs.module.webservices.rest.resource;

import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.model.ItemStockEntry;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

/**
 * REST resource representing an {@link ItemStockEntry}.
 */
@Resource(name = ModuleRestConstants.ITEM_STOCK_ENTRY_RESOURCE, supportedClass = ItemStockEntry.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class ItemStockEntryResource extends ItemStockDetailBaseResource<ItemStockEntry> {
	@Override
	public ItemStockEntry save(ItemStockEntry item) {
		throw new APIException("Not implemented.");
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
