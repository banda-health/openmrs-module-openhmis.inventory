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
	
	angular.module('app.restfulServices').service('StockTakeRestfulService', StockTakeRestfulService);
	
	StockTakeRestfulService.$inject = ['EntityRestFactory', 'PaginationService'];
	
	function StockTakeRestfulService(EntityRestFactory, PaginationService) {
		var service;
		
		service = {
			loadStockrooms: loadStockrooms,
			loadStockDetails: loadStockDetails
		};
		
		return service;
		
		/**
		 * Retrieve all Stockrooms
		 * @param onLoadStockroomsSuccessful
		 */
		function loadStockrooms(module_name, onLoadStockroomsSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = 'stockroom';
			EntityRestFactory.loadEntities(requestParams,
				onLoadStockroomsSuccessful,
				errorCallback
			);
		}
		
		/**
		 * Retrieve all the stock in the selected stockroom
		 * @param stockroom-uuid
		 * */
		function loadStockDetails(module_name, onLoadStockDetailsSuccessful, stockroomUuid) {
			var requestParams = [];
			requestParams['rest_entity_name'] = 'inventoryStockTakeSummary';
			requestParams['stockroom_uuid'] = stockroomUuid;
			EntityRestFactory.loadEntities(requestParams,
				onLoadStockDetailsSuccessful,
				errorCallback
			);
		}
		
		function errorCallback(error) {
			console.log(error);
		}
	}
})();
