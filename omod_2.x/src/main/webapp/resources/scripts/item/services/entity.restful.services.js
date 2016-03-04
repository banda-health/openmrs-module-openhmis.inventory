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

	ItemRestfulService.$inject = ['EntityRestFactory'];

	function ItemRestfulService(EntityRestFactory) {
		var service;

		service = {
			searchItems : searchItems,
			loadDepartments : loadDepartments,
			searchConcepts : searchConcepts,
			loadItemStock : loadItemStock,
			loadItemAttributeTypes : loadItemAttributeTypes,
		};

		return service;

		function searchItems(q, startIndex, limit, department_uuid, onLoadSuccessfulCallback){
			var requestParams = [];
			requestParams['rest_entity_name'] = 'item';
			if(angular.isDefined(department_uuid)){
				requestParams['department_uuid'] = department_uuid;
			}

			if(angular.isDefined(q) && q !== ''){
				requestParams['q'] = q;
			}
			else{
				// always pass 'q' when 'department_uuid' is set.
				if(angular.isDefined('department_uuid')){
					requestParams['q'] = q;
				}
			}

			requestParams['startIndex'] = startIndex;
			requestParams['limit'] = limit;

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
		 * @param onSearchConceptsSuccessful
		 */
		function searchConcepts(module_name, q, onSearchConceptsSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = '';
			requestParams['q'] = q;
			requestParams['limit'] = 10;
			EntityRestFactory.setBaseUrl('concept', 'v1');
			EntityRestFactory.loadEntities(requestParams,
					onSearchConceptsSuccessful, errorCallback);
			//reset base url..
			EntityRestFactory.setBaseUrl(module_name);
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
			console.log(error);
		}
	}
})();
