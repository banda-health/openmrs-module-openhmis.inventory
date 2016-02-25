(function() {
	'use strict';

	var base = angular.module('app.genericManageController');
	base.controller("ManageItemController", ManageItemController);
	ManageItemController.$inject = ['$injector', '$scope', '$filter',
			'EntityRestFactory', 'CssStylesFactory', 'PaginationService',
			'ItemModel', 'CookiesService'];

	function ManageItemController($injector, $scope, $filter,
			EntityRestFactory, CssStylesFactory, PaginationService, ItemModel,
			CookiesService) {

		var self = this;

		var module_name = 'inventory';
		var entity_name = emr.message("openhmis.inventory.item.name");
		var rest_entity_name = emr.message("openhmis.inventory.item.rest_name");

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
			GenericMetadataModel : ItemModel,
			CookiesService : CookiesService
		});
	}
})();
