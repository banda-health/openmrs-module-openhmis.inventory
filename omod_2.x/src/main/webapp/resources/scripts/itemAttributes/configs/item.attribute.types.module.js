/*
 * The module determines which page should be loaded depending on the url/route.
 * The manageItemAttributeTypes.page page loads all item attributes. The itemAttributeType.page
 * page either creates a new attribute if NO uuid is given, else loads an
 * existing attribute type for editing.
 */
(function() {
    define(['itemAttributes/configs/modules.require'], loadpage);

    function loadpage() {
        'use strict';
        var app = angular.module('itemAttributeTypesApp', ['ui.bootstrap', 'ui.router', 'angularUtils.directives.dirPagination', 'app.css',
            'app.filters', 'app.pagination', 'app.cookies', 'app.genericMetadataModel', 'app.restfulServices',
            'app.genericEntityController', 'app.genericManageController']);
        app.config(function($stateProvider, $urlRouterProvider, $provide) {
            /*
             * Configure routes and urls. The default route is '/' which loads
             * manageItemAttributeTypes.page. 'edit' route calls itemAttributeType.page -- it
             * appends a 'uuid' to the url to edit an existing item attribute. 'new'
             * route is called to create a new item attribute.
             */
            $urlRouterProvider.otherwise('/');
            $stateProvider.state('/', {
                url: '/',
                templateUrl: 'manageItemAttributeTypes.page',
                controller: 'ManageItemAttributeTypesController'
            }).state('edit', {
                url: '/:uuid',
                views: {
                    '': {
                        templateUrl: 'itemAttributeType.page',
                        controller: 'ItemAttributeTypesController'
                    }
                }
            }).state('new', {
                url: '/',
                views: {
                    '': {
                        templateUrl: 'itemAttributeType.page',
                        controller: 'ItemAttributeTypesController'
                    }
                }
            });

            $provide.factory('$exceptionHandler', function($injector) {
                return function(exception, cause) {
                    /*
                     * There are times when the manage item attribute's page won't render on
                     * initial page load -- which is brought about by inconsistencies in
                     * loading dependencies. As a work around we look out for such errors
                     * and reload the page. TODO: Find a better solution to ensure all
                     * dependencies are loaded before bootstrapping the application.
                     */
                    // unknown provider..
                    var exc = String(exception);
                    if (exc.indexOf("unpr") !== -1) {
                        console.log(exc);
                        window.location.reload();
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