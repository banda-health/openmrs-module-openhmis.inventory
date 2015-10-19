(function() {
	'use strict';

	// create manage institution main controller..
	angular.module('manageInstitutionApp').controller('ManageInstitutionController',
			ManageInstitutionController);

	// inject dependencies..
	ManageInstitutionController.$inject = [ '$scope', 'ManageInstitutionRestFactory', 'CssStylesFactory', 'InstitutionModel' ];

	function ManageInstitutionController($scope, ManageInstitutionRestFactory, CssStylesFactory, InstitutionModel) {
		
		/* ENTRY POINT: load institutions */
		console.log("load all institutions");
		loadInstitutions();
		
		// function called to view institution details.
		function loadInstitutionFromManagePage(uuid) {
			window.location = "institution.page?uuid=" + uuid;
		}
		
		/* ######## START RESTFUL OPERATIONS ############## */
		
		function loadInstitutions(){
			ManageInstitutionRestFactory.loadInstitutions('', onLoadInstitutionsSuccess, onLoadError);	
		}
		
		function includeRetiredInstitutions(){
			if($scope.includeRetired){
				var params = [];
				params["includeAll"] = true;
				ManageInstitutionRestFactory.includeRetiredInstitutions(params, onLoadRetiredInstitutionSuccess, onLoadError);
			}
			else{
				loadInstitutions();
			}
		}
		
		/* ########### END RESTFUL OPERATIONS ################### */
		
		/* ########## START CALLBACK FUNCTIONS ########## */
		
		// successful call back on loading institutions
		function onLoadInstitutionsSuccess(data){
			initialize($scope, data, false);
		}
		
		//callback for a successful retiredInstitution 
		function onLoadRetiredInstitutionSuccess(data) {
			initialize($scope, data, true);
		}
		
		function onLoadError(error) {
			console.error(error);
			emr.errorMessage(error);
		}
		
		/* ############# END CALLBACK FUNCTIONS ################ */
		
		// bind scope with required attributes and/or functions..
		function initialize(scopeObj, data, includeRetired) {
			
		    scopeObj.includeRetired = includeRetired;
		    scopeObj.currentPage = 0;
		    
		    scopeObj.fetchedInstitutions = InstitutionModel.populateModels(data.results);
		    scopeObj.length = scopeObj.fetchedInstitutions.length;
		    scopeObj.loadInstitutionFromManagePage = loadInstitutionFromManagePage;
		    scopeObj.includeRetiredInstitutions = includeRetiredInstitutions;
		    
		    //paging logic
		    scopeObj.numberOfPages = function() {
		        return Math.ceil(scopeObj.fetchedInstitutions.length / 10);
		    }
		    scopeObj.pagingFrom = function() {
		        return scopeObj.currentPage <= 0 ? 1 : (scopeObj.currentPage) * 10; //TODO must the default number to be paged = 10 be available for change by the user?
		    }
		    scopeObj.pagingTo = function() {
		        return scopeObj.currentPage <= 0 ? 10 : (scopeObj.currentPage + 1) * 10;
		    }
		    scopeObj.existingPages = function() {
		        var pages = [];

		        for (var i = 1; i <= scopeObj.numberOfPages(); i++) {
		            pages.push(i);
		        }
		        return pages;
		    }
		    scopeObj.loadPageByNumber = function(page) {
		        scopeObj.currentPage = page - 1;
		    }
		    scopeObj.disAbleSinglePage = function(page) {
		        if (page === scopeObj.currentPage + 1 || (page === scopeObj.currentPage + 1 && (page - 1 === 0 || page === scopeObj.numberOfPages()))) {
		            return true;
		        } else {
		            return false;
		        }
		    }
		    
		    scopeObj.strikeThrough = CssStylesFactory.strikeThrough;
		}
	}
})();
