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
    base.controller("ManageStockOperationsController", ManageStockOperationsController);
    ManageStockOperationsController.$inject = ['$injector', '$scope', '$filter', 'EntityRestFactory', 'CssStylesFactory',
        'PaginationService', 'StockOperationModel', 'CookiesService', 'StockOperationRestfulService'];

    function ManageStockOperationsController($injector, $scope, $filter, EntityRestFactory, CssStylesFactory, PaginationService,
                                             StockOperationModel, CookiesService, StockOperationRestfulService) {
        var self = this;
        var module_name = 'inventory';
        var entity_name = emr.message("openhmis.inventory.stock.operation.name");
        var rest_entity_name = emr.message("openhmis.inventory.stock.operation.rest_name");

        // @Override
        self.getModelAndEntityName = self.getModelAndEntityName || function() {
                self.bindBaseParameters(module_name, rest_entity_name, entity_name);
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
                $scope.stockOperationItems = $scope.stockOperationItems || {};

                $scope.searchItems = self.searchItems;
                $scope.selectItem = self.selectItem;

                $scope.postSearchMessage = $filter('EmrFormat')(emr.message("openhmis.inventory.general.postSearchMessage"),
                    [self.entity_name]);
            }

        self.searchStockOperation = self.searchStockOperation || function(){
                var operationType_uuid;
                var stockroom_uuid;
                var operationItem_uuid;

                if($scope.operationType !== null){
                    operationType_uuid = $scope.operationType.uuid
                }

                if($scope.stockroom !== null){
                    stockroom_uuid = $scope.stockroom.uuid;
                }

                if($scope.operationItem != null){
                    operationItem_uuid = $scope.operationItem.uuid;
                }

                StockOperationRestfulService.searchStockOperation(
                    rest_entity_name, $scope.currentPage, $scope.limit,
                    operationItem_uuid, $scope.operation_status,
                    operationType_uuid, stockroom_uuid,
                    self.onLoadSearchStockOperationSuccessful
                );
            }

        self.searchItems = self.searchItems || function(){
                $scope.operationItem = {};
                console.log('search items..' + $scope.searchOperationItem);
                if($scope.searchOperationItem !== null && angular.isDefined($scope.searchOperationItem)){
                    StockOperationRestfulService.searchStockOperationItems($scope.searchOperationItem, self.onLoadSearchStockOperationItemsSuccessful);
                }
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

        self.onLoadSearchStockOperationItemsSuccessful = self.onLoadSearchStockOperationItemsSuccessful || function(data){
                $scope.stockOperationItems = data.results;
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
