(function() {
	'use strict';

	var base = angular.module('app.genericEntityController');
	base.controller("InstitutionController", InstitutionController);
	InstitutionController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory', 'InstitutionModel', '$filter' ];

	function InstitutionController($stateParams, $injector, $scope, $filter, EntityRestFactory, InstitutionModel) {

		var self = this;

		var module_name = 'inventory';
		var entity_rest_name = 'institution';
		var entity_name = emr.message("openhmis.inventory.institution.name");
		
		// @Override
		self.getModuleAndEntityName = self.getModuleAndEntityName || function() {
			self.bindBaseParameters(module_name, entity_rest_name, entity_name);
		}

		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, self, {
			$scope : $scope,
			$filter : $filter,
			$stateParams : $stateParams,
			EntityRestFactory : EntityRestFactory,
			GenericMetadataModel : InstitutionModel
		});
	}
})();
