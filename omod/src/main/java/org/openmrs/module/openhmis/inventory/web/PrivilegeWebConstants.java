package org.openmrs.module.openhmis.inventory.web;

import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;

public class PrivilegeWebConstants extends PrivilegeConstants {
	public static final String ITEM_PAGE_PRIVILEGES = MANAGE_ITEMS + ", " + VIEW_ITEMS;
	public static final String DEPARTMENT_PAGE_PRIVILEGES = MANAGE_METADATA + ", " + VIEW_METADATA;
	public static final String CATEGORY_PAGE_PRIVILEGES = MANAGE_METADATA + ", " + VIEW_METADATA;
	public static final String STOCKROOM_PAGE_PRIVILEGES = MANAGE_STOCKROOMS + ", " + VIEW_STOCKROOMS;
	public static final String INSTITUTION_PAGE_PRIVILEGES = MANAGE_METADATA + ", " + VIEW_METADATA;
	public static final String OPERATION_TYPES_PAGE_PRIVILEGES = MANAGE_METADATA + ", " + VIEW_METADATA;
	public static final String MY_OPERATIONS_PAGE_PRIVILEGES = VIEW_OPERATIONS;

	protected PrivilegeWebConstants() { }
}
