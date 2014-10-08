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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.commons.api.util.IdgenUtil;
import org.openmrs.module.openhmis.inventory.ModuleSettings;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationService;
import org.openmrs.module.openhmis.inventory.api.model.IStockOperationType;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationAttribute;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationAttributeType;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.springframework.web.client.RestClientException;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Resource(name = ModuleRestConstants.OPERATION_RESOURCE, supportedClass=StockOperation.class,
		supportedOpenmrsVersions={"1.9.*", "1.10.*"})
public class StockOperationResource
		extends BaseRestCustomizableInstanceMetadataResource<StockOperation, IStockOperationType,
		StockOperationAttributeType, StockOperationAttribute> {

    private static final Log LOG = LogFactory.getLog(StockOperationResource.class);

	private IStockOperationService operationService;
	private AdministrationService administrationService;
	private boolean submitRequired = false;

	public StockOperationResource() {
		this.operationService = Context.getService(IStockOperationService.class);
		this.administrationService = Context.getAdministrationService();
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
		description.addProperty("status", Representation.DEFAULT);
		description.addProperty("operationNumber", Representation.DEFAULT);
		description.addProperty("dateCreated", Representation.DEFAULT);

		if (!(rep instanceof RefRepresentation)) {
			description.addProperty("items", Representation.REF);
			description.addProperty("reserved", Representation.REF);
			description.addProperty("transactions", Representation.REF);
			description.addProperty("source", Representation.REF);
			description.addProperty("destination", Representation.REF);
			description.addProperty("patient", Representation.REF);
			description.addProperty("institution", Representation.REF);

			description.addProperty("canProcess", findMethod("canUserProcess"));
		}

		return description;
	}

	public Boolean canUserProcess(StockOperation operation) {
		return StockOperationTypeResource.canUserProcess(operation.getInstanceType());
	}

	@Override
	public StockOperation save(StockOperation operation) {
		// Ensure that the current user can process the operation
		if (!canUserProcess(operation)) {
			throw new RestClientException("The current user not authorized to process this operation.");
		}

		StockOperation result;

		if (operation.getStatus() == StockOperationStatus.NEW || operation.getStatus() == StockOperationStatus.PENDING) {
			if (operation.getOperationDate() == null) {
				operation.setOperationDate(new Date());
			}
		}

		// If the status has changed, submit the operation
		if (submitRequired) {
			result = operationService.submitOperation(operation);
		} else {
			result = super.save(operation);
		}

		return result;
	}
	
	@PropertySetter("operationNumber")
	public void setOperationNumber(StockOperation instance, String operationNumber) {
		if (StringUtils.isEmpty(instance.getOperationNumber())) {
			if (ModuleSettings.generateOperationNumber()) {
				try {
					operationNumber = IdgenUtil.generateId(ModuleSettings.OPERATION_NUMBER_IDENTIFIER_SOURCE_ID_PROPERTY);

					instance.setOperationNumber(operationNumber);
				} catch (Exception ex) {
					LOG.error("Could not generate operation number: " + ex.getMessage());

					throw new IllegalStateException("The Operation Number was not defined and could not be generated.", ex);
				}
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
			submitRequired = true;

			operation.setStatus(status);
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
					operation.addItem(item); }
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

	@Override
	@PropertySetter("attributes")
	public void setAttributes(StockOperation instance, List<StockOperationAttribute> attributes) {
		super.setAttributes(instance, attributes);
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
			if (status != null) {
				result = getOperationsByStatus(context);
			} else {
				result = super.doSearch(context);
			}
		}

		return result;
	}

	protected PageableResult getUserOperations(RequestContext context) {
		User user = Context.getAuthenticatedUser();
		if (user == null) {
			log.warn("Could not retrieve the current user to be able to find the current user operations.");

			return  new EmptySearchResult();
		}

		StockOperationStatus status = getStatus(context);
		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);

		List<StockOperation> results;
		if (status == null) {
			results = ((IStockOperationDataService)getService()).getUserOperations(user, pagingInfo);
		} else {
			results = ((IStockOperationDataService)getService()).getUserOperations(user, status, pagingInfo);
		}

		return new AlreadyPagedWithLength<StockOperation>(context, results, pagingInfo.hasMoreResults(),
				pagingInfo.getTotalRecordCount());
	}

	protected PageableResult getOperationsByStatus(RequestContext context) {
		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
		StockOperationStatus status = getStatus(context);

		List<StockOperation> results;
		if (status == null) {
			results = getService().getAll(context.getIncludeAll(), pagingInfo);
		} else {
			StockOperationSearch search = new StockOperationSearch();
			search.getTemplate().setStatus(status);

			results = ((IStockOperationDataService)getService()).getOperations(search, pagingInfo);
		}

		return new AlreadyPagedWithLength<StockOperation>(context, results, pagingInfo.hasMoreResults(),
				pagingInfo.getTotalRecordCount());
	}

	protected StockOperationStatus getStatus(RequestContext context) {
		StockOperationStatus status = null;
		String statusText = context.getParameter("operation_status");
		if (!StringUtils.isEmpty(statusText)) {
			status = StockOperationStatus.valueOf(statusText.toUpperCase());

			if (status == null) {
				log.warn("Could not parse Stock Operation Status '" + statusText + "'");

				throw new IllegalArgumentException("The status '" + statusText + "' is not a valid operation status.");
			}
		}

		return status;
	}

	private void processItemStock(StockOperation operation, Set<StockOperationItem> items) {
		IStockOperationType type = operation.getInstanceType();

		// Process each operation item to set the appropriate fields
		for (StockOperationItem item : items) {
			Item sourceItem = item.getItem();

			// Set the calculated expiration flag for expirable items without an expiration
			if (sourceItem.hasExpiration()) {
				if (item.getExpiration() == null) {
					if (!type.getHasSource()) {
						// If the operation has no source then all expirable items must define an expiration
						throw new IllegalArgumentException("The expiration for item '" + item.getItem().getName() + "' must be" +
								" defined");
					} else {
						// No expiration was specified for an expirable item, flag as a calculated expiration
						item.setCalculatedExpiration(true);
					}
				} else {
					// An expiration was specified so make sure that the calculated flag is not set
					item.setCalculatedExpiration(false);
				}
			} else {
				// This is not an expirable item so set expire fields to null
				item.setExpiration(null);
				item.setCalculatedExpiration(null);
			}

			// Set the batch operation or calculated batch operation flag
			if (item.getBatchOperation() == null) {
				if (!type.getHasSource()) {
					// The batch operation is set to the current operation when item stock originally enters in the system
					item.setBatchOperation(operation);
					item.setCalculatedBatch(false);
				} else {
					// The batch operation was not set so flag it as calculated
					item.setCalculatedBatch(true);
				}
			}
		}
	}
}
