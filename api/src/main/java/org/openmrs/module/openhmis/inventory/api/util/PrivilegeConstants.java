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

public class PrivilegeConstants {
	public static final String MANAGE_ITEMS = "Manage Inventory Items";
	public static final String VIEW_ITEMS = "View Inventory Items";
	public static final String PURGE_ITEMS = "Purge Inventory Items";

	public static final String MANAGE_STOCK_ROOMS = "Manage Inventory Stock Rooms";
	public static final String VIEW_STOCK_ROOMS = "View Inventory Stock Rooms";
	public static final String PURGE_STOCK_ROOMS = "Purge Inventory Stock Rooms";

	public static final String MANAGE_METADATA = "Manage Inventory Metadata";
	public static final String VIEW_METADATA = "View Inventory Metadata";
	public static final String PURGE_METADATA = "Purge Inventory Metadata";

	public static final String MANAGE_TRANSACTIONS = "Manage Inventory Transactions";
	public static final String VIEW_TRANSACTIONS = "View Inventory Transactions";
	public static final String PURGE_TRANSACTIONS = "Purge Inventory Transactions";

	public static final String ITEM_PAGE_PRIVILEGES = MANAGE_ITEMS + ", " + VIEW_ITEMS;
	public static final String DEPARTMENT_PAGE_PRIVILEGES = MANAGE_METADATA + ", " + VIEW_METADATA;
	public static final String CATEGORY_PAGE_PRIVILEGES = MANAGE_METADATA + ", " + VIEW_METADATA;
	public static final String STOCK_ROOM_PAGE_PRIVILEGES = MANAGE_STOCK_ROOMS + ", " + VIEW_STOCK_ROOMS;
	public static final String TRANSACTION_PAGE_PRIVILEGES = STOCK_ROOM_PAGE_PRIVILEGES;
}
