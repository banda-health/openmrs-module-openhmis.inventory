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
package org.openmrs.module.openhmis.inventory.api.util;

import static org.openmrs.module.jasperreport.util.JasperReportPrivilegeConstants.VIEW_JASPER_REPORTS;

import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.compatibility.PrivilegeConstantsCompatibility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Constants class for module privilege constants.
 */
public class PrivilegeConstants {

	private static PrivilegeConstantsCompatibility privilegeConstantsCompatibility;

	public static final String MANAGE_ITEMS = "Manage Inventory Items";
	public static final String VIEW_ITEMS = "View Inventory Items";
	public static final String PURGE_ITEMS = "Purge Inventory Items";

	public static final String MANAGE_STOCKROOMS = "Manage Inventory Stockrooms";
	public static final String VIEW_STOCKROOMS = "View Inventory Stockrooms";
	public static final String PURGE_STOCKROOMS = "Purge Inventory Stockrooms";

	public static final String MANAGE_METADATA = "Manage Inventory Metadata";
	public static final String VIEW_METADATA = "View Inventory Metadata";
	public static final String PURGE_METADATA = "Purge Inventory Metadata";

	public static final String MANAGE_OPERATIONS = "Manage Inventory Operations";
	public static final String VIEW_OPERATIONS = "View Inventory Operations";
	public static final String ROLLBACK_OPERATIONS = "Rollback Inventory Operations";

	public static final String APP_VIEW_INVENTORY_APP = "App: View Inventory App";
	public static final String APP_ACCESS_INVENTORY_TASKS_PAGE = "App: Access Inventory Tasks";
	public static final String TASK_MANAGE_INVENTORY_MODULE_PAGE = "Task: Manage Inventory Module";
	public static final String TASK_ACCESS_INVENTORY_TASK_PAGE = "Task: Access Inventory Task";
	public static final String TASK_ACCESS_VIEW_STOCK_OPERATIONS_PAGE = "Task: View Stock Operations";
	public static final String TASK_ACCESS_CREATE_OPERATION_PAGE = "Task: Access Create Operation";
	public static final String TASK_ACCESS_INVENTORY_REPORTS_PAGE = "Task: Access Inventory Reports";
	public static final String TASK_ACCESS_STOCK_TAKE_PAGE = "Task: Access Stock Take";

	public static final String TASK_MANAGE_INVENTORY_METADATA = "Task: Manage Inventory Metadata";

	public static final String[] PRIVILEGE_NAMES = new String[] { MANAGE_ITEMS, VIEW_ITEMS, PURGE_ITEMS, MANAGE_STOCKROOMS,
	        VIEW_STOCKROOMS, PURGE_STOCKROOMS, MANAGE_OPERATIONS, VIEW_OPERATIONS, MANAGE_METADATA, VIEW_METADATA,
	        PURGE_METADATA, APP_VIEW_INVENTORY_APP, APP_ACCESS_INVENTORY_TASKS_PAGE,
	        TASK_MANAGE_INVENTORY_MODULE_PAGE, TASK_ACCESS_INVENTORY_TASK_PAGE, TASK_ACCESS_VIEW_STOCK_OPERATIONS_PAGE,
	        TASK_ACCESS_CREATE_OPERATION_PAGE, TASK_ACCESS_INVENTORY_REPORTS_PAGE, TASK_ACCESS_STOCK_TAKE_PAGE,
	        ROLLBACK_OPERATIONS, TASK_MANAGE_INVENTORY_METADATA };

	@Autowired
	protected PrivilegeConstants(PrivilegeConstantsCompatibility privilegeConstantsCompatibility) {
		PrivilegeConstants.privilegeConstantsCompatibility = privilegeConstantsCompatibility;
	}

	/**
	 * Gets all the privileges defined by the module.
	 * @return The module privileges.
	 */
	public static Set<Privilege> getModulePrivileges() {
		Set<Privilege> privileges = new HashSet<Privilege>(PRIVILEGE_NAMES.length);

		UserService service = Context.getUserService();
		if (service == null) {
			throw new IllegalStateException("The OpenMRS user service cannot be loaded.");
		}

		for (String name : PRIVILEGE_NAMES) {
			privileges.add(service.getPrivilege(name));
		}

		return privileges;
	}

	/**
	 * Gets the default privileges needed to fully use the module.
	 * @return A set containing the default set of privileges.
	 */
	public static Set<Privilege> getDefaultPrivileges() {
		Set<Privilege> privileges = getModulePrivileges();

		UserService service = Context.getUserService();
		if (service == null) {
			throw new IllegalStateException("The OpenMRS user service cannot be loaded.");
		}

		List<String> names = new ArrayList<String>();

		names.add(org.openmrs.util.PrivilegeConstants.EDIT_PATIENT_IDENTIFIERS);
		names.add(org.openmrs.util.PrivilegeConstants.VIEW_ADMIN_FUNCTIONS);
		names.add(privilegeConstantsCompatibility.GET_CONCEPTS);
		names.add(privilegeConstantsCompatibility.GET_LOCATIONS);
		names.add(org.openmrs.util.PrivilegeConstants.VIEW_NAVIGATION_MENU);
		names.add(privilegeConstantsCompatibility.GET_USERS);
		names.add(privilegeConstantsCompatibility.GET_ROLES);

		names.add(org.openmrs.util.PrivilegeConstants.MANAGE_GLOBAL_PROPERTIES);
		names.add(org.openmrs.util.PrivilegeConstants.MANAGE_ROLES);
		names.add(VIEW_JASPER_REPORTS);
		names.add(privilegeConstantsCompatibility.GET_VISITS);
		names.add(privilegeConstantsCompatibility.GET_PATIENTS);

		for (String name : names) {
			privileges.add(service.getPrivilege(name));
		}

		return privileges;
	}
}
