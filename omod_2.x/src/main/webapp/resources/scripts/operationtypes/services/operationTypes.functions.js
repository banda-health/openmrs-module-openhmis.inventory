( function() {
	'use strict';

	var app = angular.module('app.operationsTypeFunctionsFactory', []);
	app.service('OperationsTypeFunctions', OperationsTypeFunctions);

	OperationsTypeFunctions.$inject = [];
	function OperationsTypeFuntions() {
		var service;
		service = {
			removeAttributeTypes: removeAttributeTypes,
		}
	};

})();
