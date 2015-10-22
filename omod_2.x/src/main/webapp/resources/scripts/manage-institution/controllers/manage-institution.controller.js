(function() {
	'use strict';

	// create manage institution main controller..
	angular.module('manageInstitutionApp').controller('ManageInstitutionController',
			ManageInstitutionController);

	// inject dependencies..
	ManageInstitutionController.$inject = [ '$scope', 'ManageInstitutionRestFactory', 'CssStylesFactory', 'InstitutionModel'];
	
	function ManageInstitutionController($scope, ManageInstitutionRestFactory, CssStylesFactory, InstitutionModel) {
		
		/* ENTRY POINT: loadPage() */
		loadPage();
		
		function loadPage(){
			// initialize variables.
			$scope.currentPage = 1;
			$scope.limit = 5;
			$scope.numberOfPages = 0;
			//get total count of results
			getTotalCount();
			//load 1st page..
			paginate($scope.currentPage, $scope.limit);
			//bind required functions
			initialize($scope);
	
		}
		
		// function called to view institution details.
		function loadInstitutionFromManagePage(uuid) {
			window.location = "institution.page?uuid=" + uuid;
		}
		
		/* ######## START RESTFUL OPERATIONS ############## */
		function paginate(start, limit){
			var limit = $scope.limit;
			var startIndex = ((start - 1) * limit) + 1;
			var params;
			if($scope.includeRetired){
				params = {
					limit : limit,
					includeAll : true,
					startIndex : startIndex
				};
			}
			else{
				params = {
					limit : limit,
					startIndex : startIndex
				};
			}
				
			ManageInstitutionRestFactory.loadInstitutions(params, function(data){
				$scope.fetchedInstitutions = InstitutionModel.populateModels(data.results);
			},
			onLoadError);
		}
		
		// retrieve total number of institutions.
		// TODO: implement a webservice method to fetch total number of results
		function getTotalCount(){
			var params;
			if($scope.includeRetired){
				params = {includeAll : true};
			}
			
			ManageInstitutionRestFactory.loadInstitutions(params, onLoadGetTotalCountSuccess, onLoadError);
		}
		/* ########### END RESTFUL OPERATIONS ################### */
		
		/* ########## START CALLBACK FUNCTIONS ########## */
		
		// successful call back on loading institutions
		function onLoadError(error) {
			console.error(error);
			emr.errorMessage(error);
		}
		
		function onLoadGetTotalCountSuccess(data){
			var results = data.results;
			var totalNumOfResults = results.length;
			var limit = $scope.limit;
			var numberOfPages = Math.ceil(totalNumOfResults / limit);
			
			$scope.totalNumOfResults = totalNumOfResults;
			$scope.numberOfPages = numberOfPages;
		}
		
		/* ############# END CALLBACK FUNCTIONS ################ */
		
		// bind scope with required attributes and/or functions..
		function initialize(scopeObj) {
			
			scopeObj.loadPage = loadPage;
			
		    scopeObj.loadInstitutionFromManagePage = loadInstitutionFromManagePage;
		    
		    scopeObj.pagingFrom = function() {
		    	var limit = scopeObj.limit;
		    	return scopeObj.currentPage <= 1 ? 1 : (scopeObj.currentPage - 1) * limit;
		    }

		    scopeObj.pagingTo = function() {
		    	var limit = scopeObj.limit;
		    	if(scopeObj.currentPage <= 0){
		    		return limit;
		    	}
		    	else{
		    		var num = scopeObj.currentPage * limit;
		    		if(num > scopeObj.totalNumOfResults){
		    			return scopeObj.totalNumOfResults;
		    		}
		    		return num;
		    	}
		    }
		    
		    scopeObj.strikeThrough = CssStylesFactory.strikeThrough;
		    
		    scopeObj.paginate = paginate;
		}
	}
})();
