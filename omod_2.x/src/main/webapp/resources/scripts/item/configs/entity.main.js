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
 *
 */

/* initialize and bootstrap application */
requirejs(['item/configs/entity.module'], function() {
	angular.bootstrap(document, ['entitiesApp']);
});

emr.loadMessages(["openhmis.inventory.general.new",
		"openhmis.inventory.general.error.notFound",
		"openhmis.inventory.general.created.success",
		"openhmis.inventory.general.updated.success",
		"openhmis.inventory.general.retired.success",
		"openhmis.inventory.general.unretired.success",
		"openhmis.inventory.general.confirm.delete",
		"openhmis.inventory.general.deleted.success",
		"openhmis.inventory.general.name.required",
		"openhmis.inventory.general.retireReason.required",
		"openhmis.inventory.general.unretire",
		"openhmis.inventory.general.retire",
		"openhmis.inventory.general.delete",
		"openhmis.inventory.general.retired.reason", "general.edit",
		"general.new", "general.name", "general.description", "general.cancel",
		"general.save", "general.retireReason", "general.purge",
		"openhmis.inventory.general.error.entityName",
		"openhmis.inventory.general.error.restName",
		"openhmis.inventory.general.error.uuid",
		"openhmis.inventory.general.error.retired",
		"openhmis.inventory.general.error.retireReason",
		"openhmis.inventory.department.name",
		"openhmis.inventory.department.namePlural",
		"openhmis.inventory.item.name", "openhmis.inventory.item.namePlural",
		"openhmis.inventory.item.price.name",
		"openhmis.inventory.item.price.namePlural",
		"openhmis.inventory.item.code.name",
		"openhmis.inventory.item.code.namePlural",
		"openhmis.inventory.item.concept.suggestion.name",
		"openhmis.inventory.item.concept.suggestion.namePlural",
		"openhmis.inventory.item.concept.suggestion.list.name",
		"openhmis.inventory.item.concept.suggestion.list.namePlural",
		"openhmis.inventory.item.description",
		"openhmis.inventory.item.quantity", "openhmis.inventory.item.price",
		"openhmis.inventory.item.required.departmentError",
		"openhmis.inventory.item.required.priceError",
		"openhmis.inventory.item.required.price",
		"openhmis.inventory.item.required.itemCode",
		"openhmis.inventory.item.required.default.PriceError",
		"openhmis.inventory.item.required.default.ExpirationPeriodError",
		"openhmis.inventory.item.required.minimumQuantity",
		"openhmis.inventory.item.delete.confirm.Item",
		"openhmis.inventory.item.delete.confirm.AttributeType",
		"openhmis.inventory.item.delete.confirm.itemPrice",
		"openhmis.inventory.item.delete.confirm.itemCode", "Concept",
		"openhmis.inventory.item.hasExpiration",
		"openhmis.inventory.item.hasPhysicalInventory",
		"openhmis.inventory.item.minimumQuantity",
		"openhmis.inventory.item.buyingPrice",
		"openhmis.inventory.item.prices",
		"openhmis.inventory.item.defaultPrice",
		"openhmis.inventory.item.defaultExpirationPeriod",
		"openhmis.inventory.item.enterConceptName",
		"openhmis.inventory.item.rest_name",
		"openhmis.inventory.stockroom.searchStockroom",
		"openhmis.inventory.stockroom.name", "openhmis.inventory.general.add",
		"openhmis.inventory.general.edit", ]);
