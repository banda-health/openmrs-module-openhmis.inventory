(function() {
	'use strict';

	console.log("institution.controller...");
	
	// create main institution controller..
	angular.module('institutionApp').controller('InstitutionController',
			InstitutionController);

	// inject dependencies..
	InstitutionController.$inject = [ '$scope', 'InstitutionModel', 'InstitutionRestFactory'];

	var uuid = window.location.search.split("=")[0] === "?uuid" ? window.location.search
			.split("=")[1]
			: ""; // search looks like; '?uuid=09404'
			
	function InstitutionController($scope, InstitutionModel, InstitutionRestFactory) {
		
		console.log("InstitutionController func..");
		
		/* ENTRY POINT: Load an institution */
		//use uuid extracted from the url
		loadInstitution(uuid);
		
		function saveOrUpdate(){
			console.log("save or update..");
			if ($scope.institution.name() === undefined || $scope.institution.name() === "") {
	        	emr.errorMessage(emr.message("openhmis.inventory.institution.name.required"));
	            $scope.nameIsRequiredMsg = emr.message("openhmis.inventory.institution.name.required");
	        } else {
	            $scope.nameIsRequiredMsg = "";
	            if ($scope.institution.uuid() === "" || $scope.institution.uuid() === undefined) {
	                if($scope.fetchedInstitutionNames.indexOf($scope.institution.name().toLowerCase()) === -1) {
	                	$scope.nameIsRequiredMsg = "";
	                	save();
	                } else {
	                	emr.errorMessage(emr.message("openhmis.inventory.institution.error.duplicate"));
	                	$scope.nameIsRequiredMsg = emr.message("openhmis.inventory.institution.error.duplicate");
	                }
	            } else {
	                update();
	            }
	        }
		}
		
		function retireOrUnretireFunction() {
	        if ($scope.institution.retired() === true) {
	            unretire();
	        } else {
	            if ($scope.institution.reason() === "") {
	            	emr.errorMessage(emr.message("openhmis.inventory.institution.retireReason.required"));
	                $scope.retireReasonIsRequiredMsg = emr.message("openhmis.inventory.institution.retireReason.required");
	            } else {
	                $scope.retireReasonIsRequiredMsg = "";
	                retire();
	            }
	        }
	    }


		/* ######## START RESTFUL OPERATIONS ############## */
		
		function save(){
			InstitutionRestFactory.saveInstitution($scope.institution, onLoadSuccessful, onLoadError);
		}
		
		function update(){
			InstitutionRestFactory.updateInstitution($scope.institution, onLoadSuccessful, onLoadError);
		}
		
		function retire(){
			InstitutionRestFactory.retireInstitution($scope.institution, onLoadSuccessful, onLoadError);
		}
		
		function unretire(){
			InstitutionRestFactory.unretireInstitution($scope.institution, onLoadSuccessful, onLoadError);
		}
		
		function purge(){
			InstitutionRestFactory.purge($scope.institution, onLoadSuccessful, onLoadError);
		}
		
		/* ########### END RESTFUL OPERATIONS ################### */
		
		// navigate back to the manage institutions page
		function cancel(){
			window.location = "manageInstitutions.page";
		}
		
		// load an institution given @uuid
		function loadInstitution(uuid){
			console.log('load institution.....');
			InstitutionRestFactory.loadInstitution(uuid, onLoadInstitution, onLoadErrorInstitution);
		}
		
		/* ########## START CALLBACK FUNCTIONS ########## */
		
		function onLoadSuccessful(data){
			loadInstitution(data.uuid);
		}
		
		//callback for a successfully loaded institution
		function onLoadInstitution(data){
			console.log("successful callback ");
			console.log(data);
			var institution = InstitutionModel.populateModel(data);	
			
			console.log('institution model...');
			console.log(institution);
			
			setInstitutionProperties($scope, institution);
			
			init(emr, $scope, uuid);
		}
		
		// callback for an unsuccessfully loaded institution
		function onLoadErrorInstitution(error){
			console.log("failed callback.. " + error);
			var institution = InstitutionModel.newModelInstance();
			setInstitutionProperties($scope, institution);
            emr.errorMessage(emr.message("openhmis.inventory.institution.error.notFound"));
		}

		function onLoadError(error) {
			console.error(error);
			emr.errorMessage(error);
		}
		
		/* ############# END CALLBACK FUNCTIONS ################ */
		
		// bind institution to scope
		function setInstitutionProperties(scope, institution) {
		    scope.institution = institution;
		}
		
		// initialize page and bind functions
		function init(emr, scope, uuid){
		    if (uuid === null || uuid === undefined || uuid === "") {
		        scope.h2SubString = emr.message("general.new") == "general.new" ? "New" : emr.message("general.new");
		    } else {
		        scope.h2SubString = emr.message("general.edit");
		    }
		    if (scope.institution.retired() === true) {
		        scope.retireOrUnretire = emr.message("openhmis.inventory.institution.unretire");
		    } else {
		        scope.retireOrUnretire = emr.message("openhmis.inventory.institution.retire");
		    }
		    
			scope.saveOrUpdate = saveOrUpdate;
			scope.cancel = cancel;
			scope.purge = purge;
			scope.retireOrUnretireFunction = retireOrUnretireFunction;
		}
		
	}
})();
