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

	CreateOperationRestfulService.$inject = ['EntityRestFactory', 'PaginationService'];

	function CreateOperationRestfulService(EntityRestFactory, PaginationService) {
		var service;

		service = {
			loadStockOperationTypes: loadStockOperationTypes,
			loadStockrooms: loadStockrooms,
			loadInstitutions : loadInstitutions,
			loadDepartments : loadDepartments,
			loadOperationTypeAttributes : loadOperationTypeAttributes,
			loadStockOperations : loadStockOperations,
			searchStockOperationItems: searchStockOperationItems,
			searchPatients : searchPatients,
			loadVisit : loadVisit,
			endVisit : endVisit,
			searchItemStock : searchItemStock,
			isOperationNumberGenerated : isOperationNumberGenerated,
		};

		return service

		function searchStockOperationItems(module_name, q){
			var requestParams = {};
			requestParams['has_physical_inventory'] = 'true';
			requestParams['q'] = q;
			requestParams['limit'] = 10;
			requestParams['startIndex'] = 1;

			return EntityRestFactory.autocompleteSearch(requestParams, 'item', module_name);
		}

		/**
		 * load list of operation types
		 * @param rest_entity_name
		 * @param successCallback
		 */
		function loadStockOperationTypes(successCallback){
			var requestParams = {};
			requestParams['rest_entity_name'] = 'stockOperationType';
			requestParams['limit'] = 100;
			EntityRestFactory.loadEntities(requestParams,successCallback, errorCallback);
		}

		/**
		 * load list of stockrooms
		 * @param rest_entity_name
		 * @param successCallback
		 */
		function loadStockrooms(successCallback){
			var requestParams = {};
			requestParams['rest_entity_name'] = 'stockroom';
			requestParams['limit'] = 100;
			EntityRestFactory.loadEntities(requestParams, successCallback, errorCallback);
		}

		function loadInstitutions(successCallback){
			var requestParams = {};
			requestParams['rest_entity_name'] = 'institution';
			requestParams['limit'] = 100;
			EntityRestFactory.loadEntities(requestParams, successCallback, errorCallback);
		}

		function loadDepartments(successCallback){
			var requestParams = {};
			requestParams['rest_entity_name'] = 'department';
			requestParams['limit'] = 100;
			EntityRestFactory.loadEntities(requestParams, successCallback, errorCallback);
		}

		function loadOperationTypeAttributes(uuid, onLoadAttributeTypesSuccessful) {
			if(uuid !== undefined){
				var requestParams = [];
				requestParams['rest_entity_name'] = 'stockOperationType/' + uuid;
				EntityRestFactory.loadEntities(requestParams,
					onLoadAttributeTypesSuccessful, errorCallback);
			}
		}

		function loadStockOperations(operation_date, onLoadStockOperationSuccessful){
			if(operation_date !== undefined){
				var requestParams = [];
				requestParams['rest_entity_name'] = 'stockOperation';
				requestParams['operation_date'] = operation_date;
				EntityRestFactory.loadEntities(requestParams,
					onLoadStockOperationSuccessful, errorCallback);
			}
		}

		/**
		 * The Patient search webservice doesn't work right with pagination -- needs to be fixed.
		 * @link https://talk.openmrs.org/t/patient-search-webservice-pagination-doesnt-work/6328
		 * @param module_name
		 * @param q
		 * @param currentPage
		 * @param limit
		 * @param onLoadPatientsSuccessful
		 */
		function searchPatients(module_name, q, currentPage, limit, onLoadPatientsSuccessful){
			var requestParams = []; // PaginationService.paginateParams(currentPage, limit, false, '');
			requestParams['q'] = q;
			requestParams['rest_entity_name'] = '';
			requestParams['v'] = "custom:(uuid,patientIdentifier:(uuid,identifier)," +
				"person:(gender,age,birthdate,birthdateEstimated,personName))";
			EntityRestFactory.setBaseUrl('patient', 'v1');
			EntityRestFactory.loadEntities(requestParams,
				onLoadPatientsSuccessful, errorCallback);
			//reset base url..
			EntityRestFactory.setBaseUrl(module_name);
		}

		function loadVisit(module_name, patient_uuid, onLoadVisitSuccessful){
			var requestParams = [];
			requestParams['rest_entity_name'] = '';
			requestParams['patient'] = patient_uuid;
			EntityRestFactory.setBaseUrl('visit', 'v1');
			EntityRestFactory.loadEntities(requestParams,
				onLoadVisitSuccessful, errorCallback);
			//reset base url..
			EntityRestFactory.setBaseUrl(module_name);
		}

		function isOperationNumberGenerated(module_name, onLoadOpNumGenSuccessful){
			var requestParams = [];
			requestParams['resource'] = 'module/openhmis/inventory/moduleSettings.page';
			requestParams['setting'] = 'isOperationNumberGenerated';
			EntityRestFactory.setCustomBaseUrl('/' + OPENMRS_CONTEXT_PATH + '/');
			EntityRestFactory.loadResults(requestParams,
				onLoadOpNumGenSuccessful, errorCallback);

			//reset base url..
			EntityRestFactory.setBaseUrl(module_name);
		}

		function endVisit(module_name, visit_uuid, stopDatetime, onLoadEndVisitSuccessful){
			var requestParams = {};
			requestParams['stopDatetime']  = stopDatetime.toString();
			EntityRestFactory.setBaseUrl('', 'v1');
			EntityRestFactory.post('visit', visit_uuid, requestParams,
				onLoadEndVisitSuccessful,
				errorCallback
			);
			//reset base url..
			EntityRestFactory.setBaseUrl(module_name);
		}

		function searchItemStock(item_uuid, stockroom_uuid, onLoadItemStockSuccessful) {
			if (angular.isDefined(item_uuid) && angular.isDefined(stockroom_uuid)) {
				var requestParams = [];
				requestParams['rest_entity_name'] = 'itemStock';
				requestParams['item_uuid'] = item_uuid;
				requestParams['stockroom_uuid'] = stockroom_uuid;
				EntityRestFactory.loadEntities(requestParams,
					onLoadItemStockSuccessful, errorCallback);
			}
		}

		function errorCallback(error){
			console.log(error);
		}
	}
})();
