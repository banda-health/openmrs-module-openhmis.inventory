(function() {
	'use strict';

	var app = angular.module('app.operationsTypeFunctionsFactory', []);
	app.service('OperationsTypeFunctions', OperationsTypeFunctions);

	OperationsTypeFunctions.$inject = [];

	function OperationsTypeFunctions() {
		var service;
		service = {
			//removeAttributeTypes: removeAttributeTypes,
			addMessageLabels : addMessageLabels
		};

		return service;

		function addMessageLabels() {
			var messages = {};
			messages['editAttributeTypeTitle'] = '';
			messages['openhmis.backboneforms.attribute.type.name'] = emr.message("openhmis.backboneforms.attribute.type.name");
			return messages;
		}
	}

})();
