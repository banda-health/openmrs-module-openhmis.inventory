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
            EntityRestFactory.setCustomBaseUrl('/openmrs/');
            EntityRestFactory.loadResults(requestParams,
                onLoadFormatFieldsSuccessful,
                function(error){
                    console.log(error);
                }
            );
            //reset base url..
            EntityRestFactory.setBaseUrl(module_name);
        }

    }
})();
