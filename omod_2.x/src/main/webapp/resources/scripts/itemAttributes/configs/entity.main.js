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
requirejs(['itemAttributes/configs/entity.module'], function() {
    angular.bootstrap(document, ['itemAttributeTypesApp']);
});

/* load UI messages */
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
    "openhmis.backboneforms.attribute.type.name",
    "openhmis.backboneforms.attribute.type.namePlural",
    "openhmis.inventory.admin.item.attribute.types",
    "openhmis.inventory.item.delete.confirm.AttributeType",
    "openhmis.backboneforms.attribute.type.name",
    "openhmis.backboneforms.required.field.name",
    "openhmis.inventory.itemAttributeType",
    "openhmis.inventory.itemAttributeType_rest",
    "FormField.required",
    "PersonAttributeType.foreignKey",
    "PersonAttributeType.format",
    "PatientIdentifierType.format",
    "Obs.order",
    "Field.attributeName",
]);
