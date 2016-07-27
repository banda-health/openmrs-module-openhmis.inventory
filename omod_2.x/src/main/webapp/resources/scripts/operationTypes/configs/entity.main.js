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

requirejs(['operationTypes/configs/entity.module'], function() {
	angular.bootstrap(document, ['entitiesApp']);
});

emr.loadMessages([
	"openhmis.inventory.operations.type.name",
	"openhmis.inventory.operations.type.name_rest",
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
	"openhmis.commons.general.retired.reason", "general.edit",
	"general.new", "general.name", "general.description", "general.cancel",
	"general.save", "general.retireReason", "general.purge",
	"general.retire",
	"general.unretire",
	"openhmis.commons.general.error.entityName",
	"openhmis.commons.general.error.restName",
	"openhmis.commons.general.error.uuid",
	"openhmis.commons.general.error.retired",
	"openhmis.commons.general.error.retireReason",
	"openhmis.inventory.attribute.type.name",
	"openhmis.commons.general.add",
	"openhmis.commons.general.edit",
	"openhmis.commons.general.saveChanges",
	"openhmis.commons.general.confirm",
	"openhmis.commons.attribute.type.namePlural",
	"openhmis.inventory.operations.type.sourceLabel",
	"openhmis.inventory.operations.type.destinationLabel",
	"openhmis.inventory.operations.type.recipientLabel",
	"openhmis.inventory.operations.type.availableWhenReservedLabel",
	"openhmis.inventory.operations.type.userLabel",
	"openhmis.inventory.operations.type.roleLabel",
	"openhmis.inventory.attribute.type.namePlural",
	"PersonAttributeType.format", "PersonAttributeType.foreignKey",
	"PatientIdentifierType.format", "FormField.required",
	"Field.attributeName", "Obs.order","openhmis.inventory.general.attributeTypeInUse.error",
]);
