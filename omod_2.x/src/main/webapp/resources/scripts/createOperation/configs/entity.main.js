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
requirejs(['createOperation/configs/entity.module'], function() {
	angular.bootstrap(document, ['createOperationApp']);
});

/* load UI messages */
emr.loadMessages([
	"openhmis.commons.general.error.notFound",
	"openhmis.commons.general.created.success",
	"openhmis.commons.general.updated.success",
	"openhmis.commons.general.confirm.delete",
	"openhmis.commons.general.deleted.success",
	"openhmis.commons.general.name.required",
	"general.new",
	"general.name",
	"general.description",
	"general.cancel",
	"general.save",
	"openhmis.commons.general.error.entityName",
	"openhmis.commons.general.error.restName",
	"openhmis.commons.general.error.uuid",
	"openhmis.commons.general.add",
	"openhmis.inventory.stock.operation.name",
	"openhmis.inventory.stock.operation.rest_name",
	"openhmis.commons.general.status",
	"openhmis.inventory.operations.type.name",
	"openhmis.inventory.stockroom.name",
	"openhmis.inventory.item.name",
	"openhmis.inventory.operations.dateCreated",
	"openhmis.inventory.operations.operationDate",
	"openhmis.inventory.operations.operationType",
	"openhmis.inventory.operations.operationNumber",
	"openhmis.commons.general.details",
	"openhmis.inventory.item.namePlural",
	"openhmis.inventory.stockroom.transaction.name",
	"openhmis.inventory.operations.operationCreators",
	"openhmis.inventory.operations.sourceStockroom",
	"openhmis.inventory.operations.destinationStockroom",
	"openhmis.commons.general.close",
	"openhmis.inventory.item.quantity",
	"openhmis.commons.general.expiration",
	"openhmis.inventory.item.enterItemSearch",
	"openhmis.commons.general.actions",
	"openhmis.commons.general.postSearchMessage",
	"openhmis.inventory.operations.confirm.title.operationTypeChange",
	"openhmis.inventory.operations.confirm.title.sourceStockroomChange",
	"openhmis.inventory.operations.confirm.operationTypeChange",
	"openhmis.inventory.operations.confirm.sourceStockroomChange",
	"openhmis.commons.general.requirePatient",
	"openhmis.inventory.operations.required.institution",
	"openhmis.inventory.operations.required.department",
	"openhmis.inventory.operations.error.number",
	"openhmis.inventory.operations.error.itemError",
	"openhmis.inventory.operations.error.expiryDate",
	"openhmis.inventory.operations.before",
	"openhmis.inventory.operations.afterLastOperation",
	"openhmis.commons.general.required.itemAttribute",
	"openhmis.inventory.operations.error.invalidItem",
	"openhmis.inventory.operations.error.itemQuantity",
	"openhmis.inventory.operations.error.itemError"
]);
