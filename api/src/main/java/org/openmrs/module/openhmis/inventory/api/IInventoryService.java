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
package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.inventory.api.model.Department;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IInventoryService extends OpenmrsService {
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_ITEMS})
	List<Item> findItems(Department department, String nameFragment, boolean includeRetired) throws APIException;

	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_ITEMS})
	List<Item> findItems(Department department, String nameFragment, boolean includeRetired, PagingInfo pagingInfo) throws APIException;

	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_ITEMS})
	List<Item> findItems(ItemCategory category, String nameFragment, boolean includeRetired) throws APIException;

	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_ITEMS})
	List<Item> findItems(ItemCategory category, String nameFragment, boolean includeRetired, PagingInfo pagingInfo) throws APIException;

	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_METADATA})
	List<Department> getDepartments(boolean includeRetired);


}