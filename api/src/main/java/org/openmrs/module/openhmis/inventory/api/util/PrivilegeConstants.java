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
package org.openmrs.module.openhmis.inventory.api.util;

import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;

import java.util.HashSet;
import java.util.Set;

public class PrivilegeConstants {
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

	public static final String[] PRIVILEGE_NAMES = new String[] {
			MANAGE_ITEMS, VIEW_ITEMS, PURGE_ITEMS,
			MANAGE_STOCKROOMS, VIEW_STOCKROOMS, PURGE_STOCKROOMS,
			MANAGE_OPERATIONS, VIEW_OPERATIONS,
			MANAGE_METADATA, VIEW_METADATA, PURGE_METADATA
	};

	protected PrivilegeConstants() { }

	public static Set<Privilege> getPrivileges() {
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
}
