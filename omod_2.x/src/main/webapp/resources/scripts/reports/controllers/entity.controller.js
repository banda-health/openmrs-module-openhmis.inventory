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
                $scope.reportURL = "/module/openhmis/inventory/jasperReport";

                $scope.searchReportItems = self.searchReportItems;
                $scope.stockTakeReport = {
                    name: "Stock Take",
                    description: "The current inventory for a specific stockroom",
                    reportId: 5
                };
                $scope.stockCardReport = {
                    name: "Stock Card for an Item ",
                    description: "All transactions for a specific item, over a time period, for a specific stockroom or all stockrooms",
                    reportId: [3,4]
                };
                $scope.stockroomUsageReport = {
                    name: "Stockroom Usage Report",
                    description: "All items used by a specific stockroom, over a time period",
                    reportId: 6
                };
                $scope.expiringStockReport = {
                    name: "Expiring Stock",
                    description: "All items that expire by a specified date",
                    reportId: 1
                };

            }

        self.loadStockRooms = self.loadStockRooms || function(){
                ReportRestfulService.loadStockRooms("stockroom", self.onLoadStockRoomSuccessful);
            }
        
        self.onLoadStockRoomSuccessful = self.onLoadStockRoomSuccessful || function(data){
                $scope.stockrooms = data.results;
            }

        self.searchReportItems = self.searchReportItems || function(search){
                $scope.operationItem = {};
                return ReportRestfulService.searchReportItems(module_name, search);
            }

        function printReport(reportId, parameters) {
            var reportUrl = $scope.reportURL;
            var url = OPENMRS_CONTEXT_PATH + reportUrl + ".form?";
            url += "reportId=" + reportId  + "&" + parameters;

            console.log("url:", url);
            window.open(url, "pdfDownload");

            return false;

            //Not really sure how to setup and use the entityRestFactory stuff.
            // the above code almost works, but just grabs a wrong base url.
            //-af
            EntityRestFactory.setCustomBaseUrl('/'+ OPENMRS_CONTEXT_PATH + '/');
            EntityRestFactory.loadResults(parameters);
        }

        $scope.generateReport_StockTakeReport = function(){
            var reportId = $scope.stockTakeReport.reportId;
            var stockroom = $scope.StockTakeReport_stockroom;
            var parameters = "";

            console.log("report", reportId, "stockroom", stockroomId);
            if (!stockroomId) {
                alert(openhmis.getMessage('openhmis.inventory.report.error.stockroomRequired'));
                return false;
            }
            parameters = "stockroomId=" + stockroom.uuid;

            return printReport(reportId, parameters);
        }
        
        $scope.generateReport_StockCardReport = function(){
            var stockroom = $scope.stockCardReport_stockroom;
            var item = $scope.stockCardReportItem;
            var beginDate = $scope.stockCardReport_beginDate;
            var endDate = $scope.stockCardReport_endDate;
    
            var reportId;
            var parameters = "";
            
            console.log("stockroom:", stockroom);
            console.log("item:", item);
            console.log("beginDate:", beginDate);
            console.log("endDate:", endDate);
            
            //Two reports are being combined here, one for if no stockroom is selected (report No. 3)
            //and one if a specific stockroom is selected (report No. 4)
            // if no stockroom is selected, it runs the report for all stockrooms
            //-af
            if(stockroom == null){
                reportId = $scope.stockCardReport.reportId[0];
                parameters = "itemUuid=" + item.uuid + "&beginDate=" + beginDate + "&endDate=" + endDate;
            } else{
                reportId =  $scope.stockCardReport.reportId[1];
                parameters = "itemUuid=" + item.uuid + "&beginDate=" + beginDate + "&endDate=" + endDate + "&stockroomId=" +stockroom.uuid;
            }
            
            return printReport(reportId, parameters);
            
        }

        //This is a helper I made, so that the item dropdown can set a scope variable when it's selected
        // otherwise, I was just getting the item name when I tried to grab the model's value.
        //-af
        $scope.setStockCardReportItem = function(item){
            $scope.stockCardReportItem = item;
            console.log(item);
        }

        //the last 2 reports aren't done, just kinda stubbed out.
        //I still need validation and formatting for these
        //stuff like finding the stockroomId instead of the UUID
        //and formating the dates in the way they need to be (dd-mm-yyyy) i think
        //-af
        $scope.generateReport_StockroomUsage = function() {
            
            var stockroom = $scope.stockroomUsage_stockroom;
            var beginDate = $scope.stockroomUsage_beginDate;
            var endDate = $scope.stockroomUsage_endDate;
            
        }

        $scope.generateReport_ExpiringStock = function() {
            var stockroom = $scope.expiringStock_stockroom;
            var expiresByDate = $scope.expiringStock_expiresByDate;

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
