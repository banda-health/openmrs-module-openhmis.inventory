(function() {
	'use strict';

	// create main institution controller..
	angular.module('institutionApp').controller('InstitutionController',
			InstitutionController);

	// inject dependencies..
	InstitutionController.$inject = [ '$scope', 'InstitutionModel', 'InstitutionRestFactory'];

	var uuid = window.location.search.split("=")[0] === "?uuid" ? window.location.search
			.split("=")[1]
			: ""; // search looks like; '?uuid=09404'
			
	function InstitutionController($scope, InstitutionModel, InstitutionRestFactory) {
		
		/* ENTRY POINT: Load an institution */
		//use uuid extracted from the url
		entryPoint(emr, $scope, uuid);
		
		function saveOrUpdate(){
			if ($scope.institution.name === undefined || $scope.institution.name === "") {
	        	emr.errorMessage(emr.message("openhmis.inventory.institution.name.required"));
	            $scope.nameIsRequiredMsg = emr.message("openhmis.inventory.institution.name.required");
	        } else {
	            $scope.nameIsRequiredMsg = "";
	            if ($scope.institution.uuid === "" || $scope.institution.uuid === undefined) {
	                if(!InstitutionRestFactory.checkExistingInstitution($scope.institution.name)) {
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
	        if ($scope.institution.retired === true) {
	            unretire();
	        } else {
	            if ($scope.institution.retireReason === "") {
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
			InstitutionRestFactory.purgeInstitution($scope.institution, onPurgeSuccessful, onLoadError);
		}
		
		/* ########### END RESTFUL OPERATIONS ################### */
		
		// navigate back to the manage institutions page
		function cancel(){
			window.location = "manageInstitutions.page";
		}
		
		// load an institution given @uuid
		function loadInstitution(uuid){
			InstitutionRestFactory.loadInstitution(uuid, onLoadInstitution, onLoadErrorInstitution);
		}
		
		/* ########## START CALLBACK FUNCTIONS ########## */
		
		function onLoadSuccessful(data){
			if(angular.isDefined(data) && angular.isDefined(data.uuid)){
				loadInstitution(data.uuid);
			}
			else{
				init(emr, $scope, "");
			}
			
		}
		
		function onPurgeSuccessful(data){
			var institution = InstitutionModel.newModelInstance();
			bindInstitutionToScope($scope, institution);
			init(emr, $scope, "");
		}
		
		//callback for a successfully loaded institution
		function onLoadInstitution(data){
			var institution = InstitutionModel.populateModel(data);	
			bindInstitutionToScope($scope, institution);
			init(emr, $scope, uuid);
		}
		
		// callback for an unsuccessfully loaded institution
		function onLoadErrorInstitution(error){
			var institution = InstitutionModel.newModelInstance();
			bindInstitutionToScope($scope, institution);
            emr.errorMessage(emr.message("openhmis.inventory.institution.error.notFound"));
		}

		function onLoadError(error) {
			console.error(error);
			emr.errorMessage(error);
		}
		
		/* ############# END CALLBACK FUNCTIONS ################ */
		
		function bindInstitutionToScope(scope, institution) {
		    scope.institution = institution;
		}
		
		// bind view functions and variables
		function init(emr, scope, uuid){
		    if (uuid === null || uuid === undefined || uuid === "") {
		        scope.h2SubString = emr.message("general.new") == "general.new" ? "New" : emr.message("general.new");
		    } else {
		        scope.h2SubString = emr.message("general.edit");
		    }
		    if (scope.institution.retired === true) {
		        scope.retireOrUnretire = emr.message("openhmis.inventory.institution.unretire");
		    } else {
		        scope.retireOrUnretire = emr.message("openhmis.inventory.institution.retire");
		    }
		    
			scope.saveOrUpdate = saveOrUpdate;
			scope.cancel = cancel;
			scope.purge = purge;
			scope.retireOrUnretireFunction = retireOrUnretireFunction;
		}
		
		function entryPoint(emr, scope, uuid){
			//no uuid given. create a new "institution" instance
			if (uuid === null || uuid === undefined || uuid === "") {
				bindInstitutionToScope($scope, InstitutionModel.newModelInstance());
				init(emr, scope, uuid);
			}
			else{
				loadInstitution(uuid);
			}
		}
	}
})();
