(function() {
	'use strict';

	var base = angular.module('app.genericManageController');
	base.controller("ManageOperationTypesController",
			ManageOperationTypesController);
	ManageOperationTypesController.$inject = ['$injector', '$scope', '$filter',
			'EntityRestFactory', 'CssStylesFactory', 'PaginationService',
			'OperationTypesModel', 'CookiesService'];

	function ManageOperationTypesController($injector, $scope, $filter,
			EntityRestFactory, CssStylesFactory, PaginationService,
			OperationTypesModel, CookiesService) {

		var self = this;

		var module_name = 'inventory';
		var entity_name = emr
				.message("openhmis.inventory.operations.type.name");
		var rest_entity_name = emr
				.message("openhmis.inventory.operations.type.name_rest");

		// @Override
		self.getModelAndEntityName = self.getModelAndEntityName
				|| function() {
					self.bindBaseParameters(module_name, rest_entity_name,
							entity_name);
				}

		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericManageController, self, {
			$scope : $scope,
			$filter : $filter,
			EntityRestFactory : EntityRestFactory,
			PaginationService : PaginationService,
			CssStylesFactory : CssStylesFactory,
			GenericMetadataModel : OperationTypesModel,
			CookiesService : CookiesService
		});
	}
})();
