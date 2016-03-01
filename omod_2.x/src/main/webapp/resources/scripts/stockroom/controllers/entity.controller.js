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
        'StockroomModel', 'StockroomRestfulService', 'PaginationService', 'EntityFunctions', 'StockroomsFunctions'];

    function StockroomController($stateParams, $injector, $scope, $filter, EntityRestFactory, StockroomModel, StockroomRestfulService, PaginationService, EntityFunctions, StockroomsFunctions) {
        var self = this;
        var module_name = 'inventory';
        var entity_name = emr.message("openhmis.inventory.stockroom.name");
        var cancel_page = 'entities.page';
        var rest_entity_name = emr.message("openhmis.inventory.stockroom.rest_name");

        // @Override
        self.setRequiredInitParameters = self.setRequiredInitParameters || function() {
                self.bindBaseParameters(module_name, rest_entity_name, entity_name, cancel_page);
            }

        /**
         * Initializes and binds any required variable and/or function specific to entity.page
         * @type {Function}
         */
            // @Override
        self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
            || function(uuid) {
                if (angular.isDefined($scope.entity) && angular.isDefined($scope.entity.retired)
                    && $scope.entity.retired === true) {
                    $scope.retireOrUnretire = $filter('EmrFormat')(emr.message("openhmis.inventory.general.unretire"),
                        [self.entity_name]);
                } else {
                    $scope.retireOrUnretire = $filter('EmrFormat')(emr.message("openhmis.inventory.general.retire"),
                        [self.entity_name]);
                }

                // bind item stock variables/functions
                $scope.itemLimit = $scope.itemLimit || 5;
                $scope.itemCurrentPage = $scope.itemCurrentPage || 1;
                $scope.searchItemStockName = $scope.searchItemStockName || '';
                $scope.searchItemStock = self.searchItemStock;
                $scope.itemPagingFrom = PaginationService.pagingFrom;
                $scope.itemPagingTo = PaginationService.pagingTo;
                $scope.showItemDetails = self.showItemDetails;

                // bind item stock operation variables/function
                $scope.itemStockOperationLimit = $scope.itemStockOperationLimit  || 5;
                $scope.itemStockOperationCurrentPage = $scope.itemStockOperationCurrentPage || 1;
                $scope.searchItemStockOperationName = $scope.searchItemStockOperationName || '';
                $scope.searchItemStockOperation = self.searchItemStockOperation;

                // bind item stock transaction variables/function
                $scope.itemStockTransactionLimit = $scope.itemStockTransactionLimit  || 5;
                $scope.itemStockTransactionCurrentPage = $scope.itemStockTransactionCurrentPage || 1;
                $scope.searchItemStockTransactionName = $scope.searchItemStockTransactionName || '';
                $scope.searchItemStockTransaction = self.searchItemStockTransaction;

                // load list of locations
                StockroomRestfulService.loadLocations(module_name ,self.onLoadLocationsSuccessful);

                // load item stocks
                self.searchItemStock(uuid);
                self.searchItemStockOperation(uuid);
                self.searchItemStockTransaction(uuid);
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
                StockroomRestfulService.itemStock(uuid, itemCurrentPage, $scope.itemLimit, $scope.searchItemStockName, self.onLoadItemStockSuccessful);
            }

        /**
         * return list of item stock operations
         * @type {Function}
         */
        self.searchItemStockOperation = self.searchItemStockOperation || function(uuid, itemStockOperationCurrentPage){
                StockroomRestfulService.itemStockOperation(uuid, itemStockOperationCurrentPage, $scope.itemStockOperationLimit, $scope.searchItemStockOperationName, self.onLoadItemStockOperationSuccessful);
            }

        /**
         * return list of item stock operation transactions
         * @type {Function}
         */
        self.searchItemStockTransaction = self.searchItemStockTransaction || function(uuid, itemStockTransactionCurrentPage){
                StockroomRestfulService.itemStockOperationTransaction(uuid, itemStockTransactionCurrentPage, $scope.itemStockTransactionLimit, $scope.searchItemStockTransactionName, self.onLoadItemStockTransactionSuccessful);
            }

        /**
         * All post-submit validations are done here.
         * @return boolean
         */
            // @Override
        self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function(){
                if(!angular.isDefined($scope.entity.name) || $scope.entity.name === ''){
                    $scope.submitted = true;
                    return false;
                }
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
