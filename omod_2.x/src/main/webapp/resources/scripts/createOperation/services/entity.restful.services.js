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

	angular.module('app.restfulServices').service('CreateOperationRestfulService', CreateOperationRestfulService);
	
	CreateOperationRestfulService.$inject = ['EntityRestFactory'];

	function CreateOperationRestfulService(EntityRestFactory) {
		var service;

		service = {
			loadStockOperationTypes: loadStockOperationTypes,
			loadStockrooms: loadStockrooms,
			loadInstitutions: loadInstitutions,
			loadDepartments: loadDepartments,
			loadOperationTypeAttributes: loadOperationTypeAttributes,
			loadStockOperations: loadStockOperations,
			searchItemStock: searchItemStock,
			isOperationNumberGenerated: isOperationNumberGenerated,
			isNegativeStockRestricted: isNegativeStockRestricted,
			searchStockOperationItems: searchStockOperationItems,
		};

		return service

		/**
		 * load list of operation types
		 * @param rest_entity_name
		 * @param successCallback
		 */
		function loadStockOperationTypes(module_name, successCallback) {
			setBaseUrl(module_name);
			var requestParams = {};
			requestParams['rest_entity_name'] = 'stockOperationType';
			requestParams['v'] = 'full';
			requestParams['limit'] = 100;
			EntityRestFactory.loadEntities(requestParams, successCallback, errorCallback);
		}

		/**
		 * load list of stockrooms
		 * @param rest_entity_name
		 * @param successCallback
		 */
		function loadStockrooms(module_name, successCallback) {
			setBaseUrl(module_name);
			var requestParams = {};
			requestParams['rest_entity_name'] = 'stockroom';
			requestParams['limit'] = 100;
			EntityRestFactory.loadEntities(requestParams, successCallback, errorCallback);
		}

		function loadInstitutions(module_name, successCallback) {
			setBaseUrl(module_name);
			var requestParams = {};
			requestParams['rest_entity_name'] = 'institution';
			requestParams['limit'] = 100;
			EntityRestFactory.loadEntities(requestParams, successCallback, errorCallback);
		}

		function loadDepartments(module_name, successCallback) {
			setBaseUrl(module_name);
			var requestParams = {};
			requestParams['rest_entity_name'] = 'department';
			requestParams['limit'] = 100;
			EntityRestFactory.loadEntities(requestParams, successCallback, errorCallback);
		}

		function loadOperationTypeAttributes(module_name, uuid, onLoadAttributeTypesSuccessful) {
			if (uuid !== undefined) {
				setBaseUrl(module_name);
				var requestParams = [];
				requestParams['rest_entity_name'] = 'stockOperationType/' + uuid;
				EntityRestFactory.loadEntities(requestParams,
					onLoadAttributeTypesSuccessful, errorCallback);
			}
		}

		function loadStockOperations(module_name, operation_date, onLoadStockOperationSuccessful) {
			if (operation_date !== undefined) {
				setBaseUrl(module_name);
				var requestParams = [];
				requestParams['rest_entity_name'] = 'stockOperation';
				requestParams['operation_date'] = operation_date;
				EntityRestFactory.loadEntities(requestParams,
					onLoadStockOperationSuccessful, errorCallback);
			}
		}

		function isOperationNumberGenerated(onLoadOpNumGenSuccessful) {
			var requestParams = [];
			requestParams['resource'] = INVENTORY_MODULE_SETTINGS_URL;
			requestParams['setting'] = 'openhmis.inventory.autoGenerateOperationNumber';
			EntityRestFactory.setCustomBaseUrl(ROOT_URL);
			EntityRestFactory.loadResults(requestParams,
				onLoadOpNumGenSuccessful, errorCallback);
		}

		function isNegativeStockRestricted(onLoadNegativeStockSuccessful) {
			var requestParams = [];
			requestParams['resource'] = INVENTORY_MODULE_SETTINGS_URL;
			requestParams['setting'] = 'openhmis.inventory.restrictNegativeInventoryStockCreation';
			EntityRestFactory.setCustomBaseUrl(ROOT_URL);
			EntityRestFactory.loadResults(requestParams,
				onLoadNegativeStockSuccessful, errorCallback);
		}

		function searchItemStock(module_name, item_uuid, stockroom_uuid, onLoadItemStockSuccessful) {
			if (angular.isDefined(item_uuid) && angular.isDefined(stockroom_uuid)) {
				setBaseUrl(module_name);
				var requestParams = [];
				requestParams['rest_entity_name'] = 'itemStock';
				requestParams['item_uuid'] = item_uuid;
				requestParams['stockroom_uuid'] = stockroom_uuid;
				EntityRestFactory.loadEntities(requestParams,
					onLoadItemStockSuccessful, errorCallback);
			}
		}

		function searchStockOperationItems(module_name, q) {
			setBaseUrl(module_name);
			var requestParams = {};
			requestParams['has_physical_inventory'] = 'true';
			requestParams['q'] = q;
			requestParams['limit'] = 10;
			requestParams['startIndex'] = 1;

			return EntityRestFactory.autocompleteSearch(requestParams, 'item', module_name);
		}

		function setBaseUrl(module_name) {
			EntityRestFactory.setBaseUrl(module_name);
		}

		function errorCallback(error) {
			emr.errorAlert(error);
		}
	}
})();
