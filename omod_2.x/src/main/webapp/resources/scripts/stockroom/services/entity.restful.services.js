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

    angular.module('app.restfulServices').service('StockroomRestfulService', StockroomRestfulService);

    StockroomRestfulService.$inject = ['EntityRestFactory', 'PaginationService'];

    function StockroomRestfulService(EntityRestFactory, PaginationService) {
        var service;

        service = {
            loadLocations: loadLocations,
            searchStockrooms: searchStockrooms,
            itemStock: itemStock,
            itemStockOperation: itemStockOperation,
            itemStockOperationTransaction: itemStockOperationTransaction,
        };

        return service;

        /**
         * Retrieve all locations
         * @param onLoadLocationsSuccessful
         */
        function loadLocations(module_name, onLoadLocationsSuccessful) {
            var requestParams = [];
            requestParams['rest_entity_name'] = '';
            requestParams['limit'] = 100;
            EntityRestFactory.setBaseUrl('location', 'v1');
            EntityRestFactory.loadEntities(requestParams,
                onLoadLocationsSuccessful,
                errorCallback
            );

            //reset base url..
            EntityRestFactory.setBaseUrl(module_name);
        }

        /**
         * Load /or search stock rooms
         * @param rest_entity_name
         * @param location_uuid
         * @param currentPage
         * @param limit
         * @param q
         * @param onSearchStockRoomsSuccessful
         */
        function searchStockrooms(rest_entity_name, location_uuid, currentPage, limit, q, onSearchStockRoomsSuccessful){
            var requestParams = PaginationService.paginateParams(currentPage, limit, false, q);
            requestParams['rest_entity_name'] = rest_entity_name;
            if(angular.isDefined(location_uuid) && location_uuid !== undefined && location_uuid !== ''){
                requestParams['location_uuid'] = location_uuid;
                requestParams['q'] = q;
            }

            EntityRestFactory.loadEntities(requestParams,
                onSearchStockRoomsSuccessful,
                errorCallback
            );
        }

        /**
         *
         * @param stockroom_uuid
         * @param currentPage
         * @param limit
         * @param q
         * @param onLoadItemStockSuccessful
         */
        function itemStock(stockroom_uuid, currentPage, limit, q, onLoadItemStockSuccessful){
           ws_call('itemStock', stockroom_uuid, currentPage, limit, q, onLoadItemStockSuccessful);
        }

        function itemStockOperation(stockroom_uuid, currentPage, limit, q, onLoadItemStockOperationSuccessful){
            ws_call('stockOperation', stockroom_uuid, currentPage, limit, q, onLoadItemStockOperationSuccessful);
        }

        function itemStockOperationTransaction(stockroom_uuid, currentPage, limit, q, onLoadItemStockTransactionSuccessful){
            ws_call('stockOperationTransaction', stockroom_uuid, currentPage, limit, q, onLoadItemStockTransactionSuccessful);
        }

        /**
         * Make restful calls for item stock web services
         * @param rest_entity_name
         * @param stockroom_uuid
         * @param currentPage
         * @param limit
         * @param successfulCallback
         */
        function ws_call(rest_entity_name, stockroom_uuid, currentPage, limit, q, successfulCallback){
            if(angular.isDefined(stockroom_uuid) && stockroom_uuid !== '' && stockroom_uuid !== undefined){
                var requestParams = PaginationService.paginateParams(currentPage, limit, false, q);
                requestParams['rest_entity_name'] = rest_entity_name;
                requestParams['stockroom_uuid'] = stockroom_uuid;

                EntityRestFactory.loadEntities(requestParams,
                    successfulCallback,
                    errorCallback
                );
            }
        }

        function errorCallback(error){
            console.log(error);
        }
    }
})();
