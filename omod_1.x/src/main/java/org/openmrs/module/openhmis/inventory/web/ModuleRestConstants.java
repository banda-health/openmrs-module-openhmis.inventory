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

import org.openmrs.module.webservices.rest.web.RestConstants;

/**
 * Constants class for REST urls.
 */
public class ModuleRestConstants extends ModuleWebConstants {
	public static final String MODULE_REST_ROOT = RestConstants.VERSION_2 + "/inventory/";

	public static final String ITEM_RESOURCE = MODULE_REST_ROOT + "item";
	public static final String ITEM_PRICE_RESOURCE = MODULE_REST_ROOT + "itemPrice";
	public static final String ITEM_CODE_RESOURCE = MODULE_REST_ROOT + "itemCode";
	public static final String ITEM_CONCEPT_SUGGESTION_RESOURCE = MODULE_REST_ROOT + "itemConceptSuggestion";
	public static final String ITEM_CONCEPT_SUGGESTION_LIST_RESOURCE = MODULE_REST_ROOT + "itemConceptSuggestionList";

	public static final String DEPARTMENT_RESOURCE = MODULE_REST_ROOT + "department";
	public static final String INSTITUTION_RESOURCE = MODULE_REST_ROOT + "institution";

	public static final String STOCKROOM_RESOURCE = MODULE_REST_ROOT + "stockroom";
	public static final String ITEM_STOCK_RESOURCE = MODULE_REST_ROOT + "itemStock";
	public static final String ITEM_STOCK_DETAIL_RESOURCE = MODULE_REST_ROOT + "itemStockDetail";
	public static final String ITEM_STOCK_ENTRY_RESOURCE = MODULE_REST_ROOT + "itemStockEntry";
	public static final String ITEM_ATTRIBUTE_RESOURCE = MODULE_REST_ROOT + "itemAttribute";
	public static final String ITEM_ATTRIBUTE_TYPE_RESOURCE = MODULE_REST_ROOT + "itemAttributeType";
	public static final String INVENTORY_STOCK_TAKE_RESOURCE = MODULE_REST_ROOT + "inventoryStockTake";
	public static final String INVENTORY_STOCK_TAKE_SUMMARY_RESOURCE = MODULE_REST_ROOT + "inventoryStockTakeSummary";

	public static final String OPERATION_RESOURCE = MODULE_REST_ROOT + "stockOperation";
	public static final String OPERATION_TYPE_RESOURCE = MODULE_REST_ROOT + "stockOperationType";
	public static final String OPERATION_ITEM_RESOURCE = MODULE_REST_ROOT + "stockOperationItem";
	public static final String OPERATION_ATTRIBUTE_RESOURCE = MODULE_REST_ROOT + "stockOperationAttribute";
	public static final String OPERATION_ATTRIBUTE_TYPE_RESOURCE = MODULE_REST_ROOT + "stockOperationAttributeType";
	public static final String OPERATION_TRANSACTION_RESOURCE = MODULE_REST_ROOT + "stockOperationTransaction";

	public static final String RESERVATION_TRANSACTION_RESOURCE = MODULE_REST_ROOT + "reservationTransaction";

	protected ModuleRestConstants() {}
}
