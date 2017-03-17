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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.Utility;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationTypeDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.model.IStockOperationType;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationAttribute;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationTemplate;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.helper.IdgenHelper;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.springframework.web.client.RestClientException;

/**
 * REST resource representing a {@link StockOperation}.
 */
@Resource(name = ModuleRestConstants.OPERATION_RESOURCE, supportedClass = StockOperation.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*" })
public class StockOperationResource
        extends BaseRestInstanceCustomizableMetadataResource<StockOperation, IStockOperationType, StockOperationAttribute> {
	private static final Log LOG = LogFactory.getLog(StockOperationResource.class);

	private IStockOperationService operationService;
	private IStockOperationTypeDataService stockOperationTypeDataService;
	private IStockroomDataService stockroomDataService;
	private IItemDataService itemDataService;
	private boolean submitRequired = false;
	private boolean rollbackRequired = false;

	public StockOperationResource() {
		this.operationService = Context.getService(IStockOperationService.class);
		this.stockOperationTypeDataService = Context.getService(IStockOperationTypeDataService.class);
		this.stockroomDataService = Context.getService(IStockroomDataService.class);
		this.itemDataService = Context.getService(IItemDataService.class);
	}

	@Override
	public StockOperation newDelegate() {
		return new StockOperation();
	}

	@Override
	public Class<? extends IMetadataDataService<StockOperation>> getServiceClass() {
		return IStockOperationDataService.class;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.addProperty("instanceType", Representation.DEFAULT);
		description.addProperty("status", Representation.DEFAULT);
		description.addProperty("operationNumber", Representation.DEFAULT);
		description.addProperty("dateCreated", Representation.DEFAULT);
		description.addProperty("operationDate", Representation.DEFAULT);
		description.addProperty("operationOrder", Representation.DEFAULT);
		description.addProperty("cancelReason", Representation.DEFAULT);

		if (!(rep instanceof RefRepresentation)) {
			description.addProperty("source", Representation.REF);
			description.addProperty("destination", Representation.REF);
			description.addProperty("patient", Representation.REF);
			description.addProperty("institution", Representation.REF);
			description.addProperty("department", Representation.REF);
			description.addProperty("creator", Representation.DEFAULT);

			description.addProperty("canProcess", findMethod("userCanProcess"));
			description.addProperty("canRollback", findMethod("userCanRollback"));
		}

		return description;
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = super.getCreatableProperties();
		description.addProperty("items", Representation.REF);

		return description;
	}

	public Boolean userCanProcess(StockOperation operation) {
		return StockOperationTypeResource.userCanProcess(operation.getInstanceType());
	}

	public Boolean userCanRollback(StockOperation operation) {
		return Context.hasPrivilege(PrivilegeConstants.ROLLBACK_OPERATIONS);
	}

	@Override
	public StockOperation save(StockOperation operation) {
		// Ensure that the current user can process the operation
		if (!userCanProcess(operation)) {
			throw new RestClientException("The current user not authorized to process this operation.");
		}

		StockOperation result;

		// If the status has changed, submit the operation
		try {
			if (submitRequired) {
				result = operationService.submitOperation(operation);
			} else if (rollbackRequired) {
				if (!userCanRollback(operation)) {
					throw new RestClientException("The current user not authorized to rollback this operation.");
				}

				result = operationService.rollbackOperation(operation);
			} else {
				result = super.save(operation);
			}
		} finally {
			submitRequired = false;
			rollbackRequired = false;
		}

		return result;
	}

	@PropertySetter("operationNumber")
	public void setOperationNumber(StockOperation instance, String operationNumber) {
		if (StringUtils.isEmpty(instance.getOperationNumber())) {
			if (IdgenHelper.isOperationNumberGenerated()) {
				operationNumber = IdgenHelper.generateId();
				instance.setOperationNumber(operationNumber);
			} else if (StringUtils.isEmpty(operationNumber)) {
				LOG.error("Operation Number not defined or generated.");

				throw new IllegalStateException("The Operation Number was not defined and no generator was configured.");
			} else {
				instance.setOperationNumber(operationNumber);
			}
		} else if (!StringUtils.isEmpty(operationNumber)) {
			instance.setOperationNumber(operationNumber);
		} else {
			LOG.error("Operation Number not defined or generated.");

			throw new IllegalStateException("The Operation Number was not defined and no generator was configured.");
		}
	}

	@PropertySetter("status")
	public void setStatus(StockOperation operation, StockOperationStatus status) {
		if (operation.getStatus() != status) {
			if (status == StockOperationStatus.ROLLBACK) {
				rollbackRequired = true;
			} else {
				submitRequired = true;

				operation.setStatus(status);
			}
		}
	}

	@PropertySetter(value = "items")
	public void setItems(final StockOperation operation, Set<StockOperationItem> items) {
		if (operation.getItems() == null) {
			operation.setItems(new HashSet<StockOperationItem>(items.size()));
		}

		processItemStock(operation, items);

		BaseRestDataResource.syncCollection(operation.getItems(), items,
		    new Action2<Collection<StockOperationItem>, StockOperationItem>() {
			    @Override
			    public void apply(Collection<StockOperationItem> collection, StockOperationItem item) {
				    operation.addItem(item);
			    }
		    },
		    new Action2<Collection<StockOperationItem>, StockOperationItem>() {
			    @Override
			    public void apply(Collection<StockOperationItem> collection, StockOperationItem item) {
				    operation.removeItem(item);
			    }
		    });
	}

	@PropertySetter("instanceType")
	public void setInstanceType(StockOperation instance, IStockOperationType instanceType) {
		instance.setInstanceType(instanceType);
	}

	@PropertySetter("attributes")
	public void setAttributes(StockOperation instance, List<StockOperationAttribute> attributes) {
		super.baseSetAttributes(instance, attributes);
	}

	@PropertySetter("operationDate")
	public void setOperationDate(StockOperation instance, String dateText) {
		Date date = Utility.parseOpenhmisDateString(dateText);
		if (date == null) {
			throw new IllegalArgumentException("Could not parse '" + dateText + "' as a date.");
		}

		instance.setOperationDate(date);
	}

	@Override
	protected PageableResult doSearch(RequestContext context) {
		PageableResult result;

		// TODO: Research if there is a better (more standard) way to do this.

		// Check to see if this search is for 'my', which we're hardcoding to return the list for the current user
		String query = context.getParameter("q");
		if (query != null && query.equals("my")) {
			result = getUserOperations(context);
		} else {
			String status = context.getParameter("operation_status");
			String operationTypeUuid = context.getParameter("operationType_uuid");
			String stockroomUuid = context.getParameter("stockroom_uuid");
			String operationItemUuid = context.getParameter("operationItem_uuid");

			if (status != null || operationTypeUuid != null || stockroomUuid != null || operationItemUuid != null) {
				result = getOperationsByContextParams(context);
			} else {
				result = super.doSearch(context);
			}
		}

		return result;
	}

	protected PageableResult getUserOperations(RequestContext context) {
		User user = Context.getAuthenticatedUser();
		if (user == null) {
			LOG.warn("Could not retrieve the current user to be able to find the current user operations.");

			return new EmptySearchResult();
		}

		StockOperationStatus status = getStatus(context);
		IStockOperationType stockOperationType = getStockOperationType(context);
		Item item = getItem(context);
		Stockroom stockroom = getStockroom(context);
		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);

		List<StockOperation> results;
		if (status == null && stockOperationType == null && item == null && stockroom == null) {
			results = ((IStockOperationDataService)getService()).getUserOperations(user, pagingInfo);
		} else {
			results =
			        ((IStockOperationDataService)getService()).getUserOperations(user, status, stockOperationType, item,
			            stockroom, pagingInfo);
		}

		return new AlreadyPagedWithLength<StockOperation>(context, results, pagingInfo.hasMoreResults(),
		        pagingInfo.getTotalRecordCount());
	}

	protected PageableResult getOperationsByContextParams(RequestContext context) {
		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
		StockOperationStatus status = getStatus(context);
		IStockOperationType stockOperationType = getStockOperationType(context);
		Stockroom stockroom = getStockroom(context);
		Item item = getItem(context);

		List<StockOperation> results;
		if (status == null && stockOperationType == null && item == null) {
			results = getService().getAll(context.getIncludeAll(), pagingInfo);
		} else {
			StockOperationSearch search = new StockOperationSearch();
			StockOperationTemplate template = search.getTemplate();
			if (status != null) {
				template.setStatus(status);
			}
			if (stockOperationType != null) {
				template.setInstanceType(stockOperationType);
			}
			if (stockroom != null) {
				template.setStockroom(stockroom);
			}
			if (item != null) {
				template.setItem(item);
			}

			results = ((IStockOperationDataService)getService()).getOperations(search, pagingInfo);
		}

		return new AlreadyPagedWithLength<StockOperation>(context, results, pagingInfo.hasMoreResults(),
		        pagingInfo.getTotalRecordCount());
	}

	protected StockOperationStatus getStatus(RequestContext context) {
		StockOperationStatus status = null;
		String statusText = context.getParameter("operation_status");
		if (StringUtils.isNotEmpty(statusText)) {
			status = StockOperationStatus.valueOf(statusText.toUpperCase());

			if (status == null) {
				LOG.warn("Could not parse Stock Operation Status '" + statusText + "'");
				throw new IllegalArgumentException("The status '" + statusText + "' is not a valid operation status.");
			}
		}

		return status;
	}

	private IStockOperationType getStockOperationType(RequestContext context) {
		IStockOperationType stockOperationType = null;
		String stockOperationTypeUuid = context.getParameter("operationType_uuid");
		if (StringUtils.isNotEmpty(stockOperationTypeUuid)) {
			stockOperationType = stockOperationTypeDataService.getByUuid(stockOperationTypeUuid);
			if (stockOperationType == null) {
				LOG.warn("Could not parse Stock Operation Type '" + stockOperationTypeUuid + "'");
				throw new IllegalArgumentException("The type '" + stockOperationTypeUuid
				        + "' is not a valid operation type.");
			}
		}

		return stockOperationType;
	}

	private Stockroom getStockroom(RequestContext context) {
		Stockroom stockroom = null;
		String stockroomUuid = context.getParameter("stockroom_uuid");
		if (StringUtils.isNotEmpty(stockroomUuid)) {
			stockroom = stockroomDataService.getByUuid(stockroomUuid);

			if (stockroom == null) {
				LOG.warn("Could not parse Stockroom '" + stockroomUuid + "'");
				throw new IllegalArgumentException("The stockroom '" + stockroomUuid + "' is not a valid stockroom.");
			}
		}

		return stockroom;
	}

	private Item getItem(RequestContext context) {
		Item item = null;
		String stockOperationItemUuid = context.getParameter("operationItem_uuid");
		if (StringUtils.isNotEmpty(stockOperationItemUuid)) {
			item = itemDataService.getByUuid(stockOperationItemUuid);
			if (item == null) {
				LOG.warn("Could not parse Item '" + stockOperationItemUuid + "'");
				throw new IllegalArgumentException("The item '" + stockOperationItemUuid
				        + "' is not a valid operation type.");
			}
		}
		return item;
	}

	private void processItemStock(StockOperation operation, Set<StockOperationItem> items) {
		IStockOperationType type = operation.getInstanceType();

		// Process each operation item to set the appropriate fields
		for (StockOperationItem opItem : items) {
			Item sourceItem = opItem.getItem();

			if (type.getHasSource()) {
				// If the operation has a source we will allow an expiration or null, regardless of whether the source
				// item is expirable or not

				// The code that saves the operation sets the appropriate fields based on whether a specific expiration,
				// Auto, or None was selected

				if (opItem.getExpiration() != null) {
					// A specific expiration was selected
					opItem.setCalculatedExpiration(false);
				}
			} else if (!type.getHasSource() && sourceItem.getHasExpiration()) {
				// This is new item stock so the expiration must be valid

				if (opItem.getExpiration() == null) {
					// If the operation has no source then all expirable items must define an expiration
					throw new IllegalArgumentException("The expiration for item '" + opItem.getItem().getName()
					        + "' must be defined");
				} else {
					// An expiration was specified so make sure that the calculated flag is not set
					opItem.setCalculatedExpiration(false);
				}
			}

			// Set the batch operation or calculated batch operation flag
			if (opItem.getBatchOperation() == null) {
				if (!type.getHasSource()) {
					// The batch operation is set to the current operation when item stock originally enters in the system
					opItem.setBatchOperation(operation);
					opItem.setCalculatedBatch(false);
				} else {
					// The batch operation was not set so flag it as calculated
					opItem.setCalculatedBatch(true);
				}
			}
		}
	}
}
