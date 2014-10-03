/*

 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
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
package org.openmrs.module.webservices.rest.resource;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.commons.api.exception.PrivilegeException;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemCode;
import org.openmrs.module.openhmis.inventory.api.model.ItemPrice;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

@Resource(name= ModuleRestConstants.ITEM_RESOURCE, supportedClass=Item.class, supportedOpenmrsVersions={"1.9"})
public class ItemResource extends BaseRestMetadataResource<Item> {
	
	private static final Log LOG = LogFactory.getLog(ItemResource.class);
	
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = super.getRepresentationDescription(rep);
        if (rep instanceof RefRepresentation) {
            description.addProperty("codes", Representation.REF);
            description.addProperty("department", Representation.REF);
            // TODO enable categories in v1.1
            //description.addProperty("category", Representation.REF);
            description.addProperty("defaultPrice", Representation.REF);
	        description.addProperty("hasExpiration");
	        description.addProperty("defaultExpirationPeriod");
	        description.addProperty("hasPhysicalInventory");
        } else {
            description.addProperty("name");
            description.addProperty("codes", Representation.REF);
            description.addProperty("prices", Representation.REF);
            description.addProperty("department", Representation.REF);
            // TODO enable categories in v1.1
            //description.addProperty("category", Representation.REF);
            description.addProperty("defaultPrice", Representation.REF);
            description.addProperty("concept", Representation.REF);
	        description.addProperty("hasExpiration");
	        description.addProperty("defaultExpirationPeriod");
	        description.addProperty("hasPhysicalInventory");
        }

        return description;
    }

    @PropertySetter(value="codes")
    public void setItemCodes(Item instance, Set<ItemCode> codes) {
        if (instance.getCodes() == null) {
            instance.setCodes(new HashSet<ItemCode>());
        }

        BaseRestDataResource.syncCollection(instance.getCodes(), codes);
        for (ItemCode code : instance.getCodes()) {
            code.setItem(instance);
        }
    }

    @PropertySetter(value="prices")
    public void setItemPrices(Item instance, Set<ItemPrice> prices) {
        if (instance.getPrices() == null) {
            instance.setPrices(new HashSet<ItemPrice>());
        }

        BaseRestDataResource.syncCollection(instance.getPrices(), prices);
        for (ItemPrice price : instance.getPrices()) {
            price.setItem(instance);
        }
    }

    @PropertySetter(value="concept")
    public void setConcept(Item instance, final String uuid) {
        if(StringUtils.isBlank(uuid)) {
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

    @Override
    public Item save(Item item) {
        checkDefaultPrice(item);
		return super.save(item);
    }
    
    @Override
    public void purge(Item item, RequestContext context) {
        try {
            super.purge(item, context);
        } catch (PrivilegeException ce) {
        	LOG.error("Exception occured when trying to purge item <" + item.getName() + ">", ce);
        	throw new ResponseException("Can't purge item with name <" +  item.getName() + "> as required privilege is missing") {
                private static final long serialVersionUID = 1L;
            };
        } catch(APIException e) {
            LOG.error("Exception occured when trying to purge item <" + item.getName() + ">", e);
            throw new ResponseException("Can't purge item with name <" +  item.getName() + "> as it is still in use") {
                private static final long serialVersionUID = 1L;
            };
        }
    }

    @Override
    public Item newDelegate() {
        return new Item();
    }

    @Override
    public Class<? extends IMetadataDataService<Item>> getServiceClass() {
        return IItemDataService.class;
    }

	private void checkDefaultPrice(Item item) {
		// Check that default price has been properly set now that the item's
		// prices have definitely been set
		if (!item.getPrices().contains(item.getDefaultPrice())) {
			if (item.getDefaultPrice().getId() == null) {
				setDefaultPrice(item, item.getDefaultPrice().getPrice().toString());
			}

			// If it's still not set to one of the item's prices, set it to the
			// first available price, or null.
			if (!item.getPrices().contains(item.getDefaultPrice())) {
				if (item.getPrices().size() > 0) {
					Set<ItemPrice> prices = item.getPrices();
					item.setDefaultPrice(prices.toArray(new ItemPrice[prices.size()])[0]);
				} else {
					item.setDefaultPrice(null);
				}
			}
		}
	}

	private void setDefaultPrice(Item instance, final String uuidOrPrice) {
		Collection<ItemPrice> results = Collections2.filter(instance.getPrices(), new Predicate<ItemPrice>() {
			@Override
			public boolean apply(@Nullable ItemPrice price) {
				if (price != null) {
					if (price.getUuid().equals(uuidOrPrice) || price.getPrice().toPlainString().equals(uuidOrPrice)) {
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
			instance.setDefaultPrice(new ItemPrice(new BigDecimal(uuidOrPrice), ""));
		}
	}
}
