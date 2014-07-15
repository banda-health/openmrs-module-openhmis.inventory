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
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Resource(name = ModuleRestConstants.OPERATION_RESOURCE, supportedClass=StockOperation.class, supportedOpenmrsVersions={"1.9"})
public class StockOperationResource
		extends BaseRestCustomizableInstanceMetadataResource<StockOperation, IStockOperationType, StockOperationAttributeType, StockOperationAttribute> {

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
			description.addProperty("reserved", Representation.REF);
			description.addProperty("transactions", Representation.REF);
			description.addProperty("source", Representation.REF);
			description.addProperty("destination", Representation.REF);
			description.addProperty("patient", Representation.REF);
			description.addProperty("institution", Representation.REF);
		}

		return description;
	}

	@PropertySetter(value="reserved")
	public void setReserved(final StockOperation instance, Set<ReservedTransaction> reserved) {
		if (instance.getReserved() == null) {
			instance.setReserved(new TreeSet<ReservedTransaction>());
		}

		BaseRestDataResource.syncCollection(instance.getReserved(), reserved,
				new Action2<Collection<ReservedTransaction>, ReservedTransaction>() {
					@Override
					public void apply(Collection<ReservedTransaction> collection, ReservedTransaction reserved) {
						instance.addReserved(reserved);
					}
				},
				new Action2<Collection<ReservedTransaction>, ReservedTransaction>() {
					@Override
					public void apply(Collection<ReservedTransaction> collection, ReservedTransaction reserved) {
						instance.removeReserved(reserved);
					}
				});
	}

	@Override
	protected PageableResult doSearch(RequestContext context) {
		// TODO: Research if there is a better (more standard) way to do this.

		// Check to see if this search is for 'my', which we're hardcoding to return the list for the current user
		String query = context.getParameter("q");
		if (query != null && query.equals("my")) {
			return getUserOperations(context);
		} else {
			return super.doSearch(context);
		}
	}

	protected PageableResult getUserOperations(RequestContext context) {
		User user = Context.getAuthenticatedUser();
		if (user == null) {
			log.warn("Could not retrieve the current user to be able to find the current user operations.");

			return  new EmptySearchResult();
		}

		StockOperationStatus status = null;
		String statusText = context.getParameter("operation_status");
		if (!StringUtils.isEmpty(statusText)) {
			status = StockOperationStatus.valueOf(statusText.toUpperCase());

			if (status == null) {
				log.warn("Could not parse Stock Operation Status '" + statusText + "'");

				return new EmptySearchResult();
			}
		}

		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
		List<StockOperation> results = null;

		if (status == null) {
			results = ((IStockOperationDataService)getService()).getUserOperations(user, pagingInfo);
		} else {
			results = ((IStockOperationDataService)getService()).getUserOperations(user, status, pagingInfo);
		}


		return new AlreadyPagedWithLength<StockOperation>(context, results, pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
	}
}

