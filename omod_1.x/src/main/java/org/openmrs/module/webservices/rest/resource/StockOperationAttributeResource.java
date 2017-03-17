/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationAttribute;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationAttributeType;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

/**
 * REST resource representing a {@link StockOperationAttribute}.
 */
@Resource(name = ModuleRestConstants.OPERATION_ATTRIBUTE_RESOURCE, supportedClass = StockOperationAttribute.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*" })
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

	@PropertyGetter("value")
	public Object getValue(StockOperationAttribute instance) {
		return super.baseGetValue(instance);
	}

	@PropertySetter("attributeType")
	public void setAttributeType(StockOperationAttribute instance, StockOperationAttributeType attributeType) {
		baseSetAttributeType(instance, attributeType);
	}
}
