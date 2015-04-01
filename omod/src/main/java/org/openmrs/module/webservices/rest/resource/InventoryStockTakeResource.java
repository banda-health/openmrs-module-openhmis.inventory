package org.openmrs.module.webservices.rest.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.WellKnownOperationTypes;
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.springframework.web.client.RestClientException;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Resource(name = ModuleRestConstants.INVENTORY_STOCK_TAKE_RESOURCE, supportedClass = InventoryStockTake.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*" })
public class InventoryStockTakeResource extends BaseRestMetadataResource<InventoryStockTake> {
	
	private IStockOperationService operationService;
	private IStockroomDataService stockroomDataService;
	
	public InventoryStockTakeResource() {
		this.stockroomDataService = Context.getService(IStockroomDataService.class);
		this.operationService = Context.getService(IStockOperationService.class);
	}
	
	@Override
	public Class<? extends IMetadataDataService<InventoryStockTake>> getServiceClass() {
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.removeProperty("name");
		description.removeProperty("description");
		description.addProperty("operationNumber");
		description.addProperty("inventoryStockTakeList");
		
		return description;
	}
	
	public Boolean userCanProcess(StockOperation operation) {
		return StockOperationTypeResource.userCanProcess(operation.getInstanceType());
	}
	
	@Override
	public InventoryStockTake newDelegate() {
		return new InventoryStockTake();
	}
	
	@Override
	public InventoryStockTake save(InventoryStockTake delegate) {
		StockOperation operation = new StockOperation();
		operation = createOperation(operation, delegate);
		// Ensure that the current user can process the operation
		if (!userCanProcess(operation)) {
			throw new RestClientException("The current user not authorized to process this operation.");
		}
		InventoryStockTake inventoryStockTake = newDelegate();
		operationService.submitOperation(operation);
		return inventoryStockTake;
	}
	
	private StockOperation createOperation(StockOperation operation, InventoryStockTake delegate) {
		operation.setStatus(StockOperationStatus.NEW);
		IStockOperationType operationType = WellKnownOperationTypes.getAdjustment();
		operation.setInstanceType(operationType);
		Stockroom stockroom;
		stockroom = stockroomDataService.getByUuid(delegate.getInventoryStockTakeList().get(0).getStockroom().getUuid());
		operation.setSource(stockroom);
		operation.setOperationNumber(delegate.getOperationNumber());
		Date dNow = new Date();
		operation.setOperationDate(dNow);
		operation.setItems(createOperationsItemSet(operation, delegate.getInventoryStockTakeList()));
		return operation;
	}
	
	private Set<StockOperationItem> createOperationsItemSet(StockOperation operation,
	        List<InventoryStockTakeEntity> stockTakeEntityList) {
		Set<StockOperationItem> items = new HashSet<StockOperationItem>();
		for (InventoryStockTakeEntity invitem : stockTakeEntityList) {
			StockOperationItem item = new StockOperationItem();
			item.setOperation(operation);
			item.setItem(invitem.getItem());
			item.setCalculatedExpiration(invitem.getCalculatedExpiration());
			item.setExpiration(invitem.getExpiration());
			if (invitem.getBatchOperation() == null) {
				invitem.setBatchOperation(operation);
				item.setBatchOperation(invitem.getBatchOperation());
			}
			int actualQuantity = invitem.getActualQuantity() - invitem.getQuantity();
			item.setQuantity(actualQuantity);
			item.setBatchOperation(operation);
			items.add(item);
		}
		return items;
	}
	
}
