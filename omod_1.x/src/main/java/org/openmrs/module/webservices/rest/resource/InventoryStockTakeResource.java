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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationService;
import org.openmrs.module.openhmis.inventory.api.WellKnownOperationTypes;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockSummary;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.model.InventoryStockTake;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.helper.IdgenHelper;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.springframework.web.client.RestClientException;

/**
 * REST resource representing an {@link InventoryStockTake}.
 */
@Resource(name = ModuleRestConstants.INVENTORY_STOCK_TAKE_RESOURCE, supportedClass = InventoryStockTake.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*" })
public class InventoryStockTakeResource extends BaseRestObjectResource<InventoryStockTake> {

	private IStockOperationService operationService;

	public InventoryStockTakeResource() {
		this.operationService = Context.getService(IStockOperationService.class);
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.removeProperty("name");
		description.removeProperty("description");
		description.addProperty("operationNumber");
		description.addProperty("stockroom");
		description.addProperty("itemStockSummaryList");

		return description;
	}

	@Override
	public InventoryStockTake newDelegate() {
		return new InventoryStockTake();
	}

	public Boolean userCanProcessAdjustment() {
		return StockOperationTypeResource.userCanProcess(WellKnownOperationTypes.getAdjustment());
	}

	@Override
	public InventoryStockTake save(InventoryStockTake delegate) {
		// Ensure that the current user can process the operation
		if (!userCanProcessAdjustment()) {
			throw new RestClientException("The current user not authorized to process this operation.");
		}
		StockOperation operation = createOperation(delegate);
		operationService.submitOperation(operation);

		return newDelegate();
	}

	public StockOperation createOperation(InventoryStockTake delegate) {
		if (IdgenHelper.isOperationNumberGenerated()) {
			delegate.setOperationNumber(IdgenHelper.generateId());
		}
		StockOperation operation = new StockOperation();
		operation.setStatus(StockOperationStatus.NEW);
		operation.setInstanceType(WellKnownOperationTypes.getAdjustment());
		operation.setSource(delegate.getStockroom());
		operation.setOperationNumber(delegate.getOperationNumber());
		operation.setOperationDate(new Date());
		operation.setItems(createOperationsItemSet(operation, delegate.getItemStockSummaryList()));

		return operation;
	}

	private Set<StockOperationItem> createOperationsItemSet(StockOperation operation,
	        List<ItemStockSummary> inventoryStockTakeList) {
		Set<StockOperationItem> items = new HashSet<StockOperationItem>();
		for (ItemStockSummary invitem : inventoryStockTakeList) {
			StockOperationItem item = new StockOperationItem();
			item.setOperation(operation);
			item.setItem(invitem.getItem());
			item.setExpiration(invitem.getExpiration());
			item.setCalculatedExpiration(false);

			int quantity = invitem.getActualQuantity() - invitem.getQuantity();
			item.setQuantity(quantity);

			if (quantity < 0 || invitem.getActualQuantity() == 0) {
				item.setCalculatedBatch(true);
				item.setBatchOperation(null);
			} else {
				item.setCalculatedBatch(true);
			}
			items.add(item);
		}

		return items;
	}

	@Override
	public Class<? extends IObjectDataService<InventoryStockTake>> getServiceClass() {
		return null;
	}
}
