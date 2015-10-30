(function() {
	'use strict';

	var base = angular.module('app.genericManageController');
	base.controller("ManageInstitutionController", ManageInstitutionController);
	ManageInstitutionController.$inject = ['$injector', '$scope', 'ManageEntityRestFactory', 'CssStylesFactory', 'PaginationService', 'InstitutionModel'];
	
	function ManageInstitutionController($injector, $scope, ManageEntityRestFactory, CssStylesFactory, PaginationService, InstitutionModel) {

		var self = this;
		
		var resource = 'inventory';
		var entity_name = 'institution';
		
		// @Override
		self.getResourceAndEntityName = self.getResourceAndEntityName || function(){
			self.bindBaseParameters(resource, entity_name);
		}
		
		// @Override
		self.onLoadEntitiesSuccess = self.onLoadEntitiesSuccess || function(data){
			$scope.fetchedInstitutions = InstitutionModel.populateModels(data.results);
			self.computeNumberOfPages(data.length);
		}
		
		// @Override
		self.onLoadEntitiesError = self.onLoadEntitiesError || function(error){
			console.error(error);
			emr.errorMessage(error);
		}
		
		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericManageController, self, {
			$scope: $scope,
			ManageEntityRestFactory: ManageEntityRestFactory, 
			PaginationService: PaginationService, 
			CssStylesFactory: CssStylesFactory
		});
	}
})();
