(function() {
    'use strict';

    var base = angular.module('app.genericManageController');
    base.controller("ManageEntityController", ManageEntityController);
    ManageEntityController.$inject = ['$injector', '$scope', '$filter', 'EntityRestFactory', 'CssStylesFactory',
        'PaginationService', 'DepartmentModel', 'CookiesService'];

    var ENTITY_NAME = "department";

    function ManageEntityController($injector, $scope, $filter, EntityRestFactory, CssStylesFactory, PaginationService,
                                         DepartmentModel, CookiesService) {
        var self = this;

        var module_name = 'inventory';
        var entity_name = emr.message("openhmis.inventory." + ENTITY_NAME + ".name");
        var rest_entity_name = ENTITY_NAME;

        // @Override
        self.getModelAndEntityName = self.getModelAndEntityName || function() {
                self.bindBaseParameters(module_name, rest_entity_name, entity_name);
            };

        /* ENTRY POINT: Instantiate the base controller which loads the page */
        $injector.invoke(base.GenericManageController, self, {
            $scope: $scope,
            $filter: $filter,
            EntityRestFactory: EntityRestFactory,
            PaginationService: PaginationService,
            CssStylesFactory: CssStylesFactory,
            GenericMetadataModel: DepartmentModel,
            CookiesService: CookiesService
        });
    }
})();
