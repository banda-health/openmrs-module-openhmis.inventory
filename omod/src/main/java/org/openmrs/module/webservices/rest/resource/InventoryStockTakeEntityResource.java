package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockSummary;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

@Resource(name = ModuleRestConstants.INVENTORY_STOCK_TAKE_SUMMARY_RESOURCE, supportedClass = ItemStockSummary.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*" })
public class InventoryStockTakeEntityResource extends BaseRestObjectResource<ItemStockSummary> {
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.addProperty("item", Representation.DEFAULT);
		description.addProperty("expiration", Representation.DEFAULT);
		description.addProperty("quantity", Representation.DEFAULT);
		description.addProperty("actualQuantity", Representation.DEFAULT);
		
		return description;
	}
	
	@PropertySetter("expiration")
	public void setExpiration(ItemStockSummary instance, String dateText) {
		System.out.println("aaaaaaaaaaaaaa");
	}
	
	@Override
	public Class<? extends IObjectDataService<ItemStockSummary>> getServiceClass() {
		return null;
	}
	
	@Override
	public ItemStockSummary newDelegate() {
		return new ItemStockSummary();
	}
}
