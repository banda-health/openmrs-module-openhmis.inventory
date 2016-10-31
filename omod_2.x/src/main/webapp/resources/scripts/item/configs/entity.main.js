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

emr.loadMessages([
	"openhmis.commons.general.new",
	"openhmis.commons.general.error.notFound",
	"openhmis.commons.general.created.success",
	"openhmis.commons.general.updated.success",
	"openhmis.commons.general.retired.success",
	"openhmis.commons.general.unretired.success",
	"openhmis.commons.general.confirm.delete",
	"openhmis.commons.general.deleted.success",
	"openhmis.commons.general.name.required",
	"openhmis.commons.general.retireReason.required",
	"openhmis.commons.general.unretire",
	"openhmis.commons.general.retire",
	"openhmis.commons.general.delete",
	"openhmis.commons.general.retired.reason",
	"general.edit",	"general.new", "general.name",
	"general.description", "general.cancel",
	"general.save", "general.retireReason",
	"general.purge", "general.retire",	"general.unretire",
	"openhmis.commons.general.error.entityName",
	"openhmis.commons.general.error.restName",
	"openhmis.commons.general.error.uuid",
	"openhmis.commons.general.error.retired",
	"openhmis.commons.general.error.retireReason",
	"openhmis.inventory.department.name",
	"openhmis.inventory.department.namePlural",
	"openhmis.inventory.item.name",
	"openhmis.inventory.item.namePlural",
	"openhmis.inventory.item.price.name",
	"openhmis.inventory.item.price.namePlural",
	"openhmis.inventory.item.code.name",
	"openhmis.inventory.item.code.namePlural",
	"openhmis.inventory.item.concept.suggestion.name",
	"openhmis.inventory.item.concept.suggestion.namePlural",
	"openhmis.inventory.item.concept.suggestion.list.name",
	"openhmis.inventory.item.concept.suggestion.list.namePlural",
	"openhmis.inventory.item.description",
	"openhmis.inventory.item.quantity",
	"openhmis.inventory.item.price",
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
	"openhmis.inventory.stockroom.name",
	"openhmis.commons.general.add",
	"openhmis.commons.general.enterSearchPhrase",
	"openhmis.commons.general.edit",
	"openhmis.commons.general.postSearchMessage",
	"openhmis.commons.general.required.defaultPrice",
	"openhmis.commons.general.required.itemAttribute",
	"openhmis.commons.general.required.priceValue",
	"openhmis.commons.general.required.prices"
]);
