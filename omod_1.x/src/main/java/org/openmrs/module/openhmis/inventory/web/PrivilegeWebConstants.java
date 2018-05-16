/*
 * The contents of this file are subject to the OpenMRS Public License

import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;

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
package org.openmrs.module.openhmis.inventory.web;

import org.openmrs.module.openhmis.commons.api.compatibility.PrivilegeConstantsCompatibility;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Constants class for privilges required by web resources.
 */
public class PrivilegeWebConstants extends PrivilegeConstants {
	public static final String ROLE_CREATION_PAGE_PRIVILEGES = org.openmrs.util.PrivilegeConstants.MANAGE_ROLES;
	public static final String ITEM_PAGE_PRIVILEGES = MANAGE_ITEMS + ", " + VIEW_ITEMS;
	public static final String ITEM_ATTRIBUTE_TYPE_PAGE_PRIVILEGES = MANAGE_METADATA + ", " + VIEW_METADATA;
	public static final String DEPARTMENT_PAGE_PRIVILEGES = MANAGE_METADATA + ", " + VIEW_METADATA;
	public static final String STOCKROOM_PAGE_PRIVILEGES = MANAGE_STOCKROOMS + ", " + VIEW_STOCKROOMS;
	public static final String INSTITUTION_PAGE_PRIVILEGES = MANAGE_METADATA + ", " + VIEW_METADATA;
	public static final String OPERATION_TYPES_PAGE_PRIVILEGES = MANAGE_METADATA + ", " + VIEW_METADATA;
	public static final String OPERATIONS_PAGE_PRIVILEGES = VIEW_OPERATIONS;
	public static final String ITEM_CONCEPT_SUGGESTION_PAGE_PRIVILEGES = MANAGE_ITEMS + ", " + VIEW_ITEMS;
	public static final String INVENTORY_PAGE_PRIVILEGES = MANAGE_OPERATIONS + ", " + VIEW_OPERATIONS;
	public static final String SETTINGS_PAGE_PRIVILEGES = MANAGE_METADATA;

	@Autowired
	protected PrivilegeWebConstants(PrivilegeConstantsCompatibility privilegeConstantsCompatibility) {
		super(privilegeConstantsCompatibility);
	}
}
