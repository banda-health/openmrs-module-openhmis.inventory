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
requirejs(['stockOperations/configs/entity.module'], function() {
    angular.bootstrap(document, ['stockOperationApp']);
});

/* load UI messages */
emr.loadMessages([
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
    "general.edit",
    "general.new",
    "general.name",
    "general.description",
    "general.cancel",
    "general.save",
    "general.retireReason",
    "general.purge",
    "general.retire",
    "general.unretire",
    "openhmis.commons.general.error.entityName",
    "openhmis.commons.general.error.restName",
    "openhmis.commons.general.error.uuid",
    "openhmis.commons.general.error.retired",
    "openhmis.commons.general.error.retireReason",
    "openhmis.commons.general.add",
    "openhmis.commons.general.edit",
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
    "openhmis.commons.general.rollbackOperation",
    "openhmis.commons.general.close",
    "openhmis.inventory.item.quantity",
    "openhmis.commons.general.batchOperation",
    "openhmis.commons.general.expiration",
    "openhmis.inventory.item.enterItemSearch",
    "openhmis.commons.general.auto",
    "openhmis.commons.general.patient",
    "openhmis.commons.general.actions",
    "openhmis.commons.general.completeOperation",
    "openhmis.commons.general.cancelOperation",
    "openhmis.commons.general.cancelReason",
    "openhmis.commons.general.postSearchMessage",
    "openhmis.commons.general.status"
]);
