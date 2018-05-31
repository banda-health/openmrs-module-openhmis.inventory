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
import org.openmrs.module.openhmis.inventory.api.IItemAttributeDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemAttribute;
import org.openmrs.module.openhmis.inventory.api.model.ItemAttributeType;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

/**
 * REST resource representing an {@link ItemAttribute}.
 */
@Resource(name = ModuleRestConstants.ITEM_ATTRIBUTE_RESOURCE, supportedClass = ItemAttribute.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*" })
public class ItemAttributeResource extends BaseRestAttributeObjectResource<ItemAttribute, ItemAttributeType> {
	@Override
	public ItemAttribute newDelegate() {
		return new ItemAttribute();
	}

	@PropertyGetter("value")
	public Object getValue(ItemAttribute attribute) {
		return super.baseGetValue(attribute);
	}

	@PropertySetter("attributeType")
	public void setAttributeType(ItemAttribute instance, ItemAttributeType attributeType) {
		super.baseSetAttributeType(instance, attributeType);
	}

	@Override
	public Class<? extends IObjectDataService<ItemAttribute>> getServiceClass() {
		return null;
		//		return IItemAttributeDataService.class;
	}
}
