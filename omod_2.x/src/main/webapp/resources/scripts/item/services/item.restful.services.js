(function() {
    'use strict';

    angular.module('app.restfulServices').service('ItemRestfulService', ItemRestfulService);

    ItemRestfulService.$inject = ['EntityRestFactory'];

    function ItemRestfulService(EntityRestFactory) {
        var service;

        service = {
            loadDepartments: loadDepartments,
            searchConcepts: searchConcepts,
            loadItemStock: loadItemStock,
            loadItemAttributeTypes: loadItemAttributeTypes,
        };

        return service;

        /**
         * Temporary Function: It will ONLY be used until the Department module is done.
         * @param limit
         * @param onLoadDepartmentsSuccessful
         */
        function loadDepartments(limit, onLoadDepartmentsSuccessful) {
            var requestParams = [];
            requestParams['rest_entity_name'] = 'department';
            requestParams['limit'] = limit;
            EntityRestFactory.loadEntities(requestParams,
                onLoadDepartmentsSuccessful,
                errorCallback
            );
        }

        /**
         * An auto-complete function to search concepts given a query term.
         * @param module_name
         * @param q - search term
         * @param limit
         * @param onSearchConceptsSuccessful
         */
        function searchConcepts(module_name, q, limit, onSearchConceptsSuccessful){
            var requestParams = [];
            requestParams['rest_entity_name'] = '';
            requestParams['q'] = q;
            requestParams['limit'] = limit;
            EntityRestFactory.setBaseUrl('concept', 'v1');
            EntityRestFactory.loadEntities(requestParams,
                onSearchConceptsSuccessful,
                errorCallback
            );
            //reset base url..
            EntityRestFactory.setBaseUrl(module_name);
        }

        function loadItemAttributeTypes(module_name, onLoadAttributeTypesSuccessful){
            var requestParams = [];
            requestParams['rest_entity_name'] = 'itemAttributeType';
            EntityRestFactory.loadEntities(requestParams,
                onLoadAttributeTypesSuccessful,
                errorCallback
            );
        }

        /**
         * Retrieve an item stock given a uuid.
         * @param uuid
         * @param limit
         * @param onLoadItemStockSuccessful
         */
        function loadItemStock(uuid, limit, onLoadItemStockSuccessful){
            if(angular.isDefined(uuid)){
                var requestParams = [];
                requestParams['rest_entity_name'] = 'itemStock';
                requestParams['limit'] = limit;
                requestParams['item_uuid'] = uuid;
                EntityRestFactory.loadEntities(requestParams,
                    onLoadItemStockSuccessful,
                    errorCallback
                );
            }
        }

        function errorCallback(error){
            console.log(error);
        }
    }
})();
