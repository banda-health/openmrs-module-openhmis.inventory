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

	angular.module('app.restfulServices').service('ReportRestfulService',
		ReportRestfulService);

	ReportRestfulService.$inject = ['EntityRestFactory'];

	function ReportRestfulService(EntityRestFactory) {
		var service;
		service = {
			loadStockRooms: loadStockRooms,
			searchReportItems: searchReportItems
		};
		return service;

		/**
		 * load list of stockrooms
		 * @param rest_entity_name
		 * @param successCallback
		 */
		function loadStockRooms(rest_entity_name, successCallback) {
			var requestParams = {};
			requestParams['rest_entity_name'] = rest_entity_name;
			EntityRestFactory.loadEntities(requestParams, successCallback, function(error){console.log(error);});
		}

		function searchReportItems(module_name, q){
			var requestParams = {};
			requestParams['has_physical_inventory'] = 'true';
			requestParams['q'] = q;
			requestParams['limit'] = 10;
			requestParams['startIndex'] = 1;

			return EntityRestFactory.autocompleteSearch(requestParams, 'item', module_name);
		}
	}
})();
