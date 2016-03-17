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
        var module_name = 'inventory';
        var entity_name = emr.message("openhmis.inventory.stockroom.name");
        var rest_entity_name = emr.message("openhmis.inventory.stockroom.rest_name");

        // @Override
        self.getModelAndEntityName = self.getModelAndEntityName || function() {
                self.bindBaseParameters(module_name, rest_entity_name, entity_name);
            }

        // @Override
        self.bindExtraVariablesToScope = self.bindExtraVariablesToScope || function() {
                self.loadLocations();
                $scope.searchStockrooms = self.searchStockrooms;

              $scope.postSearchMessage = $filter('EmrFormat')(emr.message("openhmis.inventory.general.postSearchMessage"),
                    [self.entity_name]);
            }

        self.loadLocations = self.loadLocations || function(){
                StockroomRestfulService.loadLocations(module_name, self.onLoadLocationsSuccessful);
            }

        self.searchStockrooms = self.searchStockrooms || function(){
                var location_uuid;
                if($scope.location !== "" && $scope.location !== null){
                    location_uuid = $scope.location.uuid;
                }
                StockroomRestfulService.searchStockrooms(rest_entity_name, location_uuid, $scope.currentPage, $scope.limit, $scope.searchField, self.onSearchStockRoomsSuccessful);
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
