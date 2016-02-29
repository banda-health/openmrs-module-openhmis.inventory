/*
 * The module determines which page should be loaded depending on the url/route.
 * The manage stockrooms page loads all stockrooms. The stockrooms.page
 * page either creates a new stockroom if NO uuid is given, else loads an
 * existing stockroom for editing.
 */
(function() {
    define([], loadpage);

    function loadpage() {
        'use strict';
        var app = angular.module('stockroomsApp', ['ui.bootstrap', 'ngDialog', 'ui.router', 'angularUtils.directives.dirPagination', 'app.css',
            'app.filters', 'app.stockroomsFunctionsFactory', 'app.pagination', 'app.cookies', 'app.genericMetadataModel', 'app.restfulServices',
            'app.entityFunctionsFactory',
            'app.genericEntityController', 'app.genericManageController']);
        app.config(function($stateProvider, $urlRouterProvider, $provide) {
            /*
             * Configure routes and urls. The default route is '/' which loads
             * manageStockrooms.page. 'edit' route calls stockrooms.page -- it
             * appends a 'uuid' to the url to edit an existing stockroom. 'new'
             * route is called to create a new stockroom.
             */
            $urlRouterProvider.otherwise('/');
            $stateProvider.state('/', {
                url: '/',
                templateUrl: 'manageEntities.page',
                controller: 'ManageStockroomsController'
            }).state('edit', {
                url: '/:uuid',
                views: {
                    '': {
                        templateUrl: 'entity.page',
                        controller: 'StockroomController'
                    }
                }
            }).state('new', {
                url: '/',
                views: {
                    '': {
                        templateUrl: 'entity.page',
                        controller: 'StockroomController'
                    }
                }
            });

            $provide.factory('$exceptionHandler', function($injector) {
                return function(exception, cause) {
                    /*
                     * Handle common exceptions
                     */
                    // unknown provider..
                    var exc = String(exception);
                    if (exc.indexOf("unpr") !== -1) {
                        console.log("ISSUE LOADING MODULE(S)");
                        console.log(exc);
                    } else if (exc.indexOf("session") !== -1 || exc.indexOf("timeout") !== -1) {
                        emr.message("SESSION TIMEOUT");
                        console.log(exc + " - " + cause);
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