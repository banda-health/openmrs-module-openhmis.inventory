package org.openmrs.module.webservices.rest.resource;

import java.util.Date;

import org.openmrs.module.openhmis.commons.api.Utility;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.InventoryStockTakeEntity;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

@Resource(name = ModuleRestConstants.INVENTORY_STOCK_TAKE_ENTITY_RESOURCE, supportedClass = InventoryStockTakeEntity.class,
supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*" })
public class InventoryStockTakeEntityResource extends ItemStockDetailBaseResource<InventoryStockTakeEntity> {

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep)  {
    	DelegatingResourceDescription description =  super.getRepresentationDescription(rep);
		description.addProperty("stockroom", Representation.DEFAULT);
		description.addProperty("actualQuantity", Representation.DEFAULT);
		return description;
    }

    @PropertySetter("expiration")
	public void setExpiration(InventoryStockTakeEntity instance, String dateText) {
    	System.out.println("aaaaaaaaaaaaaa");
    }

	@Override
    public Class<? extends IObjectDataService<InventoryStockTakeEntity>> getServiceClass() {
	    return null;
    }

	@Override
    public InventoryStockTakeEntity newDelegate() {
	    return new InventoryStockTakeEntity();
    }
}
