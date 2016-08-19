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
 * The manageitems.page page loads all items. The item.page
 * page either creates a new item if NO uuid is given, else loads an
 * existing item for editing.
 */
(function() {
	define([], loadpage);

	function loadpage() {
		'use strict';
		var app = angular.module('entitiesApp', ['ui.bootstrap', 'ngDialog',
				'ui.router', 'angularUtils.directives.dirPagination',
				'app.css', 'app.filters', 'app.pagination', 'app.cookies',
				'app.genericMetadataModel', 'app.restfulServices',
				'app.itemFunctionsFactory', 'app.genericEntityController',
				'app.genericManageController']);
		app.config(function($stateProvider, $urlRouterProvider, $provide) {
			/*
			 * Configure routes and urls. The default route is '/' which loads
			 * manageItems.page. 'edit' route calls item.page -- it
			 * appends a 'uuid' to the url to edit an existing item. 'new'
			 * route is called to create a new item.
			 */
			$urlRouterProvider.otherwise('/');
			$stateProvider.state('/', {
				url : '/',
				templateUrl : 'manageEntities.page',
				controller : 'ManageItemController'
			}).state('edit', {
				url : '/:uuid',
				views : {
					'' : {
						templateUrl : 'entity.page',
						controller : 'ItemController'
					}
				}
			}).state('new', {
				url : '/',
				views : {
					'' : {
						templateUrl : 'entity.page',
						controller : 'ItemController'
					}
				}
			});
			
			$provide.factory('$exceptionHandler', function($injector) {
				return ohmis.handleException;
			});
		});
		return app;
	}
})();
