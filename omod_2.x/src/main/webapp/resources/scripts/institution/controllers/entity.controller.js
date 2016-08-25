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

(function() {
	'use strict';

	var base = angular.module('app.genericEntityController');
	base.controller("InstitutionController", InstitutionController);
	InstitutionController.$inject = ['$stateParams', '$injector', '$scope',
			'$filter', 'EntityRestFactory', 'InstitutionModel'];

	function InstitutionController($stateParams, $injector, $scope, $filter,
			EntityRestFactory, InstitutionModel) {

		var self = this;

		var entity_name_message_key = "openhmis.inventory.institution.name";
		var REST_ENTITY_NAME = "institution";

		// @Override
		self.setRequiredInitParameters = self.setRequiredInitParameters
				|| function() {
					self.bindBaseParameters(INVENTORY_MODULE_NAME, REST_ENTITY_NAME,
						entity_name_message_key, RELATIVE_CANCEL_PAGE_URL);
					self.checkPrivileges(TASK_MANAGE_METADATA);
				}

		/**
		 * All post-submit validations are done here.
		 * @return boolean
		 */
		// @Override
		self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function() {
				if (!angular.isDefined($scope.entity.name) || $scope.entity.name === '') {
					$scope.submitted = true;
					return false;
				}

				$scope.loading = true;
				return true;
			};

		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, self, {
			$scope : $scope,
			$filter : $filter,
			$stateParams : $stateParams,
			EntityRestFactory : EntityRestFactory,
			GenericMetadataModel : InstitutionModel
		});
	}
})();
