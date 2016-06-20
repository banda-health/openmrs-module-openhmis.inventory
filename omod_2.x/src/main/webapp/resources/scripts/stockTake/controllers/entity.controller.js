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
	base.controller("StockTakeController", StockTakeController);
	StockTakeController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory',
		'StockTakeModel', 'StockTakeRestfulService', 'PaginationService', 'EntityFunctions', 'StockTakeFunctions',
		'CookiesService'];
	
	function StockTakeController($stateParams, $injector, $scope, $filter, EntityRestFactory, StockTakeModel,
	                             StockTakeRestfulService, PaginationService, EntityFunctions, StockTakeFunctions,
	                             CookiesService) {
		var self = this;
		var module_name = 'inventory';
		var entity_name_message_key = emr.message("openhmis.inventory.admin.stockTake");
		var cancel_page = 'entity.page';
		var rest_entity_name = emr.message("openhmis.inventory.stocktake.rest_name");
		
		// @Override
		self.setRequiredInitParameters = self.setRequiredInitParameters || function () {
				self.bindBaseParameters(module_name, rest_entity_name, entity_name_message_key, cancel_page);
			}
		
		/**
		 * Initializes and binds any required variable and/or function specific to entity.page
		 * @type {Function}
		 */
		// @Override
		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
			|| function (uuid) {
				self.loadStockrooms();
				self.showStockDetails = false;
				$scope.loadStockDetails = function () {
					if ($scope.entity.stockroom.uuid != null) {
						self.loadStockDetails($scope.entity.stockroom.uuid);
					}
				}
			}
		
		self.loadStockrooms = self.loadStockrooms || function () {
				StockTakeRestfulService.loadStockrooms(module_name, self.onLoadStockroomsSuccessful);
			}
		
		self.loadStockDetails = self.loadStockDetails || function (stockroomUuid) {
				StockTakeRestfulService.loadStockDetails(module_name, self.onLoadStockDetailsSuccessful, stockroomUuid)
			}
		
		//callback
		self.onLoadStockroomsSuccessful = self.onLoadStockroomsSuccessful || function (data) {
				$scope.stockrooms = data.results;
			}
		
		self.onLoadStockDetailsSuccessful = self.onLoadStockDetailsSuccessful || function (data) {
				$scope.fetchedEntities = data.results;
				$scope.totalNumOfResults = data.results.length;
				if (data.results.length) {
					$scope.showStockDetails = true
				} else {
					$scope.showStockDetails = false;
				}
			}
		
		/**
		 * All post-submit validations are done here.
		 * @return boolean
		 */
		// @Override
		self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function () {
				return true;
			}
		
		// @Override
		self.setAdditionalMessageLabels = self.setAdditionalMessageLabels || function () {
				
				return StockTakeFunctions.addMessageLabels();
			}
		
		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, self, {
			$scope: $scope,
			$filter: $filter,
			$stateParams: $stateParams,
			EntityRestFactory: EntityRestFactory,
			PaginationService: PaginationService,
			GenericMetadataModel: StockTakeModel,
			EntityFunctions: EntityFunctions
		});
	}
})();
