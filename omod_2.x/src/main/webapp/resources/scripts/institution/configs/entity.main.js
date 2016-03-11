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
requirejs(['institution/configs/entity.module'], function() {
	angular.bootstrap(document, ['entitiesApp']);
});

/*Load UI messages for the istitutions */
emr.loadMessages(["openhmis.inventory.institution.name",
		"openhmis.inventory.institution.name_rest",
		"openhmis.inventory.general.new",
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
		"general.retire",
		"general.unretire",
		"openhmis.inventory.general.error.entityName",
		"openhmis.inventory.general.error.restName",
		"openhmis.inventory.general.error.uuid",
		"openhmis.inventory.general.error.retired",
		"openhmis.inventory.general.error.retireReason", ]);
