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

    angular.module('app.restfulServices').service('StockOperationRestfulService', StockOperationRestfulService);

    StockOperationRestfulService.$inject = ['EntityRestFactory', 'PaginationService'];

    function StockOperationRestfulService(EntityRestFactory, PaginationService) {
        var service;

        service = {
            stockroom: stockroom,
            stockOperation: stockOperation,
            searchStockOperation: searchStockOperation,
            stockOperationItem: stockOperationItem,
            stockOperationTransaction: stockOperationTransaction,
            invokeOperation: invokeOperation,
            loadStockOperationTypes: loadStockOperationTypes,
            loadStockRooms: loadStockRooms,
            searchStockOperationItems: searchStockOperationItems,
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
         * search stock operations
         * @param rest_entity_name
         * @param currentPage
         * @param limit
         * @param operationItem_uuid
         * @param operation_status
         * @param operationType_uuid
         * @param stockroom_uuid
         * @param successCallback
         */
        function searchStockOperation(rest_entity_name, currentPage, limit, operationItem_uuid,
                                      operation_status, operationType_uuid, stockroom_uuid,
                                      successCallback, myOperation){
            var requestParams = PaginationService.paginateParams(currentPage, limit, false, '');
            requestParams['rest_entity_name'] = rest_entity_name;

            if(angular.isDefined(operation_status) && operation_status !== undefined && operation_status !== ''){
                requestParams['operation_status'] = operation_status;
            }

            if(angular.isDefined(operationType_uuid) && operationType_uuid !== undefined && operationType_uuid !== ''){
                requestParams['operationType_uuid'] = operationType_uuid;
            }

            if(angular.isDefined(stockroom_uuid) && stockroom_uuid !== undefined && stockroom_uuid !== ''){
                requestParams['stockroom_uuid'] = stockroom_uuid;
            }

            if(angular.isDefined(operationItem_uuid) && operationItem_uuid !== undefined && operationItem_uuid !== '') {
                requestParams['operationItem_uuid'] = operationItem_uuid;
            }

            if(angular.isDefined(myOperation) && myOperation !== undefined){
                requestParams['q'] = 'my'
            }

            EntityRestFactory.loadEntities(requestParams,
                successCallback,
                errorCallback
            );
        }

        /**
         * load list of operation types
         * @param rest_entity_name
         * @param successCallback
         */
        function loadStockOperationTypes(rest_entity_name, successCallback){
            var requestParams = {};
            requestParams['rest_entity_name'] = rest_entity_name;
            EntityRestFactory.loadEntities(requestParams,successCallback, errorCallback);
        }

        /**
         * load list of stockrooms
         * @param rest_entity_name
         * @param successCallback
         */
        function loadStockRooms(rest_entity_name, successCallback){
            var requestParams = {};
            requestParams['rest_entity_name'] = rest_entity_name;
            EntityRestFactory.loadEntities(requestParams, successCallback, errorCallback);
        }

        /**
         * load stockrooms given an operation uuid
         * @param operation_uuid
         * @param currentPage
         * @param limit
         * @param q
         * @param successCallback
         */
        function stockroom(operation_uuid, currentPage, limit, q, successCallback){
            ws_call('stockroom', operation_uuid, currentPage, limit, q, successCallback);
        }

        /**
         * retrieve stock operations
         * @param operation_uuid
         * @param rest_entity_name
         * @param successCallback
         */
        function stockOperation(operation_uuid, rest_entity_name, successCallback){
            if(angular.isDefined(operation_uuid) && operation_uuid !== '' && operation_uuid !== undefined){
                var requestParams = {};
                requestParams['rest_entity_name'] = rest_entity_name + '/' + operation_uuid;

                EntityRestFactory.loadEntities(requestParams,
                    successCallback,
                    errorCallback
                );
            }
        }

        /**
         * invokeOperation a rollback, complete, cancel operation
         * @param operation_uuid
         * @param rest_entity_name
         * @param successCallback
         */
        function invokeOperation(status, operation_uuid, rest_entity_name, successCallback) {
            if (angular.isDefined(operation_uuid) && operation_uuid !== '' && operation_uuid !== undefined) {
                var requestParams = {};
                requestParams['status'] = status;

                EntityRestFactory.post(rest_entity_name, operation_uuid, requestParams,
                    successCallback,
                    errorCallback
                );

                successCallback(operation_uuid);
            }
        }

        /**
         * load stock operation items
         * @param operation_uuid
         * @param currentPage
         * @param limit
         * @param successCallback
         */
        function stockOperationItem(operation_uuid, currentPage, limit, successCallback){
            ws_call('stockOperationItem', operation_uuid, currentPage, limit, successCallback);
        }

        /**
         * load stock operations
         * @param operation_uuid
         * @param currentPage
         * @param limit
         * @param successCallback
         */
        function stockOperationTransaction(operation_uuid, currentPage, limit, successCallback){
            ws_call('stockOperationTransaction', operation_uuid, currentPage, limit, successCallback);
        }

        /**
         * Make restful calls for operation web services that require pagination
         * @param rest_entity_name
         * @param operation_uuid
         * @param currentPage
         * @param limit
         * @param successfulCallback
         */
        function ws_call(rest_entity_name, operation_uuid, currentPage, limit, successCallback){
            if(angular.isDefined(operation_uuid) && operation_uuid !== '' && operation_uuid !== undefined){
                var requestParams = PaginationService.paginateParams(currentPage, limit, false, '');
                requestParams['rest_entity_name'] = rest_entity_name;
                requestParams['operation_uuid'] = operation_uuid;

                EntityRestFactory.loadEntities(requestParams,
                    successCallback,
                    errorCallback
                );
            }
        }
    
        function errorCallback(error) {
            emr.errorAlert(error);
        }
    }
})();
