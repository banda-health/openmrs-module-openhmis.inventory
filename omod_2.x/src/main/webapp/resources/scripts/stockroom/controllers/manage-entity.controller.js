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
    base.controller("ManageStockroomsController", ManageStockroomsController);
    ManageStockroomsController.$inject = ['$injector', '$scope', '$filter', 'EntityRestFactory', 'CssStylesFactory',
        'PaginationService', 'StockroomModel', 'CookiesService', 'StockroomRestfulService'];

    function ManageStockroomsController($injector, $scope, $filter, EntityRestFactory, CssStylesFactory, PaginationService,
                                  StockroomModel, CookiesService, StockroomRestfulService) {
        var self = this;
        var entity_name = emr.message("openhmis.inventory.stockroom.name");
        var REST_ENTITY_NAME = "stockroom";

        // @Override
        self.getModelAndEntityName = self.getModelAndEntityName || function() {
                self.bindBaseParameters(INVENTORY_MODULE_NAME, REST_ENTITY_NAME, entity_name);
                self.checkPrivileges(TASK_MANAGE_METADATA);
            }

        // @Override
        self.bindExtraVariablesToScope = self.bindExtraVariablesToScope || function() {
                self.loadLocations();
                $scope.searchStockroomsByName = self.searchStockroomsByName;
                $scope.searchStockrooms = self.searchStockrooms;
            }

        self.loadLocations = self.loadLocations || function(){
                StockroomRestfulService.loadLocations(INVENTORY_MODULE_NAME, self.onLoadLocationsSuccessful);
            }

        self.searchStockroomsByName = self.searchStockroomsByName || function(currentPage){
                if($scope.searchField === undefined || $scope.searchField === ''){
                    currentPage = 1;
                    $scope.currentPage = currentPage;
                }

                self.searchStockrooms(currentPage);
            }

        self.searchStockrooms = self.searchStockrooms || function(currentPage){
                CookiesService.set('searchField', $scope.searchField);
                CookiesService.set('startIndex', $scope.startIndex);
                CookiesService.set('limit', $scope.limit);
                CookiesService.set('includeRetired', $scope.includeRetired);
                CookiesService.set('currentPage', currentPage);

                var location_uuid;
                if($scope.location !== "" && $scope.location !== null){
                    location_uuid = $scope.location.uuid;
                }

                CookiesService.set('location_uuid', location_uuid);

                StockroomRestfulService.searchStockrooms(REST_ENTITY_NAME, location_uuid, currentPage, $scope.limit, $scope.includeRetired, $scope.searchField, self.onSearchStockRoomsSuccessful);
            }

        // call back
        self.onLoadLocationsSuccessful = self.onLoadLocationsSuccessful || function(data){
                $scope.locations = data.results;
                $scope.location = $scope.location || 'Any location'
            }

        self.onSearchStockRoomsSuccessful = self.onSearchStockRoomsSuccessful || function(data){
                $scope.fetchedEntities = data.results;
                $scope.totalNumOfResults = data.length;
            }

        /* ENTRY POINT: Instantiate the base controller which loads the page */
        $injector.invoke(base.GenericManageController, self, {
            $scope: $scope,
            $filter: $filter,
            EntityRestFactory: EntityRestFactory,
            PaginationService: PaginationService,
            CssStylesFactory: CssStylesFactory,
            GenericMetadataModel: StockroomModel,
            CookiesService: CookiesService
        });
    }
})();
