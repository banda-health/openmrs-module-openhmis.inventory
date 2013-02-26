/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.attribute.BaseAttributeType;

public class ItemAttributeType extends BaseAttributeType<Item> {
	public Integer itemAttributeTypeId;

	@Override
	public Integer getId() {
		return this.itemAttributeTypeId;
	}

	@Override
	public void setId(Integer id) {
		this.itemAttributeTypeId = id;
	}
}
