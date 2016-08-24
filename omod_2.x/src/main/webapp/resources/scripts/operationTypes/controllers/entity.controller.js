/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 *
 */

(function () {
	'use strict';

	var base = angular.module('app.genericEntityController');
	base.controller("OperationTypesController", OperationTypesController);
	OperationTypesController.$inject = ['$stateParams', '$injector', '$scope',
		'$filter', 'EntityRestFactory', 'OperationTypesModel', 'ngDialog',
		'OperationsTypeFunctions', 'OperationTypesRestfulService', 'EntityFunctions'];

	function OperationTypesController($stateParams, $injector, $scope, $filter,
	                                  EntityRestFactory, OperationTypesModel, ngDialog,
	                                  OperationsTypeFunctions, OperationTypesRestfulService, EntityFunctions) {

		var self = this;

		var entity_name_message_key = "openhmis.inventory.operations.type.name";
		var REST_ENTITY_NAME = "stockOperationType";

		// @Override
		self.setRequiredInitParameters = self.setRequiredInitParameters
			|| function () {
				self.bindBaseParameters(INVENTORY_MODULE_NAME, REST_ENTITY_NAME,
					entity_name_message_key, RELATIVE_CANCEL_PAGE_URL);
				self.checkPrivileges(TASK_MANAGE_METADATA);
			}

		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
			|| function (uuid) {
				var usersLimit = null;
				var rolesLimit = null;
				$scope.attributeType = {};
				OperationTypesRestfulService.loadUsers(INVENTORY_MODULE_NAME, usersLimit, self.onLoadUsersSuccessful);
				OperationTypesRestfulService.loadRoles(INVENTORY_MODULE_NAME, rolesLimit, self.onLoadRolesSuccessful);
				OperationTypesRestfulService.loadFormatFields(INVENTORY_MODULE_NAME, self.onLoadFormatFieldsSuccessful);

				// open dialog box to add an attribute type
				$scope.addAttributeType = function () {
					$scope.editAttributeTypeTitle = '';
					$scope.addAttributeTypeTitle = $scope.messageLabels['openhmis.commons.general.add']
						+ ' '
						+ $scope.messageLabels['openhmis.inventory.attribute.type.name'];
					EntityFunctions.addAttributeType($scope);
					EntityFunctions.disableBackground();
				}

				// deletes an attribute type
				$scope.removeAttributeType = function (attributeType) {
					EntityFunctions.removeAttributeType(attributeType, $scope.entity.attributeTypes);
				}

				// open dialog box to edit an attribute type
				$scope.editAttributeType = function (attributeType) {
					$scope.editAttributeTypeTitle = $scope.messageLabels['openhmis.commons.general.edit']
						+ ' '
						+ $scope.messageLabels['openhmis.inventory.attribute.type.name'];
					$scope.addAttributeTypeTitle = '';
					EntityFunctions.editAttributeType(attributeType, $scope);
					EntityFunctions.disableBackground();
				}

				$scope.delete = self.delete;
			}
		
		// @Override
		self.onChangeEntityError = self.onChangeEntityError || function (error) {
				$scope.loading = false;
				if(error.indexOf("inv_stock_operation_attribute_type") != -1){
					emr.errorAlert("openhmis.inventory.general.attributeTypeInUse.error");
				}
				else{
					emr.errorAlert(error);
				}
			}
		
		// call-back functions.
		self.onLoadUsersSuccessful = self.onLoadUsersSuccessful || function (data) {
				$scope.users = data.results;
			}

		self.onLoadRolesSuccessful = self.onLoadRolesSuccessful || function (data) {
				$scope.roles = data.results;
			}

		// @Override
		self.setAdditionalMessageLabels = self.setAdditionalMessageLabels || function () {
				return OperationsTypeFunctions.addMessageLabels();
			}

		// call-back functions.
		self.onLoadFormatFieldsSuccessful = self.onLoadFormatFieldsSuccessful || function (data) {
				$scope.formatFields = data.results;
				return EntityFunctions.addExtraFormatListElements($scope.formatFields);
			}

		/**
		 * All post-submit validations are done here.
		 * @return boolean
		 */
			// @Override
		self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function () {
				if (!angular.isDefined($scope.entity.name) || $scope.entity.name === '') {
					$scope.submitted = true;
					return false;
				}
				if (!angular.isDefined($scope.entity.attributeTypes)
						|| $scope.entity.attributeTypes.format === '') {
					$scope.submmited = true;
					return false;
				}
				if (!angular.isDefined($scope.entity.attributeTypes.attributeOrder)
						|| $scope.entity.attributeTypes.attributeOrder === '') {
					$scope.entity.attributeTypes.attributeOrder === null;
				}
				// remove temporarily assigned ids from the attribute type array lists.
				self.removeTemporaryIds();
				
				// validate attribute types.
				if($scope.entity.attributeTypes === ''){
					$scope.entity.attributeTypes = null;
				}

				$scope.loading = true;
				return true;
			}

		/**
		 * removes the 'attributes' parameter from the entity object before purging.
		 * @type {Function}
		 */
		self.delete = self.delete || function () {
				$scope.purge();
			}

		/**
		 * Removes the temporarily assigned unique ids before POSTing data
		 * @type {Function}
		 */
		self.removeTemporaryIds = self.removeTemporaryIds || function () {
				EntityFunctions.removeTemporaryId($scope.entity.attributeTypes);
			}


		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, self, {
			$scope: $scope,
			$filter: $filter,
			$stateParams: $stateParams,
			EntityRestFactory: EntityRestFactory,
			GenericMetadataModel: OperationTypesModel
		});
	}
})();
