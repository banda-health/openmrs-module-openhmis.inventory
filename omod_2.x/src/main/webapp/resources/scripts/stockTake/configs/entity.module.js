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

/*
 * The module determines which page should be loaded depending on the url/route.
 * This loads and performs the  functions needed in the stock take page..
 */
(function () {
	define([], loadpage);
	
	function loadpage() {
		'use strict';
		var app = angular.module('entitiesApp', ['ui.bootstrap', 'ngDialog', 'ui.router', 'angularUtils.directives.dirPagination', 'app.css',
			'app.filters', 'app.stockTakeFunctionsFactory', 'app.pagination', 'app.cookies', 'app.genericMetadataModel', 'app.restfulServices',
			'app.genericEntityController', 'app.genericManageController']);
		app.config(function ($stateProvider, $urlRouterProvider, $provide) {
			/*
			 * Configure routes and urls. The default route is '/' which loads
			 * manageStockrooms.page. 'edit' route calls stockrooms.page -- it
			 * appends a 'uuid' to the url to edit an existing stockroom. 'new'
			 * route is called to create a new stockroom.
			 */
			$urlRouterProvider.otherwise('/');
			$stateProvider.state('/', {
				url: '/',
				templateUrl: 'entity.page',
				controller: 'StockTakeController'
			});
			
			$provide.factory('$exceptionHandler', function($injector) {
				return ohmis.handleException;
			});
		});
		return app;
	}
})();
