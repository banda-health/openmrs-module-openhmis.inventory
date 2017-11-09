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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemAttribute;
import org.openmrs.module.openhmis.inventory.api.model.ItemCode;
import org.openmrs.module.openhmis.inventory.api.model.ItemPrice;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.helper.Converter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

/**
 * REST resource representing an {@link Item}.
 */
@Resource(name = ModuleRestConstants.ITEM_RESOURCE, supportedClass = Item.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*" })
public class ItemResource extends BaseRestSimpleCustomizableMetadataResource<Item, ItemAttribute> {
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.addProperty("codes", Representation.REF);
		description.addProperty("department", Representation.REF);
		description.addProperty("hasExpiration");
		description.addProperty("defaultExpirationPeriod");
		description.addProperty("hasPhysicalInventory");
		description.addProperty("minimumQuantity");

		if (!(rep instanceof RefRepresentation)) {
			description.addProperty("prices", Representation.REF);
			description.addProperty("concept", Representation.REF);
			description.addProperty("buyingPrice");
		}

		description.addProperty("defaultPrice", Representation.REF);
		return description;
	}

	@PropertySetter(value = "codes")
	public void setItemCodes(Item instance, Set<ItemCode> codes) {
		if (instance.getCodes() == null) {
			instance.setCodes(new HashSet<ItemCode>());
		}

		BaseRestDataResource.syncCollection(instance.getCodes(), codes);
		for (ItemCode code : instance.getCodes()) {
			code.setItem(instance);
		}
	}

	@PropertySetter(value = "prices")
	public void setItemPrices(Item instance, Set<ItemPrice> prices) {
		if (instance.getPrices() == null) {
			instance.setPrices(new HashSet<ItemPrice>());
		}

		BaseRestDataResource.syncCollection(instance.getPrices(), prices);
		for (ItemPrice price : instance.getPrices()) {
			price.setItem(instance);
		}
	}

	@PropertySetter(value = "defaultPrice")
	public void setDefaultPrice(Item instance, ItemPrice defaultPrice) {
		IItemDataService service = Context.getService(IItemDataService.class);
		ItemPrice dataBaseItemPrice = service.getItemPriceByUuid(defaultPrice.getUuid());
		if (dataBaseItemPrice != null) {
			instance.setDefaultPrice(dataBaseItemPrice);
			return;
		}
		instance.setDefaultPrice(defaultPrice);
		setNewDefaultPrice(instance, defaultPrice.getPrice().toPlainString(), defaultPrice.getName());
	}

	@PropertySetter(value = "concept")
	public void setConcept(Item instance, final String uuid) {
		if (StringUtils.isBlank(uuid)) {
			instance.setConcept(null);
			return;
		}

		if (instance.getConcept() != null && uuid.equals(instance.getConcept().getUuid())) {
			return;
		}

		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConceptByUuid(uuid);
		instance.setConcept(concept);
	}

	@PropertySetter(value = "buyingPrice")
	public void setPrice(Item instance, Object price) {
		if (price == null || price.equals("")) {
			instance.setBuyingPrice(null);
		} else {
			instance.setBuyingPrice(Converter.objectToBigDecimal(price));
		}
	}

	@PropertySetter("attributes")
	public void setAttributes(Item instance, List<ItemAttribute> attributes) {
		super.baseSetAttributes(instance, attributes);
	}

	@Override
	public Item newDelegate() {
		return new Item();
	}

	@Override
	public Class<? extends IMetadataDataService<Item>> getServiceClass() {
		return IItemDataService.class;
	}

	private void setNewDefaultPrice(Item instance, final String price, final String name) {
		Collection<ItemPrice> results = Collections2.filter(instance.getPrices(), new Predicate<ItemPrice>() {
			@Override
			public boolean apply(@Nullable ItemPrice itemPrice) {
				if (itemPrice != null) {
					String itemPriceName = itemPrice.getName();
					if (itemPrice.getPrice().toPlainString().equals(price) && namesEqualOrBlank(itemPriceName, name)) {
						return true;
					}
				}

				return false;
			}

		});

		if (results != null && results.size() > 0) {
			instance.setDefaultPrice(Iterables.getFirst(results, null));
		} else {
			// If there are no matches in the current price set, save the price in a new ItemPrice to hopefully be
			// updated later, in case we haven't set new prices yet.
			instance.setDefaultPrice(new ItemPrice(new BigDecimal(price), ""));
		}
	}

	private boolean namesEqualOrBlank(final String name1, final String name2) {
		if (StringUtils.isBlank(name1) && StringUtils.isBlank(name2)) {
			return true;
		}
		if (StringUtils.equals(name1, name2)) {
			return true;
		}
		return false;
	}
}
