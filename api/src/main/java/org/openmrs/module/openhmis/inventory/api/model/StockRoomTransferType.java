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

import org.openmrs.BaseOpenmrsMetadata;

import java.util.Set;

public class StockRoomTransferType extends BaseOpenmrsMetadata {
	public static final long serialVersionUID = 0L;

	private Integer stockRoomTransferTypeId;
	private Set<StockRoomTransferTypeAttributeType> attributeTypes;
	private boolean authorized;

	@Override
	public Integer getId() {
		return stockRoomTransferTypeId;
	}

	@Override
	public void setId(Integer id) {
		stockRoomTransferTypeId = id;
	}

	public Set<StockRoomTransferTypeAttributeType> getAttributeTypes() {
		return attributeTypes;
	}

	public void setAttributeTypes(Set<StockRoomTransferTypeAttributeType> attributeTypes) {
		this.attributeTypes = attributeTypes;
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}
}
