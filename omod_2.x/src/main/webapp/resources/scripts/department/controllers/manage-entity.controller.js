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

    var base = angular.module('app.genericManageController');
    base.controller("ManageEntityController", ManageEntityController);
    ManageEntityController.$inject = ['$injector', '$scope', '$filter', 'EntityRestFactory', 'CssStylesFactory',
        'PaginationService', 'DepartmentModel', 'CookiesService'];

    var ENTITY_NAME = "department";

    function ManageEntityController($injector, $scope, $filter, EntityRestFactory, CssStylesFactory, PaginationService,
                                         DepartmentModel, CookiesService) {
        var self = this;

        var entity_name = emr.message("openhmis.inventory." + ENTITY_NAME + ".name");

        // @Override
        self.getModelAndEntityName = self.getModelAndEntityName || function() {
                self.bindBaseParameters(INVENTORY_MODULE_NAME, ENTITY_NAME, entity_name);
                self.checkPrivileges(TASK_MANAGE_METADATA);
            };

        // @Override
        self.bindExtraVariablesToScope = self.bindExtraVariablesToScope || function() {
                $scope.postSearchMessage = $filter('EmrFormat')(emr.message("openhmis.commons.general.postSearchMessage"),
                    [self.entity_name]);
            }

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
