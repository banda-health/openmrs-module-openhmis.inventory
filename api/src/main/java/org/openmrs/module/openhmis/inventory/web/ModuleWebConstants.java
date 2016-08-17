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
package org.openmrs.module.openhmis.inventory.web;

import org.openmrs.module.openhmis.commons.web.WebConstants;
import org.openmrs.module.openhmis.inventory.api.util.ModuleConstants;

/**
 * Constants class for the module web resources.
 */
public class ModuleWebConstants extends WebConstants {
	public static final String MODULE_ROOT = WebConstants.MODULE_BASE + ModuleConstants.MODULE_NAME + "/";
	public static final String MODULE_RESOURCE_ROOT = WebConstants.MODULE_RESOURCE_BASE + ModuleConstants.MODULE_NAME + "/";

	public static final String MODULE_COMMONS_RESOURCE_ROOT = "/moduleResources/openhmis/commons/";
	public static final String MODULE_COMMON_TEMPLATE_URL = "/WEB-INF/view/module/openhmis/commons/template/common/";

	public static final String ROLE_CREATION_ROOT = MODULE_ROOT + "roleCreation";
	public static final String ROLE_CREATION_PAGE = ROLE_CREATION_ROOT + ".form";

	public static final String ROLE_CREATION_2X_ROOT = MODULE_ROOT + "roleCreation2x";
	public static final String ROLE_CREATION_2X_PAGE = ROLE_CREATION_2X_ROOT + ".page";

	public static final String ITEMS_ROOT = MODULE_ROOT + "items";
	public static final String ITEMS_PAGE = ITEMS_ROOT + ".form";

	public static final String ITEM_ATTRIBUTE_TYPES_ROOT = MODULE_ROOT + "itemAttributeTypes";
	public static final String ITEM_ATTRIBUTE_TYPES_PAGE = ITEM_ATTRIBUTE_TYPES_ROOT + ".form";

	public static final String ITEM_CONCEPT_SUGGESTION_ROOT = MODULE_ROOT + "itemConceptSuggestion";
	public static final String ITEM_CONCEPT_SUGGESTION_PAGE = ITEM_CONCEPT_SUGGESTION_ROOT + ".form";

	public static final String DEPARTMENTS_ROOT = MODULE_ROOT + "departments";
	public static final String DEPARTMENTS_PAGE = DEPARTMENTS_ROOT + ".form";

	public static final String STOCKROOMS_ROOT = MODULE_ROOT + "stockrooms";
	public static final String STOCKROOMS_PAGE = STOCKROOMS_ROOT + ".form";

	public static final String INSTITUTIONS_ROOT = MODULE_ROOT + "institutions";
	public static final String INSTITUTIONS_PAGE = INSTITUTIONS_ROOT + ".form";

	public static final String OPERATION_TYPES_ROOT = MODULE_ROOT + "operationTypes";
	public static final String OPERATION_TYPES_PAGE = OPERATION_TYPES_ROOT + ".form";

	public static final String OPERATIONS_ROOT = MODULE_ROOT + "operations";
	public static final String OPERATIONS_PAGE = OPERATIONS_ROOT + ".form";

	public static final String MODULE_INIT_JS = MODULE_RESOURCE_ROOT + "js/init.js";

	public static final String INVENTORY_ROOT = MODULE_ROOT + "inventory";
	public static final String INVENTORY_PAGE = INVENTORY_ROOT + ".form";

	public static final String INVENTORY_CREATION_ROOT = MODULE_ROOT + "inventoryCreate";
	public static final String INVENTORY_CREATION_PAGE = INVENTORY_CREATION_ROOT + ".form";

	public static final String INVENTORY_STOCK_TAKE_ROOT = MODULE_ROOT + "inventoryStockTake";
	public static final String INVENTORY_STOCK_TAKE_PAGE = INVENTORY_STOCK_TAKE_ROOT + ".form";

	public static final String INVENTORY_REPORTS_ROOT = MODULE_ROOT + "inventoryReports";
	public static final String INVENTORY_REPORTS_PAGE = INVENTORY_REPORTS_ROOT + ".form";

	public static final String SETTINGS_ROOT = MODULE_ROOT + "settings";
	public static final String SETTINGS_PAGE = SETTINGS_ROOT + ".form";

	public static final String SETTINGS_2X_ROOT = MODULE_ROOT + "settings2x";
	public static final String SETTINGS_2X_PAGE = SETTINGS_2X_ROOT + ".page";

	public static final String JASPER_REPORT_PAGE = MODULE_ROOT + "jasperReport";

	public static final String LANDING_PAGE_EXTENSION_POINT_ID = "org.openmrs.module.openhmis.inventory.landing";
	public static final String MANAGE_MODULE_PAGE_EXTENSION_POINT_ID =
	        "org.openmrs.module.openhmis.inventory.manage.module";
	public static final String TASKS_DASHBOARD_PAGE_EXTENSION_POINT_ID =
	        "org.openmrs.module.openhmis.inventory.tasks.dashboard";

	public static final String MESSAGE_PROPERTIES_JS_URI = MODULE_ROOT + "inventoryMessageProperties.js";
	public static final String MESSAGE_PAGE = MODULE_ROOT + "inventoryMessageProperties";
	public static final String MESSAGE_PROPERTIES_JS = "/ws/module/openhmis/inventory/inventoryMessageProperties.js";

	public static final String MODULE_SETTINGS_ROOT = MODULE_ROOT + "moduleSettings";

	protected ModuleWebConstants() {}
}
