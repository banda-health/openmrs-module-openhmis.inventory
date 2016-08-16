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

    var base = angular.module('app.genericManageController');
    base.controller("MyOperationsController", MyOperationsController);
    MyOperationsController.$inject = ['$injector', '$scope', '$filter', 'EntityRestFactory',
        'CssStylesFactory', 'PaginationService', 'StockOperationModel',
        'CookiesService', 'StockOperationRestfulService'];

    function MyOperationsController($injector, $scope, $filter, EntityRestFactory,
                                    CssStylesFactory, PaginationService,
                                    StockOperationModel, CookiesService,
                                    StockOperationRestfulService) {
        var self = this;
        var entity_name = emr.message("openhmis.inventory.stock.operation.name");
        var rest_entity_name = emr.message("openhmis.inventory.stock.operation.rest_name");

        // @Override
        self.getModelAndEntityName = self.getModelAndEntityName || function() {
                self.bindBaseParameters(INVENTORY_MODULE_NAME, rest_entity_name, entity_name);
                self.checkPrivileges(PRIVILEGE_ACCESS_MY_OPERATIONS_PAGE);
            }

        // @Override
        self.bindExtraVariablesToScope = self.bindExtraVariablesToScope || function() {
                self.loadStockOperationTypes();
                self.loadStockRooms();
                $scope.searchStockOperation = self.searchStockOperation;

                $scope.operationType = $scope.operationType || {};
                $scope.stockroom = $scope.stockroom || {};
                $scope.operationItem = $scope.operationItem || {};
                $scope.searchOperationItem = $scope.searchOperationItem || '';

                $scope.searchItems = self.searchItems;
                $scope.selectItem = self.selectItem;

                $scope.operation_status = 'Completed';

                self.searchStockOperation(1);
            }

        // @Override
        self.paginate = self.paginate || function(page){
                //do nothing..
            }

        self.searchStockOperation = self.searchStockOperation || function(currentPage){
                if(currentPage === undefined){
                    currentPage = $scope.currentPage;
                }
                else{
                    $scope.currentPage = currentPage;
                }

                CookiesService.set('startIndex', $scope.startIndex);
                CookiesService.set('limit', $scope.limit);
                CookiesService.set('currentPage', currentPage);

                var operationType_uuid;
                var stockroom_uuid;
                var operationItem_uuid;

                if($scope.operationType !== null){
                    operationType_uuid = $scope.operationType.uuid
                }

                if($scope.stockroom !== null){
                    stockroom_uuid = $scope.stockroom.uuid;
                }

                if($scope.searchOperationItem !== '' && $scope.operationItem != null){
                    operationItem_uuid = $scope.operationItem.uuid;
                }

                StockOperationRestfulService.searchStockOperation(
                    rest_entity_name, currentPage, $scope.limit,
                    operationItem_uuid, $scope.operation_status,
                    operationType_uuid, stockroom_uuid,
                    self.onLoadSearchStockOperationSuccessful, 'myOperation'
                );
            }

        self.searchItems = self.searchItems || function(search){
                $scope.operationItem = {};
                return StockOperationRestfulService.searchStockOperationItems(INVENTORY_MODULE_NAME, search);
            }

        self.selectItem = self.selectItem || function(item){
                $scope.operationItem = item;
            }

        self.loadStockOperationTypes = self.loadStockOperationTypes || function(){
                StockOperationRestfulService.loadStockOperationTypes("stockOperationType", self.onLoadStockOperationTypesSuccessful);
            }

        self.loadStockRooms = self.loadStockRooms || function(){
                StockOperationRestfulService.loadStockRooms("stockroom", self.onLoadStockRoomSuccessful);
            }

        self.onLoadSearchStockOperationSuccessful = self.onLoadSearchStockOperationSuccessful || function(data){
                $scope.fetchedEntities = data.results;
                $scope.totalNumOfResults = data.length;
            }

        self.onLoadStockOperationTypesSuccessful = self.onLoadStockOperationTypesSuccessful || function(data){
                $scope.stockOperationTypes = data.results;
            }

        self.onLoadStockRoomSuccessful = self.onLoadStockRoomSuccessful || function(data){
                $scope.stockrooms = data.results;
            }

        /* ENTRY POINT: Instantiate the base controller which loads the page */
        $injector.invoke(base.GenericManageController, self, {
            $scope: $scope,
            $filter: $filter,
            EntityRestFactory: EntityRestFactory,
            PaginationService: PaginationService,
            CssStylesFactory: CssStylesFactory,
            GenericMetadataModel: StockOperationModel,
            CookiesService: CookiesService
        });
    }
})();
