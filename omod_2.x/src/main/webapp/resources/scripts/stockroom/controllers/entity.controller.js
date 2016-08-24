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

    var base = angular.module('app.genericEntityController');
    base.controller("StockroomController", StockroomController);
    StockroomController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory',
        'StockroomModel', 'StockroomRestfulService', 'PaginationService', 'EntityFunctions', 'StockroomsFunctions', 'CookiesService'];

    function StockroomController($stateParams, $injector, $scope, $filter, EntityRestFactory, StockroomModel, StockroomRestfulService, PaginationService, EntityFunctions, StockroomsFunctions, CookiesService) {
        var self = this;
        var entity_name_message_key = "openhmis.inventory.stockroom.name";
        var REST_ENTITY_NAME = "stockroom";

        // @Override
        self.setRequiredInitParameters = self.setRequiredInitParameters || function() {
                self.bindBaseParameters(INVENTORY_MODULE_NAME, REST_ENTITY_NAME, entity_name_message_key, RELATIVE_CANCEL_PAGE_URL);
                self.checkPrivileges(TASK_MANAGE_METADATA);
            }

        /**
         * Initializes and binds any required variable and/or function specific to entity.page
         * @type {Function}
         */
            // @Override
        self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
            || function(uuid) {
                // bind item stock variables/functions
                $scope.itemLimit = CookiesService.get(uuid + 'itemLimit') || 5;
                $scope.itemCurrentPage = CookiesService.get(uuid + 'itemCurrentPage') || 1;

                $scope.searchItemStockName = $scope.searchItemStockName || '';
                $scope.operationsItem = $scope.operationsItem || {};
                $scope.searchItemStock = self.searchItemStock;
                $scope.itemPagingFrom = PaginationService.pagingFrom;
                $scope.itemPagingTo = PaginationService.pagingTo;
                $scope.showItemDetails = self.showItemDetails;

                // bind item stock operation variables
                $scope.itemStockOperationLimit = CookiesService.get(uuid + 'itemStockOperationLimit')  || 5;
                $scope.itemStockOperationCurrentPage = CookiesService.get(uuid + 'itemStockOperationCurrentPage') || 1;
                $scope.searchItemStockOperationName = CookiesService.get(uuid + 'searchItemStockOperationName') || '';

                // bind item stock transaction variables
                $scope.itemStockTransactionLimit = CookiesService.get(uuid + 'itemStockTransactionLimit')  || 5;
                $scope.itemStockTransactionCurrentPage = CookiesService.get(uuid + 'itemStockTransactionCurrentPage') || 1;
                $scope.searchItemStockTransactionName = CookiesService.get(uuid + 'searchItemStockTransactionName') || '';

                $scope.searchItemStockOperation = self.searchItemStockOperation;
                $scope.searchItemStockTransaction = self.searchItemStockTransaction;
                $scope.searchOperationItems = self.searchOperationItems;
                $scope.searchTransactionItems = self.searchTransactionItems;
                $scope.selectOperationsItem = self.selectOperationsItem;
                $scope.selectTransactionsItem = self.selectTransactionsItem;
                $scope.postSearchMessage = $filter('EmrFormat')(emr.message("openhmis.commons.general.postSearchMessage"), ['Item']);

                // load list of locations
                StockroomRestfulService.loadLocations(INVENTORY_MODULE_NAME ,self.onLoadLocationsSuccessful);

                // load item stocks
                self.searchItemStock(uuid);
                self.searchItemStockOperation(uuid);
                self.searchItemStockTransaction(uuid);
            }

        self.searchOperationItems = self.searchOperationItems || function(search){
                return StockroomRestfulService.searchItems(INVENTORY_MODULE_NAME, search);
            }

        self.searchTransactionItems = self.searchTransactionItems || function(search){
                return StockroomRestfulService.searchItems(INVENTORY_MODULE_NAME, search);
            }

        self.selectOperationsItem = self.selectOperationsItem || function(item){
                $scope.operationsItem = item;
            }

        self.selectTransactionsItem = self.selectTransactionsItem || function(item){
                $scope.transactionsItem = item;
            }

        /**
         * display popup box with item details
         * @type {Function}
         */
        self.showItemDetails = self.showItemDetails || function(itemDetails){
                var item = itemDetails.item;
                var details = item.details;
                $scope.itemDetails = details;
                StockroomsFunctions.showItemDetails();
                $scope.showItemDetailsTitle = 'Stock Details for ' + item.item.name;
            }

        /**
         * return list of item stocks
         * @type {Function}
         */
        self.searchItemStock = self.searchItemStock || function(uuid, itemCurrentPage){
                itemCurrentPage = itemCurrentPage || $scope.itemCurrentPage;

                CookiesService.set(uuid + 'itemCurrentPage', itemCurrentPage);
                CookiesService.set(uuid + 'itemLimit', $scope.itemLimit);

                StockroomRestfulService.itemStock(
                    uuid, CookiesService.get(uuid + 'itemCurrentPage'),
                    CookiesService.get(uuid + 'itemLimit'),
                    $scope.searchItemStockName, self.onLoadItemStockSuccessful
                );
            }

        /**
         * return list of item stock operations
         * @type {Function}
         */
        self.searchItemStockOperation = self.searchItemStockOperation || function(uuid, itemStockOperationCurrentPage){
                var item_uuid;
                if($scope.searchItemStockOperationName !== '' && $scope.operationsItem !== undefined){
                    item_uuid = $scope.operationsItem.uuid;
                }

                itemStockOperationCurrentPage = itemStockOperationCurrentPage || $scope.itemStockOperationCurrentPage;

                CookiesService.set(uuid + 'itemStockOperationCurrentPage', itemStockOperationCurrentPage);
                CookiesService.set(uuid + 'itemStockOperationLimit', $scope.itemStockOperationLimit);

                StockroomRestfulService.itemStockOperation(
                    uuid, CookiesService.get(uuid + 'itemStockOperationCurrentPage'),
                    CookiesService.get(uuid + 'itemStockOperationLimit'),
                    item_uuid, self.onLoadItemStockOperationSuccessful
                );
            }

        /**
         * return list of item stock operation transactions
         * @type {Function}
         */
        self.searchItemStockTransaction = self.searchItemStockTransaction || function(uuid, itemStockTransactionCurrentPage){
                var item_uuid;
                if($scope.searchItemStockTransactionName !== '' && $scope.transactionsItem !== undefined){
                    item_uuid = $scope.transactionsItem.uuid;
                }

                itemStockTransactionCurrentPage = itemStockTransactionCurrentPage || $scope.itemStockTransactionCurrentPage;

                CookiesService.set(uuid + 'itemStockTransactionCurrentPage', itemStockTransactionCurrentPage);
                CookiesService.set(uuid + 'itemStockTransactionLimit', $scope.itemStockTransactionLimit);

                StockroomRestfulService.itemStockOperationTransaction(
                    uuid, CookiesService.get(uuid + 'itemStockTransactionCurrentPage'),
                    CookiesService.get(uuid + 'itemStockTransactionLimit'),
                    item_uuid, self.onLoadItemStockTransactionSuccessful
                );
            }

        /**
         * All post-submit validations are done here.
         * @return boolean
         */
            // @Override
        self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function(){
                if(!angular.isDefined($scope.entity.name) || $scope.entity.name === ''){
                    $scope.submitted = true;
                    emr.errorAlert(emr.message("openhmis.commons.general.name.required"));
                    return false;
                }

                $scope.loading = true;
                return true;
            }

        // callbacks
        self.onLoadLocationsSuccessful = self.onLoadLocationsSuccessful || function(data){
                $scope.locations = data.results;
            }

        self.onLoadItemStockSuccessful = self.onLoadItemStockSuccessful || function(data){
                $scope.items = data.results;
                $scope.itemTotalNumberOfResults = data.length;
            }

        self.onLoadItemStockOperationSuccessful = self.onLoadItemStockOperationSuccessful || function(data){
                $scope.itemStockOperations = data.results;
                $scope.itemStockOperationTotalNumberOfResults = data.length;
            }

        self.onLoadItemStockTransactionSuccessful = self.onLoadItemStockTransactionSuccessful || function(data){
                $scope.itemStockTransactions = data.results;
                $scope.itemStockTransactionTotalNumberOfResults = data.length;
            }

        // @Override
        self.setAdditionalMessageLabels = self.setAdditionalMessageLabels || function(){
                return StockroomsFunctions.addMessageLabels();
            }

        /* ENTRY POINT: Instantiate the base controller which loads the page */
        $injector.invoke(base.GenericEntityController, self, {
            $scope: $scope,
            $filter: $filter,
            $stateParams: $stateParams,
            EntityRestFactory: EntityRestFactory,
            GenericMetadataModel: StockroomModel,
            EntityFunctions: EntityFunctions
        });
    }
})();
