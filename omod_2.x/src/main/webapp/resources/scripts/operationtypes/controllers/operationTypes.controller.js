(function() {
	'use strict';

	var base = angular.module('app.genericEntityController');
	base.controller("OperationTypesController", OperationTypesController);
	OperationTypesController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory',
		'OperationTypesModel'];

	function OperationTypesController($stateParams, $injector, $scope, $filter, EntityRestFactory, OperationTypesModel) {

		var self = this;

		var module_name = 'inventory';
		var entity_name = emr.message("openhmis.inventory.operations.type.name");
		var rest_entity_name = emr.message("openhmis.inventory.operations.type.name_rest");
		var cancel_page = 'manageOperation.page';

		// @Override
		self.setRequiredInitParameters = self.setRequiredInitParameters || function() {
					self.bindBaseParameters(module_name, rest_entity_name, entity_name, cancel_page);
				}

		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, self, {
			$scope: $scope,
			$filter: $filter,
			$stateParams: $stateParams,
			EntityRestFactory: EntityRestFactory,
			GenericMetadataModel: OperationTypesModel
		});
	}
})();
