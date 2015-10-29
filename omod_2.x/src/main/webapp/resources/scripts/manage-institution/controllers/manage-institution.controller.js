(function() {
	'use strict';

	var base = angular.module('app.genericManageController');
	base.controller("ManageInstitutionController", ManageInstitutionController);
	ManageInstitutionController.$inject = ['$injector', '$scope', 'ManageInstitutionRestFactory', 'CssStylesFactory', 'InstitutionModel', 'PaginationService'];
	
	function ManageInstitutionController($injector, $scope, ManageInstitutionRestFactory, CssStylesFactory, InstitutionModel, PaginationService) {

		// @Override
		this.bindExtraVariablesToScope = this.bindExtraVariablesToScope || function(){
			$scope.pagingFrom = PaginationService.pagingFrom;
			$scope.pagingTo = PaginationService.pagingTo;
			$scope.strikeThrough = CssStylesFactory.strikeThrough;
		}
		
		// @Override
		this.paginate = this.paginate || function(start, limit){
			var params = PaginationService.paginateParams(start, limit, $scope.includeRetired, $scope.searchByName);
			ManageInstitutionRestFactory.loadInstitutions(params, onLoadInstitutions, onLoadError);
		}
		
		function onLoadInstitutions(data){
			$scope.fetchedInstitutions = InstitutionModel.populateModels(data.results);
			setTotalCount(data.length);
		}
		
		function onLoadError(error) {
			console.error(error);
			emr.errorMessage(error);
		}
		
		function setTotalCount(length){
			var totalNumOfResults = length;
			var limit = $scope.limit;
			var numberOfPages = Math.ceil(totalNumOfResults / limit);
			
			$scope.totalNumOfResults = totalNumOfResults;
			$scope.numberOfPages = numberOfPages;
		}
		
		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericManageController, this, {
			$scope: $scope
		});
	}
})();
