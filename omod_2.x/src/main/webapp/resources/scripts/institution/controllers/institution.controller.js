(function() {
	'use strict';

	var base = angular.module('app.genericEntityController');
	base.controller("InstitutionController", InstitutionController);
	InstitutionController.$inject = ['$injector', '$scope', 'EntityRestFactory', 'InstitutionModel'];

	function InstitutionController($injector, $scope, EntityRestFactory, InstitutionModel) {
		
		var self = this;
		
		var resource = 'inventory';
		var entity_name = 'institution';
		
		// @Override
		self.getResourceAndEntityName = self.getResourceAndEntityName || function(){
			self.bindBaseParameters(resource, entity_name);
		}
		
		// @Override
		self.bindEntityToScope = self.bindEntityToScope || function(scope, entity){
			scope.entity = entity;
		}
		
		// @Override
		self.onChangeEntitySuccessful = self.onChangeEntitySuccessful || function(data){
			if(angular.isDefined(data) && angular.isDefined(data.uuid)){
				self.loadEntity(data.uuid);
			}
			else{
				self.bindExtraVariablesToScope("");
			}
		} 
		
		// @Override
		self.onChangeEntityError = self.onChangeEntityError || function(error){
			console.error(error);
			emr.errorMessage(error);
		}
		
		// @Override
		self.onPurgeEntitySuccessful = self.onPurgeEntitySuccessful || function(data){
			self.cancel();
		} 
		
		// @Override
		self.onLoadEntitySuccessful = self.onLoadEntitySuccessful || function(data){
			var entity = InstitutionModel.populateModel(data);
			self.bindEntityToScope($scope, entity);
			self.bindExtraVariablesToScope(entity.uuid);
		} 
		
		// @Override
		self.onLoadEntityError = self.onLoadEntityError || function(error){
			var entity = InstitutionModel.newModelInstance();
			self.bindEntityToScope($scope, entity);
            emr.errorMessage(emr.message("openhmis.inventory.institution.error.notFound"));
		}
		
		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, self, {
			$scope: $scope,
			EntityRestFactory: EntityRestFactory
		});
	}
})();
