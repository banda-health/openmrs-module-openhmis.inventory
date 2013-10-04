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
package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.model.Department;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IItemDataService extends IMetadataDataService<Item> {
	/**
	 * Gets the {@link Item} with the specified code or {@code null} if not found.
	 * @param itemCode The item code to find.
	 * @return The {@link Item} or with the specified item code or {@code null}.
	 * @throws APIException
	 * @should throw IllegalArgumentException if the item code is null
	 * @should throw IllegalArgumentException if the item code is longer than 255 characters
	 * @should return the item with the specified item code
	 * @should return null if the item code is not found
	 */
	@Transactional(readOnly =  true)
	@Authorized( {PrivilegeConstants.VIEW_ITEMS})
	Item getItemByCode(String itemCode) throws APIException;

	/**
	 * Gets all the items for the specified {@link Department}.
	 * @param department The department.
	 * @param includeRetired Whether retired items should be included in the results.
	 * @return All items for the specified {@link Department}.
	 * @throws APIException
	 * @should throw NullPointerException if the department is null
	 * @should return an empty list if the department has no items
	 * @should not return retired items unless specified
	 * @should return all items for the specified department
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_ITEMS})
	List<Item> getItemsByDepartment(Department department, boolean includeRetired) throws APIException;

	/**
	 * Gets all the items for the specified {@link Department}.
	 * @param department The department.
	 * @param includeRetired Whether retired items should be included in the results.
	 * @param pagingInfo The paging information
	 * @return All items for the specified {@link Department}.
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_ITEMS})
	List<Item> getItemsByDepartment(Department department, boolean includeRetired, PagingInfo pagingInfo) throws APIException;

	/**
	 * Finds all items in the specified {@link Department} that start with the specified name.
	 * @param department The department to search within.
	 * @param name The item name fragment.
	 * @param includeRetired Whether retired items should be included in the results.
	 * @return All items in the specified {@link Department} that start with the specified name.
	 * @throws APIException
	 * @should throw NullPointerException if the department is null
	 * @should throw IllegalArgumentException if the name is null
	 * @should throw IllegalArgumentException if the name is empty
	 * @should throw IllegalArgumentException if the name is longer than 255 characters
	 * @should return an empty list if no items are found
	 * @should not return retired items unless specified
	 * @should return items that start with the specified name
	 * @should return items for only the specified department
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_ITEMS})
	List<Item> findItems(Department department, String name, boolean includeRetired) throws APIException;

	/**
	 * Finds all items in the specified {@link Department} that start with the specified name.
	 * @param department The department to search within.
	 * @param name The item name fragment.
	 * @param includeRetired Whether retired items should be included in the results.
	 * @param pagingInfo The paging information.
	 * @return All items in the specified {@link Department} that start with the specified name.
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_ITEMS})
	List<Item> findItems(Department department, String name, boolean includeRetired, PagingInfo pagingInfo) throws APIException;

	/**
	 * Finds all items using the specified {@link ItemSearch} settings.
	 * @param itemSearch The item search settings.
	 * @return The items found or an empty list if no items were found.
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_ITEMS})
	List<Item> findItems(ItemSearch itemSearch);

	/**
	 * Finds all items using the specified {@link ItemSearch} settings.
	 * @param itemSearch The item search settings.
	 * @param pagingInfo The paging information.
	 * @return The items found or an empty list if no items were found.
	 * @should throw NullPointerException if item search is null
	 * @should throw NullPointerException if item search template object is null
	 * @should return an empty list if no items are found via the search
	 * @should return items filtered by department
	 * @should return items filtered by category
	 * @should return items filtered by concept
	 * @should return items filtered by drug
	 * @should return all items if paging is null
	 * @should return paged items if paging is specified
	 * @should not return retired items from search unless specified
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_ITEMS})
	List<Item> findItems(ItemSearch itemSearch, PagingInfo pagingInfo);
}

