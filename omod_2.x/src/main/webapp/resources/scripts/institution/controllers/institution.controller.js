(function() {
	'use strict';

	var base = angular.module('app.genericEntityController');

	var uuid = window.location.search.split("=")[0] === "?uuid" ? window.location.search
			.split("=")[1]
			: ""; // search looks like; '?uuid=09404'
			
	function InstitutionController($injector, $scope, InstitutionModel, InstitutionRestFactory) {
		
		var self = this;
		
		// @Override
		self.loadEntity = self.loadEntity || function(){
			if(angular.isDefined(uuid) && uuid !== ""){
				$scope.uuid = uuid;
				InstitutionRestFactory.loadInstitution(uuid, onLoadInstitutionSuccessful, onLoadInstitutionError);
			}
			else{
				var entity = InstitutionModel.newModelInstance();
				bindEntityToScope($scope, entity);
			}
		}
		
		// @Override
		self.saveOrUpdate = self.saveOrUpdate || function(){
			InstitutionRestFactory.saveOrUpdateInstitution($scope.institution, onChangeInstitutionSuccessful, onChangeInstitutionError);
		}
		
		// @Override
		self.retireOrUnretireCall = self.retireOrUnretireCall || function() {
			InstitutionRestFactory.retireOrUnretireInstitution($scope.institution, onChangeInstitutionSuccessful, onChangeInstitutionError);
	    }

		// @Override
		self.purge = self.purge || function(){
			InstitutionRestFactory.purgeInstitution($scope.institution, onPurgeSuccessful, onChangeInstitutionError);
		}
		
		// @Override
		self.cancel = self.cancel || function(){
			window.location = "manageInstitutions.page";
		}
		
		/* ########## START LOCAL METHODS ########## */
		function onChangeInstitutionSuccessful(data){
			if(angular.isDefined(data) && angular.isDefined(data.uuid)){
				uuid = data.uuid;
				self.loadEntity();
			}
			else{
				self.bindExtraVariablesToScope("");
			}
		}
		
		function onChangeInstitutionError(error) {
			console.error(error);
			emr.errorMessage(error);
		}
		
		function onLoadInstitutionSuccessful(data){
			var entity = InstitutionModel.populateModel(data);	
			bindInstitutionToScope($scope, entity);
			self.bindExtraVariablesToScope(entity.uuid);
		}
		
		function onLoadInstitutionError(error){
			var entity = InstitutionModel.newModelInstance();
			bindInstitutionToScope($scope, entity);
            emr.errorMessage(emr.message("openhmis.inventory.institution.error.notFound"));
		}
		
		function onPurgeSuccessful(data){
			self.cancel();
		}

		function bindInstitutionToScope(scope, entity) {
		    scope.entity = entity;
		}
		/* ############# END LOCAL METHODS ################ */
		
		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, this, {
			$scope: $scope
		});
	}
	
	base.controller("InstitutionController", InstitutionController);
	InstitutionController.$inject = ['$injector', '$scope', 'InstitutionModel', 'InstitutionRestFactory'];
})();
