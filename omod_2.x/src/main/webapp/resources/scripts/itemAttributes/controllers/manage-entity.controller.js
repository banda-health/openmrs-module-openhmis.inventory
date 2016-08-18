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
    base.controller("ManageItemAttributeTypesController", ManageItemAttributeTypesController);
    ManageItemAttributeTypesController.$inject = ['$injector', '$scope', '$filter', 'EntityRestFactory', 'CssStylesFactory',
        'PaginationService', 'ItemAttributeTypesModel', 'CookiesService'];

    function ManageItemAttributeTypesController($injector, $scope, $filter, EntityRestFactory, CssStylesFactory, PaginationService,
                                  ItemAttributeTypesModel, CookiesService) {

        var self = this;

        var entity_name = emr.message("openhmis.inventory.itemAttributeType");
        var REST_ENTITY_NAME = "itemAttributeType";

        // @Override
        self.getModelAndEntityName = self.getModelAndEntityName || function() {
                self.bindBaseParameters(INVENTORY_MODULE_NAME, REST_ENTITY_NAME, entity_name);
                self.checkPrivileges(TASK_MANAGE_METADATA);
            }

        /* ENTRY POINT: Instantiate the base controller which loads the page */
        $injector.invoke(base.GenericManageController, self, {
            $scope: $scope,
            $filter: $filter,
            EntityRestFactory: EntityRestFactory,
            PaginationService: PaginationService,
            CssStylesFactory: CssStylesFactory,
            GenericMetadataModel: ItemAttributeTypesModel,
            CookiesService: CookiesService
        });
    }
})();
