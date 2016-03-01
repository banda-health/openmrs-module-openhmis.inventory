(function() {
    'use strict';

    var base = angular.module('app.genericEntityController');
    base.controller("StockOperationController", StockOperationController);
    StockOperationController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory',
        'StockOperationModel', 'StockOperationRestfulService', 'PaginationService', 'StockOperationFunctions'];

    function StockOperationController($stateParams, $injector, $scope, $filter, EntityRestFactory, StockOperationModel, StockOperationRestfulService, PaginationService, StockOperationFunctions) {
        var self = this;
        var module_name = 'inventory';
        var entity_name = emr.message("openhmis.inventory.stock.operation.name");
        var cancel_page = 'entities.page';
        var rest_entity_name = emr.message("openhmis.inventory.stock.operation.rest_name");

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
                // bind item stock variables/functions
                $scope.stockOperationPagingFrom = PaginationService.pagingFrom;
                $scope.stockOperationPagingTo = PaginationService.pagingTo;

                $scope.stockOperationItemLimit = $scope.stockOperationItemLimit || 5;
                $scope.stockOperationItemCurrentPage = $scope.stockOperationItemCurrentPage || 1;

                // bind transaction variables/function
                $scope.stockOperationTransactionLimit = $scope.stockOperationTransactionLimit  || 5;
                $scope.stockOperationTransactionCurrentPage = $scope.stockOperationTransactionCurrentPage || 1;



                $scope.stockOperationItem = self.stockOperationItem;
                $scope.stockOperationTransaction = self.stockOperationTransaction;

                $scope.rollbackOperation = self.rollbackOperation;

                self.stockOperation(uuid, rest_entity_name);
                self.stockOperationItem(uuid, $scope.stockOperationItemCurrentPage);
                self.stockOperationTransaction(uuid, $scope.stockOperationItemCurrentPage);

            }

        self.rollbackOperation = self.rollbackOperation || function(uuid){
                StockOperationRestfulService.rollbackOperation(uuid, rest_entity_name, self.onLoadRollbackOperationSuccessful);
            }

        /**
         * @type {Function}
         */
        self.stockOperation = self.stockOperation || function(uuid, rest_entity_name){
                StockOperationRestfulService.stockOperation(uuid, rest_entity_name, self.onLoadStockOperationSuccessful);
            }

        self.stockOperationItem = self.stockOperationItem || function(uuid, stockOperationItemCurrentPage){
                StockOperationRestfulService.stockOperationItem(uuid, stockOperationItemCurrentPage, $scope.stockOperationItemLimit, self.onLoadStockOperationItemSuccessful);
            }

        self.stockOperationTransaction = self.stockOperationTransaction || function(uuid, stockOperationTransactionCurrentPage){
                StockOperationRestfulService.stockOperationTransaction(uuid, stockOperationTransactionCurrentPage, $scope.stockOperationTransactionLimit, self.onLoadStockOperationTransactionSuccessful);
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

        self.onLoadRollbackOperationSuccessful = self.onLoadRollbackOperationSuccessful || function(){
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