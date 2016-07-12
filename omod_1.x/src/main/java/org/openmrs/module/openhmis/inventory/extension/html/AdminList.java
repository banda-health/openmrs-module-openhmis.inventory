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
package org.openmrs.module.openhmis.inventory.extension.html;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.openhmis.commons.api.util.PrivilegeUtil;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/**
 * Class that defines the inventory module administration links.
 */
public class AdminList extends AdministrationSectionExt {
	/**
	 * @see AdministrationSectionExt#getMediaType()
	 */
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}

	/**
	 * @see AdministrationSectionExt#getTitle()
	 */
	public String getTitle() {
		return "openhmis.inventory.title";
	}

	@Override
	public String getRequiredPrivilege() {
		return PrivilegeWebConstants.INVENTORY_PAGE_PRIVILEGES;
	}

	/**
	 * @see AdministrationSectionExt#getLinks()
	 */
	public Map<String, String> getLinks() {
		User authenticatedUser = Context.getAuthenticatedUser();
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		Boolean rolePrivilege = authenticatedUser.hasPrivilege(org.openmrs.util.PrivilegeConstants.MANAGE_ROLES);
		if (rolePrivilege) {
			map.put(ModuleWebConstants.ROLE_CREATION_PAGE, "openhmis.inventory.admin.role");
		}

		Boolean itemPrivilege = PrivilegeUtil.hasPrivileges(authenticatedUser, PrivilegeWebConstants.ITEM_PAGE_PRIVILEGES);
		if (itemPrivilege) {
			map.put(ModuleWebConstants.ITEMS_PAGE, "openhmis.inventory.admin.items");
		}

		if (PrivilegeUtil.hasPrivileges(authenticatedUser, PrivilegeWebConstants.ITEM_ATTRIBUTE_TYPE_PAGE_PRIVILEGES)) {
			map.put(ModuleWebConstants.ITEM_ATTRIBUTE_TYPES_PAGE, "openhmis.inventory.admin.item.attribute.types");
		}

		if (PrivilegeUtil.hasPrivileges(authenticatedUser, PrivilegeWebConstants.DEPARTMENT_PAGE_PRIVILEGES)) {
			map.put(ModuleWebConstants.DEPARTMENTS_PAGE, "openhmis.inventory.admin.departments");
		}

		if (PrivilegeUtil.hasPrivileges(authenticatedUser, PrivilegeWebConstants.INSTITUTION_PAGE_PRIVILEGES)) {
			map.put(ModuleWebConstants.INSTITUTIONS_PAGE, "openhmis.inventory.admin.institutions");
		}

		Boolean stockroomPrivilege = PrivilegeUtil.hasPrivileges(authenticatedUser,
		    PrivilegeWebConstants.STOCKROOM_PAGE_PRIVILEGES);
		if (stockroomPrivilege) {
			map.put(ModuleWebConstants.STOCKROOMS_PAGE, "openhmis.inventory.admin.stockrooms");
			map.put(ModuleWebConstants.OPERATION_TYPES_PAGE, "openhmis.inventory.admin.operationTypes");
		}

		// These links are put down here so that the links layout a little nicer
		if (stockroomPrivilege) {
			map.put(ModuleWebConstants.OPERATIONS_PAGE, "openhmis.inventory.admin.operations");
		}
		if (itemPrivilege) {
			map.put(ModuleWebConstants.ITEM_CONCEPT_SUGGESTION_PAGE, "openhmis.inventory.admin.items.concept.mapping");
		}

		if (PrivilegeUtil.hasPrivileges(authenticatedUser, PrivilegeWebConstants.SETTINGS_PAGE_PRIVILEGES)) {
			map.put(ModuleWebConstants.SETTINGS_PAGE, "openhmis.inventory.admin.settings");
		}

		return map;
	}
}
