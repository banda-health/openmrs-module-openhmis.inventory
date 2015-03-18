package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationAttribute;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationAttributeType;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

@Resource(name= ModuleRestConstants.OPERATION_ATTRIBUTE_RESOURCE, supportedClass=StockOperationAttribute.class,
		supportedOpenmrsVersions={"1.9.*", "1.10.*", "1.11.*" })
public class StockOperationAttributeResource extends BaseRestAttributeObjectResource<
		StockOperationAttribute, StockOperationAttributeType> {
	@Override
	public StockOperationAttribute newDelegate() {
		return new StockOperationAttribute();
	}

	@Override
	public Class<? extends IObjectDataService<StockOperationAttribute>> getServiceClass() {
		return null;
	}

	@Override
	@PropertyGetter("value")
	public Object getValue(StockOperationAttribute instance) {
		return super.getValue(instance);
	}

	@PropertySetter("attributeType")
	public void setAttributeType(StockOperationAttribute instance, StockOperationAttributeType attributeType) {
		instance.setAttributeType(attributeType);
	}
}
