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
package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.Location;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.model.Department;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface that represents classes which perform data operations for {@link Department}s.
 */
@Transactional
public interface IDepartmentDataService extends IMetadataDataService<Department> {

	/**
	 * Gets all the departments for the specified {@link Location}.
	 * @param location The location.
	 * @param includeRetired Whether retired stockrooms should be included in the results.
	 * @return All stockrooms for the specified {@link Location}.
	 * @should throw NullPointerException if the location is null
	 * @should return an empty list if the location has no stockrooms
	 * @should not return retired stockrooms unless specified
	 * @should return all departments for the specified location
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	List<Department> getDepartmentsByLocation(Location location, boolean includeRetired);

	/**
	 * Gets all the departments for the specified {@link Location}.
	 * @param location The location.
	 * @param includeRetired Whether retired stockrooms should be included in the results.
	 * @param pagingInfo The paging information
	 * @return All departments for the specified {@link location}.
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	List<Department> getDepartmentsByLocation(Location location, boolean includeRetired,
	        PagingInfo pagingInfo);
}
