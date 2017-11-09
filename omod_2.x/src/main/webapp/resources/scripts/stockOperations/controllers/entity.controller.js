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
    base.controller("StockOperationController", StockOperationController);
    StockOperationController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory',
        'StockOperationModel', 'StockOperationRestfulService', 'PaginationService', 'StockOperationFunctions', 'CookiesService'];

    function StockOperationController($stateParams, $injector, $scope, $filter, EntityRestFactory, StockOperationModel, StockOperationRestfulService, PaginationService, StockOperationFunctions, CookiesService) {
        var self = this;
        var entity_name_message_key = "openhmis.inventory.stock.operation.name";
        var REST_ENTITY_NAME = "stockOperation";

        // @Override
        self.setRequiredInitParameters = self.setRequiredInitParameters || function() {
                self.bindBaseParameters(INVENTORY_MODULE_NAME, REST_ENTITY_NAME, entity_name_message_key, RELATIVE_CANCEL_PAGE_URL);
                self.checkPrivileges(TASK_ACCESS_VIEW_STOCK_OPERATIONS_PAGE);
            }

        /**
         * Initializes and binds any required variable and/or function specific to entity.page
         * @type {Function}
         */
            // @Override
        self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
            || function(uuid) {
                // bind item stock variables/functions
                $scope.stockOperationPagingFrom = PaginationService.pagingFrom;
                $scope.stockOperationPagingTo = PaginationService.pagingTo;

                $scope.stockOperationItemLimit = CookiesService.get(uuid + 'stockOperationItemLimit') || 5;
                $scope.stockOperationItemCurrentPage = CookiesService.get(uuid + 'stockOperationItemCurrentPage') || 1;

                // bind transaction variables/function
                $scope.stockOperationTransactionLimit = CookiesService.get(uuid + 'stockOperationTransactionLimit')  || 5;
                $scope.stockOperationTransactionCurrentPage = CookiesService.get(uuid + 'stockOperationTransactionCurrentPage') || 1;


                $scope.stockOperationItem = self.stockOperationItem;
                $scope.stockOperationTransaction = self.stockOperationTransaction;
                $scope.invokeOperation = self.invokeOperation;
                $scope.showOperationActionsDialog = StockOperationFunctions.showOperationActionsDialog;

                self.stockOperation(uuid, REST_ENTITY_NAME);
                self.stockOperationItem(uuid, $scope.stockOperationItemCurrentPage);
                self.stockOperationTransaction(uuid, $scope.stockOperationItemCurrentPage);

            }

        self.invokeOperation = self.invokeOperation || function(status, uuid){
                StockOperationRestfulService.invokeOperation(status, uuid, REST_ENTITY_NAME, self.onLoadInvokeOperationSuccessful);
            }

        /**
         * @type {Function}
         */
        self.stockOperation = self.stockOperation || function(uuid, rest_entity_name){
                StockOperationRestfulService.stockOperation(uuid, rest_entity_name, self.onLoadStockOperationSuccessful);
            }

        self.stockOperationItem = self.stockOperationItem || function(uuid, stockOperationItemCurrentPage){
                stockOperationItemCurrentPage = stockOperationItemCurrentPage || $scope.stockOperationItemCurrentPage;
                CookiesService.set(uuid + 'stockOperationItemCurrentPage', stockOperationItemCurrentPage);
                CookiesService.set(uuid + 'stockOperationItemLimit', $scope.stockOperationItemLimit);

                StockOperationRestfulService.stockOperationItem(
                    uuid, CookiesService.get(uuid + 'stockOperationItemCurrentPage'),
                    CookiesService.get(uuid + 'stockOperationItemLimit'),
                    self.onLoadStockOperationItemSuccessful
                );
            }

        self.stockOperationTransaction = self.stockOperationTransaction || function(uuid, stockOperationTransactionCurrentPage){
                stockOperationTransactionCurrentPage = stockOperationTransactionCurrentPage || $scope.stockOperationTransactionCurrentPage;
                CookiesService.set(uuid + 'stockOperationTransactionCurrentPage', stockOperationTransactionCurrentPage);
                CookiesService.set(uuid + 'stockOperationTransactionLimit', $scope.stockOperationTransactionLimit);

                StockOperationRestfulService.stockOperationTransaction(
                    uuid, CookiesService.get(uuid + 'stockOperationTransactionCurrentPage'),
                    CookiesService.get(uuid + 'stockOperationTransactionLimit'),
                    self.onLoadStockOperationTransactionSuccessful
                );
            }

        // callbacks
        self.onLoadStockOperationSuccessful = self.onLoadStockOperationSuccessful || function(data){
                $scope.stockOperation = data;
            }

        self.onLoadStockOperationItemSuccessful = self.onLoadStockOperationItemSuccessful || function(data){
                $scope.stockOperationItems = data.results;
                $scope.stockOperationItemTotalNumberOfResults = data.length;
            }

        self.onLoadStockOperationTransactionSuccessful = self.onLoadStockOperationTransactionSuccessful || function(data){
                $scope.stockOperationTransactions = data.results;
                $scope.stockOperationTransactionTotalNumberOfResults = data.length;
            }

        self.onLoadInvokeOperationSuccessful = self.onLoadInvokeOperationSuccessful || function(){
                $scope.cancel();
            }

        // @Override
        self.setAdditionalMessageLabels = self.setAdditionalMessageLabels || function(){
                return StockOperationFunctions.addMessageLabels();
            }

        /* ENTRY POINT: Instantiate the base controller which loads the page */
        $injector.invoke(base.GenericEntityController, self, {
            $scope: $scope,
            $filter: $filter,
            $stateParams: $stateParams,
            EntityRestFactory: EntityRestFactory,
            GenericMetadataModel: StockOperationModel
        });
    }
})();
