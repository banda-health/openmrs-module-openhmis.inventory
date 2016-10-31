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
package org.openmrs.module.openhmis.inventory.api.model;

import java.math.BigDecimal;

import org.openmrs.module.openhmis.commons.api.entity.model.BaseSerializableOpenmrsMetadata;

/**
 * Model class to represent the price of an {@link Item}.
 */
public class ItemPrice extends BaseSerializableOpenmrsMetadata {
	public static final long serialVersionUID = 0L;

	private Integer itemPriceId;
	private BigDecimal price;
	private Item item;

	public ItemPrice() {
		super();
	}

	public ItemPrice(BigDecimal price, String name) {
		super();

		this.price = price;
		setName(name);
	}

	@Override
	public Integer getId() {
		return itemPriceId;
	}

	@Override
	public void setId(Integer id) {
		itemPriceId = id;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
