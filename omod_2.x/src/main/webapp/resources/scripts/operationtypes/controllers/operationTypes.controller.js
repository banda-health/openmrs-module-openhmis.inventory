(function() {
	'use strict';

	var base = angular.module('app.genericEntityController');
	base.controller("OperationTypesController", OperationTypesController);
	OperationTypesController.$inject = ['$stateParams', '$injector', '$scope',
			'$filter', 'EntityRestFactory', 'OperationTypesModel', 'ngDialog',
			'OperationsTypeFunctions', 'OperationTypesRestfulService'];

	function OperationTypesController($stateParams, $injector, $scope, $filter,
			EntityRestFactory, OperationTypesModel, ngDialog,
			OperationsTypeFunctions, OperationTypesRestfulService) {

		var self = this;

		var module_name = 'inventory';
		var entity_name = emr.message("openhmis.inventory.operations.type.name");
		var rest_entity_name = emr.message("openhmis.inventory.operations.type.name_rest");
		var cancel_page = 'operationTypes.page';

		// @Override
		self.setRequiredInitParameters = self.setRequiredInitParameters
				|| function() {
					self.bindBaseParameters(module_name, rest_entity_name,
							entity_name, cancel_page);
				}

		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
				|| function(uuid) {
					if (angular.isDefined($scope.entity)
							&& angular.isDefined($scope.entity.retired)
							&& $scope.entity.retired === true) {
						$scope.retireOrUnretire = $filter('EmrFormat')
								(emr.message("openhmis.inventory.general.unretire"), [self.entity_name]);
					} else {
						$scope.retireOrUnretire = $filter('EmrFormat')
								(emr.message("openhmis.inventory.general.retire"), [self.entity_name]);
					}

					var usersLimit = null;
					var rolesLimit = null;
					$scope.attributeType = {};
					OperationTypesRestfulService.loadUsers(module_name, usersLimit, self.onLoadUsersSuccessful);
					OperationTypesRestfulService.loadRoles(module_name, rolesLimit, self.onLoadRolesSuccessful);
					OperationTypesRestfulService.loadFormatFields(module_name, self.onLoadFormatFieldsSuccessful);

					// open dialog box to add an attribute type
					$scope.addAttributeType = function(){OperationsTypeFunctions.addAttributeType($scope);}

					// deletes an attribute type
					$scope.removeAttributeType = function(attributeType){
						OperationsTypeFunctions.removeAttributeType(attributeType, $scope.entity.attributeTypes);
					}

					// open dialog box to edit an attribute type
					$scope.editAttributeType = function(attributeType){
						OperationsTypeFunctions.editAttributeType(attributeType, $scope);
					}

					$scope.delete = self.delete;
				}

		// call-back functions.
		self.onLoadUsersSuccessful = self.onLoadUsersSuccessful || function(data) {
					$scope.users = data.results;
				}

		self.onLoadRolesSuccessful = self.onLoadRolesSuccessful || function(data) {
					$scope.roles = data.results;
				}

		// @Override
		self.setAdditionalMessageLabels = self.setAdditionalMessageLabels || function() {
					return OperationsTypeFunctions.addMessageLabels();
				}

		// call-back functions.
		self.onLoadFormatFieldsSuccessful = self.onLoadFormatFieldsSuccessful || function(data){
					$scope.formatFields = data.results;
				}

		/**
		 * removes the 'attributes' parameter from the entity object before purging.
		 * @type {Function}
		 */
		self.delete = self.delete || function(){
					delete $scope.entity.attributeTypes;
					$scope.purge();
				}

		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, self, {
			$scope : $scope,
			$filter : $filter,
			$stateParams : $stateParams,
			EntityRestFactory : EntityRestFactory,
			GenericMetadataModel : OperationTypesModel
		});
	}
})();
