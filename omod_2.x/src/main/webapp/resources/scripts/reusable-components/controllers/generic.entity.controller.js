(function() {
	'use strict';

	var baseController = angular.module('app.genericEntityController');

	function GenericEntityController($scope, $filter, $stateParams, EntityRestFactory, GenericMetadataModel) {

		var self = this;
		self.module_name = '';
		self.entity_rest_name = '';
		self.entity_name = '';
		self.uuid = '';

		self.name = emr.message("openhmis.inventory.institution.name")

		// protected
		self.getModelAndEntityName = self.getModuleAndEntityName || function() {
			console.log('generic get entity name and url');
		}

		// protected
		self.bindBaseParameters = function(module_name, entity_rest_name, entity_name) {
			self.module_name = module_name;
			self.entity_rest_name = entity_rest_name;
			self.entity_name = entity_name;
		}

		// protected
		self.bindEntityToScope = self.bindEntityToScope || function(scope, entity) {
			scope.entity = entity;
		}

		// protected
		self.getUuid = self.getUuid || function() {
			return $stateParams['uuid'];
		}

		self.saveOrUpdate = self.saveOrUpdate || function() {
			var params = {
				uuid : $scope.entity.uuid,
				name : $scope.entity.name,
				description : $scope.entity.description,
				resource : self.module_name
			};
			params = self.appendBaseParams(params);
			EntityRestFactory.saveOrUpdateEntity(params, self.onChangeEntitySuccessful, self.onChangeEntityError);
		}

		self.retireOrUnretireCall = self.retireOrUnretireCall || function(retire) {
			var params = {
				uuid : $scope.entity.uuid,
				retireReason : $scope.entity.retireReason,
				retired : $scope.entity.retired
			};
			params = self.appendBaseParams(params);
			EntityRestFactory.retireOrUnretireEntity(params, self.onChangeEntitySuccessful, self.onChangeEntityError);
		}

		self.purge = self.purge || function() {
			var params = {
				uuid : $scope.entity.uuid,
				purge : true
			};
			params = self.appendBaseParams(params);
			EntityRestFactory.purgeEntity(params, self.onPurgeEntitySuccessful, self.onChangeEntityError);
		}

		self.loadEntity = self.loadEntity || function(uuid) {
			if (angular.isDefined(uuid) && uuid !== "") {
				var params = {
					uuid : uuid
				};
				params = self.appendBaseParams(params);
				EntityRestFactory.loadEntity(params, self.onLoadEntitySuccessful, self.onLoadEntityError);
			} else {
				var entity = GenericMetadataModel.newModelInstance();
				self.bindEntityToScope($scope, entity);
			}
		}

		/* #### START CALLBACK Methods #### */
		self.onChangeEntitySuccessful = self.onChangeEntitySuccessful || function(data) {
			if (angular.isDefined(data) && angular.isDefined(data.uuid)) {
				self.loadEntity(data.uuid);
			} else {
				self.bindExtraVariablesToScope("");
			}
		}

		self.onChangeEntityError = self.onChangeEntityError || function(error) {
			console.error(error);
			emr.errorMessage(error);
		}

		self.onPurgeEntitySuccessful = self.onPurgeEntitySuccessful || function(data) {
			self.cancel();
		}

		self.onLoadEntitySuccessful = self.onLoadEntitySuccessful || function(data) {
			var entity = GenericMetadataModel.populateModel(data);
			self.bindEntityToScope($scope, entity);
			self.bindExtraVariablesToScope(entity.uuid);
		}

		self.onLoadEntityError = self.onLoadEntityError || function(error) {
			var entity = InstitutionModel.newModelInstance();
			self.bindEntityToScope($scope, entity);
			var msg = $filter('EmrFormat')(emr.message("openhmis.inventory.general.error.notFound"), [ self.entity_name ]);
			emr.errorMessage(msg);
		}
		/* #### END CALLBACK Methods #### */

		self.cancel = function() {
			window.location = "institutions.page";
		}

		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope || function(uuid) {
			if (uuid === null || uuid === undefined || uuid === "") {
				$scope.h2SubString = $filter('EmrFormat')(emr.message("openhmis.inventory.general.new"), [ self.entity_name ]);
			} else {
				$scope.h2SubString = emr.message("general.edit") + ' ' + self.entity_name;
			}
			if (angular.isDefined($scope.entity) && angular.isDefined($scope.entity.retired) && $scope.entity.retired === true) {
				$scope.retireOrUnretire = $filter('EmrFormat')(emr.message("openhmis.inventory.general.unretire"), [ self.entity_name ]);
			} else {
				$scope.retireOrUnretire = $filter('EmrFormat')(emr.message("openhmis.inventory.general.retire"), [ self.entity_name ]);
			}
		}

		self.appendBaseParams = self.appendBaseParams || function(params) {
			if (params) {
				params['entity_name'] = self.entity_rest_name;
				return params;
			}
		}

		self.initialize = self.initialize || function() {
			self.uuid = self.getUuid();
			self.getModuleAndEntityName();
			EntityRestFactory.setBaseUrl(self.module_name);
			$scope.cancel = self.cancel;
			$scope.purge = self.purge;
			$scope.saveOrUpdate = self.saveOrUpdate;
			$scope.retireOrUnretireCall = self.retireOrUnretireCall;
			self.bindExtraVariablesToScope('');
			$scope.deleteForeverMsg = $filter('EmrFormat')(emr.message("openhmis.inventory.general.delete"), [ self.entity_name ]);
		}

		self.loadPage = self.loadPage || function() {
			self.initialize();
			self.loadEntity(self.uuid);
		}

		/* ENTRY POINT */
		self.loadPage();
	}

	baseController.GenericEntityController = GenericEntityController;
})();
