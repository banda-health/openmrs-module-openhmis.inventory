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
	ReportController.$inject = [ '$stateParams', '$injector', '$scope', '$filter', 'ReportModel', 'ReportRestfulService',
			'ReportsFunctions', 'EntityRestFactory', 'CommonsRestfulFunctions'];

	function ReportController($stateParams, $injector, $scope, $filter, ReportModel, ReportRestfulService,
			ReportsFunctions, EntityRestFactory, CommonsRestfulFunctions) {

		var self = this;

		var entity_name_message_key = "openhmis.inventory.report.name";
		var REST_ENTITY_NAME = "report";

		// @Override
		self.setRequiredInitParameters = self.setRequiredInitParameters || function() {
			self.bindBaseParameters(INVENTORY_MODULE_NAME, REST_ENTITY_NAME, entity_name_message_key, RELATIVE_CANCEL_PAGE_URL);
			self.checkPrivileges(TASK_ACCESS_INVENTORY_REPORTS_PAGE);
		}

		/**
		 * Initializes and binds any required variable and/or function specific
		 * to report.page
		 * 
		 * @type {Function}
		 */
		// @Override
		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope || function(uuid) {
			/* bind variables.. */
			self.loadStockRooms();

			$scope.searchReportItems = self.searchReportItems;

			// Load in the 5 reports from their string names in
			// ModuleSettings.java
			ReportRestfulService.getReport("openhmis.inventory.reports.stockTake", function(data) {
				$scope.stockTakeReport = data;
			});
			ReportRestfulService.getReport("openhmis.inventory.reports.stockroom", function(data) {
				$scope.stockroomUsageReport = data;
			});
			ReportRestfulService.getReport("openhmis.inventory.reports.stockCard", function(data) {
				$scope.stockCardReport = data;
			});
			ReportRestfulService.getReport("openhmis.inventory.reports.stockOperationsByStockroom", function(data) {
				$scope.stockOperationsByStockroomReport = data;
			});
			ReportRestfulService.getReport("openhmis.inventory.reports.expiringStock", function(data) {
				$scope.expiringStockReport = data;
			});

			// Set change listeners for all datepickers used in
			// reports/entity.page
			ReportsFunctions.onChangeDatePicker('stockCardReport_beginDate-display', function(value) {
				$scope.stockCardReport_beginDate = value;
			});
			ReportsFunctions.onChangeDatePicker('stockCardReport_endDate-display', function(value) {
				$scope.stockCardReport_endDate = value;
			});
			ReportsFunctions.onChangeDatePicker('stockOperationsByStockroomReport_beginDate-display', function(value) {
				$scope.stockOperationsByStockroomReport_beginDate = value;
			});
			ReportsFunctions.onChangeDatePicker('stockOperationsByStockroomReport_endDate-display', function(value) {
				$scope.stockOperationsByStockroomReport_endDate = value;
			});
			ReportsFunctions.onChangeDatePicker('stockroomUsage_beginDate-display', function(value) {
				$scope.stockroomUsageReport_beginDate = value;
			});
			ReportsFunctions.onChangeDatePicker('stockroomUsage_endDate-display', function(value) {
				$scope.stockroomUsageReport_endDate = value;
			});
			ReportsFunctions.onChangeDatePicker('expiringStock_expiresByDate-display', function(value) {
				$scope.expiringStock_expiresByDate = value;
			});
		}

		self.loadStockRooms = self.loadStockRooms || function() {
			CommonsRestfulFunctions.loadStockRooms("stockroom", self.onLoadStockRoomSuccessful);
		}

		self.onLoadStockRoomSuccessful = self.onLoadStockRoomSuccessful || function(data) {
			$scope.stockrooms = data.results;
		}
		self.searchReportItems = self.searchReportItems || function(search) {
			$scope.operationItem = {};
			return CommonsRestfulFunctions.searchItems(INVENTORY_MODULE_NAME, search);
		}

		function printReport(reportId, parameters) {
			var url = INVENTORY_REPORTS_PAGE_URL + "reportId=" + reportId + "&" + parameters;
			window.open(url, "pdfDownload");

			return false;
		}

		function checkParameters(parameterObject) {
			var objectKeys = Object.keys(parameterObject);
			for (var i = 0; i < objectKeys.length; i++) {
				var name = objectKeys[i];
				var value = parameterObject[objectKeys[i]];
				if (!value) {
					switch (name) {
						case "stockroom":
							emr.errorAlert('openhmis.inventory.report.error.stockroomRequired');
							break;
						case "item":
							emr.errorAlert('openhmis.inventory.report.error.itemRequired');
							break;
						case "beginDate":
							emr.errorAlert('openhmis.inventory.report.error.beginDateRequired');
							break;
						case "endDate":
							emr.errorAlert('openhmis.inventory.report.error.endDateRequired');
							break;
						case "expiryDate":
							emr.errorAlert('openhmis.inventory.report.error.expiryDateRequired');
							break;
						default:
							break;
					}
					return false;
				}
			}
			return true;
		}

		$scope.setStockCardReportItem = function(item) {
			$scope.stockCardReportItem = item;
		}

		$scope.setStockOperationsByStockroomReportItem = function(item) {
			$scope.stockOperationsByStockroomReportItem = item;
		}

		/* Report Generation functions */
		$scope.generateReport_StockTakeReport = function() {
			var stockroom = $scope.StockTakeReport_stockroom;

			var parametersAreValid = checkParameters({
				"stockroom" : stockroom
			});

			if (parametersAreValid) {
				var reportId = $scope.stockTakeReport.reportId;
				var parameters = "stockroomId=" + stockroom.id;

				return printReport(reportId, parameters)
			}

		}
		$scope.generateReport_StockCardReport = function() {
			var item = $scope.stockCardReportItem;
			var beginDate = $scope.stockCardReport_beginDate;
			var endDate = $scope.stockCardReport_endDate;

			var parametersAreValid = checkParameters({
				"item" : item,
				"beginDate" : beginDate,
				"endDate" : endDate
			});

			if (parametersAreValid) {
				var reportId = $scope.stockCardReport.reportId;
				var parameters = "itemUuid=" + item.uuid + "&beginDate=" + ReportsFunctions.formatDate(beginDate)
						+ "&endDate=" + ReportsFunctions.formatDate(endDate);

				return printReport(reportId, parameters);
			}
		}
		$scope.generateReport_StockOperationsByStockroomReport = function() {
			var stockroom = $scope.stockOperationsByStockroomReport_stockroom;
			var item = $scope.stockOperationsByStockroomReportItem;
			var beginDate = $scope.stockOperationsByStockroomReport_beginDate;
			var endDate = $scope.stockOperationsByStockroomReport_endDate;

			var parametersAreValid = checkParameters({
				"stockroom" : stockroom,
				"item" : item,
				"beginDate" : beginDate,
				"endDate" : endDate
			});

			if (parametersAreValid) {
				var reportId = $scope.stockOperationsByStockroomReport.reportId;
				var parameters = "itemUuid=" + item.uuid + "&beginDate=" + ReportsFunctions.formatDate(beginDate)
						+ "&endDate=" + ReportsFunctions.formatDate(endDate) + "&stockroomId=" + stockroom.id;

				return printReport(reportId, parameters);
			}
		}
		$scope.generateReport_StockroomUsage = function() {
			var stockroom = $scope.stockroomUsageReport_stockroom;
			var beginDate = $scope.stockroomUsageReport_beginDate;
			var endDate = $scope.stockroomUsageReport_endDate;

			var parametersAreValid = checkParameters({
				"stockroom" : stockroom,
				"beginDate" : beginDate,
				"endDate" : endDate
			});

			if (parametersAreValid) {
				var reportId = $scope.stockroomUsageReport.reportId;
				var parameters = "stockroomId=" + stockroom.id + "&beginDate=" + ReportsFunctions.formatDate(beginDate)
						+ "&endDate=" + ReportsFunctions.formatDate(endDate);

				return printReport(reportId, parameters);
			}

		}
		$scope.generateReport_ExpiringStock = function() {
			var stockroom = $scope.expiringStock_stockroom;
			var expiryDate = $scope.expiringStock_expiresByDate;

			var parametersAreValid = checkParameters({
				"expiryDate" : expiryDate
			});

			if (parametersAreValid) {
				var reportId = $scope.expiringStockReport.reportId;
				var parameters = "expiresBy=" + ReportsFunctions.formatDate(expiryDate);
				if (stockroom != null) {
					parameters += "&stockroomId=" + stockroom.id;
				}
				return printReport(reportId, parameters);
			}
		}

		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, self, {
			$scope : $scope,
			$filter : $filter,
			$stateParams : $stateParams,
			ReportRestfulService : ReportRestfulService,
			EntityRestFactory : EntityRestFactory,
			GenericMetadataModel : ReportModel
		});
	}
})();
