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

import java.util.List;

public class StockRoomTransactionType extends BaseOpenmrsMetadata {
	public static final long serialVersionUID = 0L;

	private Integer stockRoomTransferTypeId;
	private List<StockRoomTransactionTypeAttributeType> attributeTypes;
	private PendingTransactionItemQuantityType quantityType;
	private boolean sourceRequired;
	private boolean destinationRequired;
	private boolean authorized;

	@Override
	public Integer getId() {
		return stockRoomTransferTypeId;
	}

	@Override
	public void setId(Integer id) {
		stockRoomTransferTypeId = id;
	}

	public List<StockRoomTransactionTypeAttributeType> getAttributeTypes() {
		return attributeTypes;
	}

	public void setAttributeTypes(List<StockRoomTransactionTypeAttributeType> attributeTypes) {
		this.attributeTypes = attributeTypes;
	}

	public PendingTransactionItemQuantityType getQuantityType() {
		return quantityType;
	}

	public void setQuantityType(PendingTransactionItemQuantityType quantityType) {
		this.quantityType = quantityType;
	}

	public boolean isSourceRequired() {
		return sourceRequired;
	}

	public void setSourceRequired(boolean sourceRequired) {
		this.sourceRequired = sourceRequired;
	}

	public boolean isDestinationRequired() {
		return destinationRequired;
	}

	public void setDestinationRequired(boolean destinationRequired) {
		this.destinationRequired = destinationRequired;
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}
}

