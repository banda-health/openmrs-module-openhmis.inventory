(function() {
	'use strict';
	angular.module('institutionsApp', [ 'ui.router', 'angularUtils.directives.dirPagination', 'app.css', 'app.filters', 'app.pagination', 'app.genericMetadataModel', 'app.restfulServices', 'app.genericEntityController', 'app.genericManageController' ])

	.config(function($stateProvider, $urlRouterProvider) {

		$urlRouterProvider.otherwise('/');

		$stateProvider

		.state('/', {
			url : '/',
			templateUrl : 'manageInstitutions.page',
			controller : 'ManageInstitutionController'
		})

		.state('edit', {
			url : '/:uuid',
			views : {
				'' : {
					templateUrl : 'institution.page',
					controller : 'InstitutionController'
				}
			}
		})

		.state('new', {
			url : '/',
			views : {
				'' : {
					templateUrl : 'institution.page',
					controller : 'InstitutionController'
				}
			}
		});
	});
})();
