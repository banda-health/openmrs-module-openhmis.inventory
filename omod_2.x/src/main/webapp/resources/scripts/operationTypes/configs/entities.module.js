/*
 * The module determines which page should be loaded depending on the url/route.
 * The manageOperationTypes.page page loads all operation types. The operationTypes.page
 * page either creates a new operation type if NO uuid is given, else loads an
 * existing operation type for editing.
 */
(function() {
	define([], loadpage);

	function loadpage() {
		'use strict';
		var app = angular.module('entitiesApp', ['ui.bootstrap', 'ngDialog',
				'ui.router', 'angularUtils.directives.dirPagination',
				'app.css', 'app.filters', 'app.pagination', 'app.cookies',
				'app.genericMetadataModel', 'app.restfulServices',
				'app.operationsTypeFunctionsFactory',
				'app.genericEntityController', 'app.genericManageController']);
		app.config(function($stateProvider, $urlRouterProvider, $provide) {
			/*
			 * Configure routes and urls. The default route is '/' which loads
			 * manageOperationType.page. 'edit' route calls operationTypes.page -- it
			 * appends a 'uuid' to the url to edit an existing operation types. 'new'
			 * route is called to create a new operation types.
			 */
			$urlRouterProvider.otherwise('/');
			$stateProvider.state('/', {
				url : '/',
				templateUrl : 'manageEntities.page',
				controller : 'ManageOperationTypesController'
			}).state('edit', {
				url : '/:uuid',
				views : {
					'' : {
						templateUrl : 'entity.page',
						controller : 'OperationTypesController'
					}
				}
			});

			$provide.factory('$exceptionHandler', function($injector) {
				return function(exception, cause) {
					/*
					 * There are times when the manage operation types's page won't render on
					 * initial page load -- which is brought about by inconsistencies in
					 * loading dependencies. As a work around we look out for such errors
					 * and reload the page. TODO: Find a better solution to ensure all
					 * dependencies are loaded before bootstrapping the application.
					 */
					// unknown provider..
					var exc = String(exception);
					if (exc.indexOf("unpr") !== -1) {
					} else if (exc.indexOf("session") !== -1
							|| exc.indexOf("timeout") !== -1) {
						console.log(exc + " - " + cause);
						emr.message("SESSION TIMEOUT");
					} else {
						console.log(exc + " - " + cause);
						emr.message(cause);
					}
				}
			});
		});
		return app;
	}
})();
