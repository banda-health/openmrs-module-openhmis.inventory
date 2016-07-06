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

(function () {
	'use strict';
	
	var app = angular.module('app.stockTakeFunctionsFactory', []);
	app.service('StockTakeFunctions', StockTakeFunctions);
	
	StockTakeFunctions.$inject = ['$filter'];
	
	function StockTakeFunctions($filter) {
		var service;
		
		service = {
			addMessageLabels: addMessageLabels,
			findIndexByKeyValue: findIndexByKeyValue,
			formatDate: formatDate,
			stockroomChangeDialog: stockroomChangeDialog
		};
		
		return service;
		
		/**
		 * Formats the date to allow proper updating of the stocks
		 * @params date
		 * */
		function formatDate(date) {
			var formattedDate = ($filter('date')(new Date(date), 'dd-MM-yyyy'));
			return formattedDate;
		}
		
		function findIndexByKeyValue(arraytosearch, key, valuetosearch) {
			for (var i = 0; i < arraytosearch.length; i++) {
				if (arraytosearch[i][key] == valuetosearch) {
					return i;
				}
			}
			return null;
		}
		
		
		/**
		 * All message labels used in the UI are defined here
		 * @returns {{}}
		 */
		function addMessageLabels() {
			var messages = {};
			return messages;
		}
		
		/**
		 * Disable and gray-out background when a dialog box opens up.
		 */
		function disableBackground() {
			var backgroundElement = angular.element('.simplemodal-overlay');
			backgroundElement.addClass('disable-background');
		}
		
		/**
		 * Show the generate report popup
		 * @param selectorId - div id
		 */
		function stockroomChangeDialog(selectorId, $scope) {
			var dialog = emr.setupConfirmationDialog({
				selector: '#' + selectorId,
				actions: {
					cancel: function () {
						dialog.close();
					},
					confirm: function () {
						$scope.loadStockDetails($scope.stockTakeCurrentPage);
						dialog.close();
					}
				}
			});
			
			dialog.show();
			disableBackground();
		}
	}
})();
