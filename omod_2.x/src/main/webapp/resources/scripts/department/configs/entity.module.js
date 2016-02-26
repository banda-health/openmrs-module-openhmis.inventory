/*
 * The module determines which page should be loaded depending on the url/route.
 * The manageEntities.page page loads all items. The entity.page
 * page either creates a new entity if NO uuid is given, else loads an
 * existing entity for editing.
 */
(function() {
    define([], loadPage);

    function loadPage() {
        'use strict';

        var app = angular.module('entitiesApp', [
            'ui.bootstrap',
            'ui.router',
            'angularUtils.directives.dirPagination',
            'app.css',
            'app.filters',
            'app.pagination',
            'app.cookies',
            'app.genericMetadataModel',
            'app.restfulServices',
            'app.genericEntityController',
            'app.genericManageController'
        ]);
        app.config(function($stateProvider, $urlRouterProvider, $provide) {
            /*
             * Configure routes and urls. The default route is '/' which loads
             * manageEntities.page. 'edit' route calls entity.page -- it
             * appends a 'uuid' to the url to edit an existing entity. 'new'
             * route is called to create a new entity.
             */
            $urlRouterProvider.otherwise('/');
            $stateProvider.state('/', {
                url: '/',
                templateUrl: 'manageEntities.page',
                controller: 'ManageEntityController'
            }).state('edit', {
                url: '/:uuid',
                views: {
                    '': {
                        templateUrl: 'entity.page',
                        controller: 'EntityController'
                    }
                }
            }).state('new', {
                url: '/',
                views: {
                    '': {
                        templateUrl: 'entity.page',
                        controller: 'EntityController'
                    }
                }
            });

            $provide.factory('$exceptionHandler', function($injector) {
                return function(exception, cause) {
                    /*
                     * There are times when the manageEntities.page won't render on
                     * initial page load -- which is brought about by inconsistencies in
                     * loading dependencies. As a work around we look out for such errors
                     * and reload the page. TODO: Find a better solution to ensure all
                     * dependencies are loaded before bootstrapping the application.
                     */
                    // unknown provider..
                    var exc = String(exception);
                    if (exc.indexOf("unpr") !== -1) {
                        console.log(exc);
                    } else if (exc.indexOf("session") !== -1 || exc.indexOf("timeout") !== -1) {
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
