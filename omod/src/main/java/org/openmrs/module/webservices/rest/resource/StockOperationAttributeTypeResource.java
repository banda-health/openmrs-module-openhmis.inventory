package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.model.IStockOperationType;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationAttribute;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationAttributeType;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

@Resource(name= ModuleRestConstants.OPERATION_ATTRIBUTE_TYPE_RESOURCE, supportedClass=StockOperationAttributeType.class, supportedOpenmrsVersions={"1.9"})
public class StockOperationAttributeTypeResource
	extends BaseRestInstanceAttributeTypeResource<StockOperationAttributeType, StockOperation, IStockOperationType, StockOperationAttribute> {
	@Override
	public StockOperationAttributeType newDelegate() {
		return new StockOperationAttributeType();
	}

	@Override
	public Class<? extends IMetadataDataService<StockOperationAttributeType>> getServiceClass() {
		return null;
	}
}
