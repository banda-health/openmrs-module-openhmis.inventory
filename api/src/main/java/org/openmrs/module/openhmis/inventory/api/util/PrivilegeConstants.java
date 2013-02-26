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
package org.openmrs.module.openhmis.inventory.api.util;

import org.openmrs.annotation.AddOnStartup;

public class PrivilegeConstants {
	@AddOnStartup(description = "Able to add/edit/delete inventory items")
	public static final String MANAGE_ITEMS = "Manage Inventory Items";

	@AddOnStartup(description = "Able to view inventory items")
	public static final String VIEW_ITEMS = "View Inventory Items";

	public static final String PURGE_ITEMS = "Purge Inventory Items";

	@AddOnStartup(description = "Able to add/edit/delete inventory module metadata")
	public static final String MANAGE_METADATA = "Manage Inventory Metadata";

	@AddOnStartup(description = "Able to view inventory module metadata")
	public static final String VIEW_METADATA = "View Inventory Metadata";

	public static final String PURGE_METADATA = "Purge Inventory Metadata";
}
