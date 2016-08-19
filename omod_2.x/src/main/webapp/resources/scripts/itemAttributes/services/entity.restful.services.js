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

    angular.module('app.restfulServices').service('ItemAttributeTypesRestfulService', ItemAttributeTypesRestfulService);

    ItemAttributeTypesRestfulService.$inject = ['EntityRestFactory'];

    function ItemAttributeTypesRestfulService(EntityRestFactory) {
        var service;

        service = {
            loadFormatFields: loadFormatFields,
        };

        return service;

        function loadFormatFields(module_name, onLoadFormatFieldsSuccessful) {
            var requestParams = [];
            requestParams['resource'] = 'fieldgenhandlers.json';
            EntityRestFactory.setCustomBaseUrl(ROOT_URL);
            EntityRestFactory.loadResults(requestParams,
                onLoadFormatFieldsSuccessful,errorCallback
            );
            //reset base url..
            EntityRestFactory.setBaseUrl(module_name);
        }
    
        function errorCallback(error) {
            emr.errorAlert(error);
        }

    }
})();
