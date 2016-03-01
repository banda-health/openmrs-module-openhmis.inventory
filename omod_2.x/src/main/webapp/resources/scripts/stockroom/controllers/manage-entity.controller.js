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
