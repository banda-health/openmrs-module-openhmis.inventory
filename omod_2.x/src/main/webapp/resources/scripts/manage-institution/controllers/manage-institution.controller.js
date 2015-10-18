(function() {
	'use strict';

	console.log("manage-institution.controller...");
	
	// create manage institution main controller..
	angular.module('manageInstitutionApp').controller('ManageInstitutionController',
			ManageInstitutionController);

	// inject dependencies..
	ManageInstitutionController.$inject = [ '$scope', 'ManageInstitutionRestFactory', 'CssStylesFactory', 'InstitutionModel' ];

	function ManageInstitutionController($scope, ManageInstitutionRestFactory, CssStylesFactory, InstitutionModel) {
		
		console.log("ManageInstitutionController func..");

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
		
		function includeRetiredInsitutions(){
			var params = [];
			if($scope.includeRetired){
				params["includeAll"] = true;
			}
			ManageInstitutionRestFactory.includeRetiredInstitutions(params, onLoadRetiredInstitutionSuccess, onLoadError);
		}
		
		function updateExistingInstitutionNames(){
			var params = [];
			params["includeAll"] = true;
			ManageInstitutionRestFactory.includeRetiredInstitutions(params, onLoadExistingInstitutionNames, onLoadError);
		}
		
		/* ########### END RESTFUL OPERATIONS ################### */
		
		/* ########## START CALLBACK FUNCTIONS ########## */
		
		// successful call back on loading institutions
		function onLoadInstitutionsSuccess(data){
			console.log('institutions loaded successfully.... ' + data);
			initialize($scope, data, false);
		}
		
		//callback for a successful retiredInstitution 
		function onLoadRetiredInstitutionSuccess(data, params) {
			if(params["includeAll"] == true){
				initialize($scope, data, true);
			}
			else{
				initialize($scope, data, false);
			}
		}
		
		//callback function for updating existing institutions
		function onLoadExistingInstitutionNames(data){
			console.log('pnLoadExistingInstitutionNames');
			console.log(data);
			$scope.fetchedInstitutionNames = []; 
             
            for(var i = 0; i < data.results.length; i++) {
            	$scope.fetchedInstitutionNames.push(data.results[i].name.toLowerCase());
            }
		}

		function onLoadError(error) {
			console.error(error);
			emr.errorMessage(error);
		}
		
		/* ############# END CALLBACK FUNCTIONS ################ */
		
		// bind scope with required attributes and/or functions..
		function initialize(scopeObj, data, includeRetired) {
			
			updateExistingInstitutionNames();
			
		    scopeObj.includeRetired = includeRetired;
		    scopeObj.currentPage = 0;
		    
		    var institutions = InstitutionModel.populateModels(data.results);
		    
		    console.log('institution model..');
		    for(var j = 0; j < institutions.length; j++){
		    	console.log(institutions[j].name);
		    	console.log(institutions[j].description);
		    }
		    
		    scopeObj.fetchedInstitutions = institutions;
		    scopeObj.length = scopeObj.fetchedInstitutions.length;
		    scopeObj.loadInstitutionFromManagePage = loadInstitutionFromManagePage;
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
