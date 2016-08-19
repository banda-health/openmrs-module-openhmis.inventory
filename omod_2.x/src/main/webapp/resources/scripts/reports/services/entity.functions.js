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

	var app = angular.module('app.reportsFunctionsFactory', []);
	app.service('ReportsFunctions', ReportsFunctions);

	ReportsFunctions.$inject = [ '$filter' ];

	function ReportsFunctions($filter) {
		var service;
		service = {
			formatDate : formatDate,
			onChangeDatePicker : onChangeDatePicker
		};

		function formatDate(date) {
			return $filter('date')(new Date(date), "dd-MM-yyyy");
		}

		function onChangeDatePicker(id, successfulCallback) {
			var datePicker = angular.element(document.getElementById(id));
			datePicker.bind('keyup change select checked', function() {
				var input = this.value;
				successfulCallback(input);
			});
		}

		return service;
	}

})();
