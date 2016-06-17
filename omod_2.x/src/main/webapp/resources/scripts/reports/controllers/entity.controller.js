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
    base.controller("ReportController", ReportController);
    ReportController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'ReportModel', 'ReportRestfulService', 'EntityRestFactory'];

    function ReportController($stateParams, $injector, $scope, $filter, ReportModel, ReportRestfulService, EntityRestFactory) {

        var self = this;

        var module_name = 'inventory';
        var entity_name_message_key = "openhmis.inventory.report.name";
        var cancel_page = 'reports.page';
        var rest_entity_name = emr.message("openhmis.inventory.report.rest_name");

        // @Override
        self.setRequiredInitParameters = self.setRequiredInitParameters || function() {
                self.bindBaseParameters(module_name, rest_entity_name, entity_name_message_key, cancel_page);
            }

        /**
         * Initializes and binds any required variable and/or function specific to report.page
         * @type {Function}
         */
        // @Override
        self.bindExtraVariablesToScope = self.bindExtraVariablesToScope || function(uuid) {
                /* bind variables.. */
                self.loadStockRooms();

                /* bind functions.. */
                $scope.loadStockRooms = function (search) {
                    return ReportRestfulService.loadStockRooms(module_name, search);
                };
                
            }

        self.loadStockRooms = self.loadStockRooms || function(){
                ReportRestfulService.loadStockRooms("stockroom", self.onLoadStockRoomSuccessful);
            }
        
        self.onLoadStockRoomSuccessful = self.onLoadStockRoomSuccessful || function(data){
                $scope.stockrooms = data.results;
            }


        /* ENTRY POINT: Instantiate the base controller which loads the page */
        $injector.invoke(base.GenericEntityController, self, {
            $scope: $scope,
            $filter: $filter,
            $stateParams: $stateParams,
            ReportRestfulService: ReportRestfulService,
            EntityRestFactory: EntityRestFactory,
            GenericMetadataModel: ReportModel
        });
    }
})();
