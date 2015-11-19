/*
 * The module determines which page should be loaded depending on the url/route.
 * The manageInstitutions.page page loads all institutions. The instition.page
 * page either creates a new institution if NO uuid is given, else loads an
 * existing institution for editing.
 */
(function() {
  define(['institution/configs/modules.require'], loadpage);

  function loadpage() {
    'use strict';
    var app = angular.module('institutionsApp', ['ui.router', 'angularUtils.directives.dirPagination', 'app.css',
        'app.filters', 'app.pagination', 'app.cookies', 'app.genericMetadataModel', 'app.restfulServices',
        'app.genericEntityController', 'app.genericManageController']);
    app.config(function($stateProvider, $urlRouterProvider, $provide) {
      /*
       * Configure routes and urls. The default route is '/' which loads
       * manageInstitutions.page. 'edit' route calls institution.page -- it
       * appends a 'uuid' to the url to edit an existing institution. 'new'
       * route is called to create a new institution.
       */
      $urlRouterProvider.otherwise('/');
      $stateProvider.state('/', {
        url: '/',
        templateUrl: 'manageInstitutions.page',
        controller: 'ManageInstitutionController'
      }).state('edit', {
        url: '/:uuid',
        views: {
          '': {
            templateUrl: 'institution.page',
            controller: 'InstitutionController'
          }
        }
      }).state('new', {
        url: '/',
        views: {
          '': {
            templateUrl: 'institution.page',
            controller: 'InstitutionController'
          }
        }
      });

      $provide.factory('$exceptionHandler', function($injector) {
        return function(exception, cause) {
          /*
           * There are times when the manage institution's page won't render on
           * initial page load -- which is brought about by inconsistencies in
           * loading dependencies. As a work around we look out for such errors
           * and reload the page. TODO: Find a better solution to ensure all
           * dependencies are loaded before bootstrapping the application.
           */
          // unknown provider..
          if (cause.indexOf("unpr") !== -1) {
            window.location.reload();
          } else if (cause.indexOf("session") !== -1 || cause.indexOf("timeout") !== -1) {
            console.log(exception + " - " + cause);
            emr.message("SESSION TIMEOUT");
          } else {
            console.log(exception + " - " + cause);
            emr.message(cause);
          }
        }
      });
    });
    return app;
  }
})();