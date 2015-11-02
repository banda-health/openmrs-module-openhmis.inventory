(function() {
	'use strict';

	var baseController = angular.module('app.genericEntityController');

	function GenericEntityController($scope, EntityRestFactory, GenericObjectModel) {
		
		var self = this;
		
		self.resource = '';
		self.entity_name = '';
		self.uuid = '';
		
		// protected
		self.getResourceAndEntityName = self.getResourceAndEntityName || function(){
			console.log('generic get entity name and url');
		}

		// protected
		self.bindBaseParameters = function(resource, entity_name){
			self.resource = resource;
			self.entity_name = entity_name;
		}
		
		// protected
		self.bindEntityToScope = self.bindEntityToScope || function(scope, entity){
			scope.entity = entity;
		}
		
		// protected
		// TODO: Look for a better way of retrieving url parameters
		self.getUuid = self.getUuid || function(){
			var uuid = window.location.search.split("=")[0] === "?uuid" ? window.location.search
					.split("=")[1]
					: "";
			return uuid;
		}
		
		self.saveOrUpdate = self.saveOrUpdate || function(){
			var params = {
				uuid: $scope.entity.uuid,
				name: $scope.entity.name,
				description: $scope.entity.description,
				resource: self.resource
			};
			params = self.appendBaseParams(params);
			EntityRestFactory.saveOrUpdateEntity(params, self.onChangeEntitySuccessful, self.onChangeEntityError);
		}
		
		self.retireOrUnretireCall = self.retireOrUnretireCall || function(retire){
			var params = {
				uuid: $scope.entity.uuid,
				retireReason: $scope.entity.retireReason,
				retired: $scope.entity.retired
			};
			params = self.appendBaseParams(params);
			EntityRestFactory.retireOrUnretireEntity(params, self.onChangeEntitySuccessful, self.onChangeEntityError);
		}
		
		self.purge = self.purge || function(){
			var params = {
				uuid: $scope.entity.uuid,
				purge: $scope.entity.purge
			};
			params = self.appendBaseParams(params);
			EntityRestFactory.purgeEntity(params, self.onPurgeEntitySuccessful, self.onChangeEntityError);
		}
		
		self.loadEntity = self.loadEntity || function(uuid){
			if(angular.isDefined(uuid) && uuid !== ""){
				var params = {
					uuid: uuid
				};
				params = self.appendBaseParams(params);
				EntityRestFactory.loadEntity(params, self.onLoadEntitySuccessful, self.onLoadEntityError);
			}
			else{
				var entity = GenericObjectModel.newModelInstance();
				self.bindEntityToScope($scope, entity);
			}
		}
		
		/* #### START CALLBACK Methods #### */
		self.onChangeEntitySuccessful = self.onChangeEntitySuccessful || function(data){
			console.log('generic onChangeEntitySuccessful');
		} 
		
		self.onChangeEntityError = self.onChangeEntityError || function(error){
			console.log('generic onSaveOrUpdateEntityError');
		}
		
		self.onPurgeEntitySuccessful = self.onPurgeEntitySuccessful || function(data){
			console.log('generic onPurgeEntitySuccessful');
		} 
		
		self.onLoadEntitySuccessful = self.onLoadEntitySuccessful || function(data){
			console.log('generic onLoadEntitySuccessful');
		} 
		
		self.onLoadEntityError = self.onLoadEntityError || function(error){
			console.log('generic onLoadEntityError');
		}
		/* #### END CALLBACK Methods #### */
		
		self.cancel = function(){
			window.location = "manageInstitutions.page";
		}
		
		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope || function(uuid){
			if (uuid === null || uuid === undefined || uuid === "") {
		        $scope.h2SubString = emr.message("general.new") == "general.new" ? "New" : emr.message("general.new");
		    } else {
		        $scope.h2SubString = emr.message("general.edit");
		    }
		    if (angular.isDefined($scope.entity) && angular.isDefined($scope.entity.retired) && $scope.entity.retired === true) {
		        $scope.retireOrUnretire = emr.message("openhmis." + self.resource + "." + self.entity_name + ".unretire");
		    } else {
		        $scope.retireOrUnretire = emr.message("openhmis." + self.resource + "." + self.entity_name + ".retire");
		    }
		}
		
		self.initialize = self.initialize || function(){
			self.uuid = self.getUuid();
			self.getResourceAndEntityName();
			EntityRestFactory.setBaseUrl(self.resource);
			$scope.cancel = self.cancel;
			$scope.purge = self.purge;
			$scope.saveOrUpdate = self.saveOrUpdate;
			$scope.retireOrUnretireCall = self.retireOrUnretireCall;
			self.bindExtraVariablesToScope('');
		}
		
		self.loadPage = self.loadPage || function(){
			self.initialize();
			self.loadEntity(self.uuid);
		}
		
		self.appendBaseParams = self.appendBaseParams || function(params){
			if(params){
				params['entity_name'] = self.entity_name;
				return params;
			}
		}
		
		/* ENTRY POINT */
		self.loadPage();
	}
	
	baseController.GenericEntityController = GenericEntityController;
})();
