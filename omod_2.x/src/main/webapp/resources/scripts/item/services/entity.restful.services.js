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

	angular.module('app.restfulServices').service('ItemRestfulService',
			ItemRestfulService);

	ItemRestfulService.$inject = ['EntityRestFactory', 'PaginationService'];

	function ItemRestfulService(EntityRestFactory, PaginationService) {
		var service;

		service = {
			searchItems : searchItems,
			loadDepartments : loadDepartments,
			searchConcepts : searchConcepts,
			loadItemStock : loadItemStock,
			loadItemAttributeTypes : loadItemAttributeTypes,
		};

		return service;

		function searchItems(q, startIndex, limit, department_uuid, includeRetired, onLoadSuccessfulCallback){
			var requestParams = PaginationService.paginateParams(startIndex, limit, includeRetired);
			requestParams['rest_entity_name'] = 'item';
			if(angular.isDefined(department_uuid)){
				requestParams['department_uuid'] = department_uuid;
			}

			if(angular.isDefined(q) && q !== '' && q !== null && q !== undefined){
				requestParams['q'] = q;
			}
			else if(angular.isDefined('department_uuid') && department_uuid !== undefined){
				requestParams['q'] = q;
			}

			EntityRestFactory.loadEntities(requestParams, onLoadSuccessfulCallback, errorCallback);
		}

		/**
		 * Temporary Function: It will ONLY be used until the Department module is done.
		 * @param onLoadDepartmentsSuccessful
		 */
		function loadDepartments(onLoadDepartmentsSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = 'department';
			EntityRestFactory.loadEntities(requestParams,
					onLoadDepartmentsSuccessful, errorCallback);
		}

		/**
		 * An auto-complete function to search concepts given a query term.
		 * @param module_name
		 * @param q - search term
		 * @param limit
		 */
		function searchConcepts(module_name, q) {
			var requestParams = [];
			requestParams['q'] = q;
			requestParams['limit'] = 10;
			return EntityRestFactory.autocompleteSearch(requestParams, 'concept', module_name, 'v1');
		}

		function loadItemAttributeTypes(onLoadAttributeTypesSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = 'itemAttributeType';
			EntityRestFactory.loadEntities(requestParams,
					onLoadAttributeTypesSuccessful, errorCallback);
		}

		/**
		 * Retrieve an item stock given a uuid.
		 * @param uuid
		 * @param onLoadItemStockSuccessful
		 */
		function loadItemStock(uuid, onLoadItemStockSuccessful) {
			if (angular.isDefined(uuid)) {
				var requestParams = [];
				requestParams['rest_entity_name'] = 'itemStock';
				requestParams['item_uuid'] = uuid;
				EntityRestFactory.loadEntities(requestParams,
						onLoadItemStockSuccessful, errorCallback);
			}
		}
		
		function errorCallback(error) {
			emr.errorAlert(error);
		}
	}
})();
