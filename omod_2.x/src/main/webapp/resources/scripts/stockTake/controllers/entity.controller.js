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
		var entity_name_message_key = "openhmis.inventory.admin.stockTake";
		var REST_ENTITY_NAME = "inventoryStockTake";
		
		// @Override
		self.setRequiredInitParameters = self.setRequiredInitParameters || function () {
				self.bindBaseParameters(INVENTORY_MODULE_NAME, REST_ENTITY_NAME, entity_name_message_key, INVENTORY_TASK_DASHBOARD_PAGE_URL);
				self.checkPrivileges(TASK_ACCESS_STOCK_TAKE_PAGE);
			}
		
		/**
		 * Initializes and binds any required variable and/or function specific to entity.page
		 * @type {Function}
		 */
		// @Override
		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
			|| function (uuid) {
				self.loadStockrooms();
				$scope.showNoStockroomSelected = true;
				$scope.showNoStockSummaries = false;
				$scope.showStockDetails = false;
				$scope.showStockChangeDetails = false;
				$scope.showStockDetailsTable = false;
				$scope.stockTakeDetails = [];
				$scope.loading = false;
				
				$scope.stockroomDialog = function (stockroomChange, stockTakeCurrentPage) {
					if ($scope.stockTakeDetails.length != 0) {
						$scope.stockTakeDetails = [];
						self.stockroomChangeDialog(stockroomChange);
					} else {
						$scope.loadStockDetails(stockTakeCurrentPage);
					}
				}
				
				$scope.loadStockDetails = function (stockTakeCurrentPage) {
					if ($scope.entity.stockroom != null) {
						var stockroom_uuid = $scope.entity.stockroom.uuid;
						self.loadStockDetails(stockroom_uuid, stockTakeCurrentPage);
						
						$scope.stockTakeLimit = CookiesService.get(stockroom_uuid + 'stockTakeLimit') || 5;
						$scope.stockTakeCurrentPage = CookiesService.get(stockroom_uuid + 'stockTakeCurrentPage') || 1;
						$scope.stockTakePagingFrom = PaginationService.pagingFrom;
						$scope.stockTakePagingTo = PaginationService.pagingTo;
					} else {
						$scope.showNoStockroomSelected = true;
						$scope.showNoStockSummaries = false;
						$scope.showStockChangeDetails = false;
						$scope.stockTakeDetails = [];
						$scope.showStockDetails = false;
						$scope.showStockDetailsTable = false;
					}
				}
				
				$scope.showTableDetails = function () {
					$scope.showStockDetailsTable = true;
				}
				
				$scope.hideTableDetails = function () {
					$scope.showStockDetailsTable = false;
				}
				
				$scope.getActualQuantity = function (entity) {
					if (entity.actualQuantity >= 0) {
						entity.id = entity.item.uuid + "_" + entity.expiration;
						self.getNewStock(entity);
					}
				}
				
			}
		
		self.stockroomChangeDialog = self.stockroomChangeDialog || function (id) {
				StockTakeFunctions.stockroomChangeDialog(id, $scope);
			}
		
		self.getNewStock = self.getNewStock || function (newStock) {
				console.log($scope.stockTakeDetails);
				var index = EntityFunctions.findIndexByKeyValue($scope.stockTakeDetails,newStock.id);
				if (index < 0 ) {
					$scope.stockTakeDetails.push(newStock);
				} else {
					$scope.stockTakeDetails[index] = newStock;
				}
				
				/*
				* This loop is to remove any stock that had the actualQuantity updated and before saving changed again to either a value
				* equal to null or a value equal to the quantity
				* */
				for (var i = 0; i < $scope.stockTakeDetails.length; i++) {
					if ($scope.stockTakeDetails[i].actualQuantity == $scope.stockTakeDetails[i].quantity || $scope.stockTakeDetails[i].actualQuantity == null) {
						$scope.stockTakeDetails.splice(i, 1);
					}
				}
				
				if ($scope.stockTakeDetails.length > 0) {
					$scope.showStockChangeDetails = true;
				} else {
					$scope.showStockDetailsTable = false;
				}
				$scope.stockTakeDetails = $filter('orderBy')($scope.stockTakeDetails, ['item.name', 'expiration']);
			}
		
		self.loadStockrooms = self.loadStockrooms || function () {
				StockTakeRestfulService.loadStockrooms(INVENTORY_MODULE_NAME, self.onLoadStockroomsSuccessful);
			}
		
		self.loadStockDetails = self.loadStockDetails || function (stockroomUuid, stockTakeCurrentPage) {
				stockTakeCurrentPage = stockTakeCurrentPage || $scope.stockTakeCurrentPage;
				CookiesService.set(stockroomUuid + 'stockTakeCurrentPage', stockTakeCurrentPage);
				CookiesService.set(stockroomUuid + 'stockTakeLimit', $scope.stockTakeLimit);
				
				StockTakeRestfulService.loadStockDetails(stockroomUuid, CookiesService.get(stockroomUuid + 'stockTakeCurrentPage'),
					CookiesService.get(stockroomUuid + 'stockTakeLimit'),
					self.onLoadStockDetailsSuccessful);
			}
		
		//callback
		self.onLoadStockroomsSuccessful = self.onLoadStockroomsSuccessful || function (data) {
				$scope.stockrooms = data.results;
			}
		
		self.onLoadStockDetailsSuccessful = self.onLoadStockDetailsSuccessful || function (data) {
				$scope.fetchedEntities = data.results;
				
				for (var i = 0; i < $scope.fetchedEntities.length; i++) {
					$scope.fetchedEntities[i].id = $scope.fetchedEntities[i].item.uuid + "_" + $scope.fetchedEntities[i].expiration;
					var index = EntityFunctions.findIndexByKeyValue($scope.stockTakeDetails,$scope.fetchedEntities[i].id);
					if (index > -1) {
						$scope.fetchedEntities[i].actualQuantity = $scope.stockTakeDetails[index].actualQuantity;
					}
				}
				
				$scope.totalNumOfResults = data.length;
				
				if (data.results.length != 0) {
					$scope.showStockDetails = true;
					$scope.showNoStockroomSelected = false;
					$scope.showNoStockSummaries = false;
				} else {
					$scope.showNoStockroomSelected = false;
					$scope.showNoStockSummaries = true;
					$scope.showStockDetails = false;
				}
			}
		
		self.onChangeEntityError = self.onChangeEntityError || function (error) {
				emr.errorAlert(error);
				$scope.loading = false;
			}
		
		/**
		 * All post-submit validations are done here.
		 * @return boolean
		 */
		// @Override
		self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function () {
				var stockObject = $scope.stockTakeDetails;
				for (var i = 0; i < stockObject.length; i++) {
					delete stockObject[i]['$$hashKey'];
					delete stockObject[i]['id'];
					if (stockObject[i].expiration != null) {
						stockObject[i].expiration = StockTakeFunctions.formatDate(stockObject[i].expiration);
					}

					stockObject[i].item = stockObject[i].item.uuid;
				}
				
				if ($scope.stockTakeDetails.length != 0) {
					$scope.entity = {
						'itemStockSummaryList': stockObject,
						"operationNumber": "",
						"stockroom": $scope.entity.stockroom.uuid
					};
					$scope.loading = true;
				} else {
					emr.errorAlert("openhmis.inventory.stocktake.adjustment.empty.error");
					return false;
				}
				
				return true;
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
