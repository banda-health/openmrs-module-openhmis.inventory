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

(function () {
	'use strict';

	var app = angular.module('app.operationsTypeFunctionsFactory', []);
	app.service('OperationsTypeFunctions', OperationsTypeFunctions);

	OperationsTypeFunctions.$inject = [];

	function OperationsTypeFunctions() {
		var service;
		service = {
			addMessageLabels: addMessageLabels
		};

		return service;
		
		

		function addMessageLabels() {
			var messages = {};
			messages['openhmis.commons.general.add'] = emr
				.message('openhmis.commons.general.add');
			messages['openhmis.inventory.attribute.type.name'] = emr
				.message('openhmis.inventory.attribute.type.name');
			messages['openhmis.commons.general.edit'] = emr
				.message('openhmis.commons.general.edit');
			messages['openhmis.commons.general.saveChanges'] = emr
				.message("openhmis.commons.general.saveChanges");
			messages['openhmis.commons.general.confirm'] = emr
				.message("openhmis.commons.general.confirm");
			messages['openhmis.inventory.operations.type.sourceLabel'] = emr
				.message("openhmis.inventory.operations.type.sourceLabel");
			messages['openhmis.inventory.operations.type.destinationLabel'] = emr
				.message("openhmis.inventory.operations.type.destinationLabel");
			messages['openhmis.inventory.operations.type.recipientLabel'] = emr
				.message("openhmis.inventory.operations.type.recipientLabel");
			messages['openhmis.inventory.operations.type.availableWhenReservedLabel'] = emr
				.message("openhmis.inventory.operations.type.availableWhenReservedLabel");
			messages['openhmis.inventory.operations.type.userLabel'] = emr
				.message("openhmis.inventory.operations.type.userLabel");
			messages['openhmis.inventory.operations.type.roleLabel'] = emr
				.message("openhmis.inventory.operations.type.roleLabel");
			messages['openhmis.inventory.attribute.type.namePlural'] = emr
				.message("openhmis.inventory.attribute.type.namePlural");
			messages['PersonAttributeType.format'] = emr
				.message("PersonAttributeType.format");
			messages['PersonAttributeType.foreignKey'] = emr
				.message("PersonAttributeType.foreignKey");
			messages['PatientIdentifierType.format'] = emr
				.message("PatientIdentifierType.format");
			messages['FormField.required'] = emr.message("FormField.required");
			messages['Field.attributeName'] = emr
				.message("Field.attributeName");
			messages['Obs.order'] = emr.message("Obs.order");
			return messages;
		}

	}

})();
