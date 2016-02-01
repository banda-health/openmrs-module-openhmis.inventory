(function() {
    'use strict';

    var base = angular.module('app.genericEntityController');
    base.controller("ItemController", ItemController);
    ItemController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory',
        'ItemModel', 'ngDialog', 'ItemFunctions', 'ItemRestfulService'];

    function ItemController($stateParams, $injector, $scope, $filter, EntityRestFactory, ItemModel, ngDialog, ItemFunctions, ItemRestfulService) {

        var self = this;

        var module_name = 'inventory';
        var entity_name = emr.message("openhmis.inventory.item.name");
        var cancel_page = 'items.page';
        var rest_entity_name = emr.message("openhmis.inventory.item.rest_name");

        // @Override
        self.setRequiredInitParameters = self.setRequiredInitParameters || function() {
                self.bindBaseParameters(module_name, rest_entity_name, entity_name, cancel_page);
            }

        /**
         * Initializes and binds any required variable and/or function specific to item.page
         * @type {Function}
         */
        // @Override
        self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
            || function(uuid) {
                if (angular.isDefined($scope.entity) && angular.isDefined($scope.entity.retired)
                    && $scope.entity.retired === true) {
                    $scope.retireOrUnretire = $filter('EmrFormat')(emr.message("openhmis.inventory.general.unretire"),
                        [self.entity_name]);
                } else {
                    $scope.retireOrUnretire = $filter('EmrFormat')(emr.message("openhmis.inventory.general.retire"),
                        [self.entity_name]);
                }

                /* bind variables.. */
                $scope.itemPrice = {};
                $scope.itemCode = {};
                $scope.uuid = uuid;
                $scope.itemStock = '';

                /* bind functions.. */
                // auto-complete search concept function
                $scope.searchConcepts = function(){
                    ItemRestfulService.searchConcepts(module_name, $scope.entity.concept, self.onSearchConceptsSuccessful);
                }

                // retrieve stocks (if any) associated to the item
                $scope.loadItemStock = function(){
                    ItemRestfulService.loadItemStock($scope.uuid, self.onLoadItemStockSuccessful);
                }

                // open dialog box to add an item price
                $scope.addItemPrice = function(){
                    ItemFunctions.addItemPrice(ngDialog, $scope);
                }

                // open dialog box to add an item code
                $scope.addItemCode = function(){
                    ItemFunctions.addItemCode(ngDialog, $scope);
                }

                // open dialog box to edit an item price
                $scope.editItemPrice = function(itemPrice){
                    ItemFunctions.editItemPrice(itemPrice, ngDialog, $scope);
                }

                // open dialog box to edit an item code
                $scope.editItemCode = function(itemCode){
                    ItemFunctions.editItemCode(itemCode, ngDialog, $scope);
                }

                // deletes an item price
                $scope.removeItemPrice = function(itemPrice){
                    ItemFunctions.removeItemPrice(itemPrice, $scope.entity.prices);
                }

                // deletes an item code
                $scope.removeItemCode = function(itemCode){
                    ItemFunctions.removeItemCode(itemCode, $scope.entity.codes);
                }

                /* bind functions to scope */
                $scope.selectConcept = self.selectConcept;
                $scope.retireUnretire = self.retireUnretire;
                $scope.delete = self.delete;
                $scope.itemPriceNameFormatter = ItemFunctions.itemPriceNameFormatter;

                // call functions..
                ItemRestfulService.loadDepartments(self.onLoadDepartmentsSuccessful);
                ItemRestfulService.loadItemStock($scope.uuid, self.onLoadItemStockSuccessful);
                ItemRestfulService.loadItemAttributeTypes(self.onLoadItemAttributeTypesSuccessful);
            }

        /**
         * All post-submit validations are done here.
         * @return boolean
         */
        // @Override
        self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function(){
                if(angular.isDefined($scope.itemAttributeTypes)){
                    var requestItemAttributeTypes = [];
                    for(var i = 0; i < $scope.itemAttributeTypes.length; i++){
                        var itemAttributeType = $scope.itemAttributeTypes[i];
                        var requestItemAttributeType = {};
                        requestItemAttributeType['attributeType'] = itemAttributeType.uuid;
                        var value = $scope.attributes[itemAttributeType.uuid] || "";
                        requestItemAttributeType['value'] = value;
                        requestItemAttributeTypes.push(requestItemAttributeType);
                    }

                    $scope.entity.attributes = requestItemAttributeTypes;
                }

                // DO NOT send the department object. Instead retrieve and set the uuid.
                var department = $scope.entity.department;
                if(angular.isDefined(department)){
                    $scope.entity.department = department.uuid;
                }
                // an empty buying price field should resolve to null and not an empty string
                if(!angular.isDefined($scope.entity.buyingPrice) || $scope.entity.buyingPrice === ''){
                    $scope.entity.buyingPrice = null;
                }
                // an empty minimum quantity should resolve to null and not an empty string
                if(!angular.isDefined($scope.entity.minimumQuantity) || $scope.entity.minimumQuantity === ''){
                    $scope.entity.minimumQuantity = null;
                }
                // an empty default expiration period should resolve to null and not an empty string
                if(!angular.isDefined($scope.entity.defaultExpirationPeriod) || $scope.entity.defaultExpirationPeriod === ''){
                    $scope.entity.defaultExpirationPeriod = null;
                }
                // empty codes should resolve as an empty array list
                if(!angular.isDefined($scope.entity.codes) || $scope.entity.codes === ''){
                    $scope.entity.codes = [];
                }
                // empty prices should resolve as an empty array list
                if(!angular.isDefined($scope.entity.prices) || $scope.entity.prices === ''){
                    $scope.entity.prices = [];
                }
                // retrieve and send the concept uuid.
                var concept = $scope.entity.concept;
                if(angular.isDefined(concept) && concept !== '' && concept !== undefined && concept !== null){
                    $scope.entity.concept = concept.uuid;
                }
                // remove temporarily assigned ids from the prices and codes array lists.
                self.removeItemTemporaryIds();

                if(!$scope.itemForm.$valid){
                    $scope.submitted = true;
                    return false;
                }

                return true;
            }

        // @Override
        self.setAdditionalMessageLabels = self.setAdditionalMessageLabels || function(){
                return ItemFunctions.addMessageLabels();
            }

        /* call-back functions. */
        // handle returned department list
        self.onLoadDepartmentsSuccessful = self.onLoadDepartmentsSuccessful || function(data){
            if(angular.isDefined($scope.entity)){
                $scope.departments = data.results;
                $scope.entity.department = $scope.entity.department || $scope.departments[0];
            }
        }

        // handle returned concepts
        self.onSearchConceptsSuccessful = self.onSearchConceptsSuccessful || function(data){
            $scope.concepts = data.results;
        }

        // handle returned stocks
        self.onLoadItemStockSuccessful = self.onLoadItemStockSuccessful || function(data){
            $scope.itemStock = data.results;
        }

        // handle returned item attribute types
        self.onLoadItemAttributeTypesSuccessful = self.onLoadItemAttributeTypesSuccessful || function(data){
            if(angular.isDefined($scope.entity)){
                $scope.itemAttributeTypes = data.results;
                $scope.attributes = {};
                if($scope.entity.attributes != null && $scope.entity.attributes.length > 0 ){
                    for(var i = 0; i < $scope.entity.attributes.length; i++){
                        var attribute = $scope.entity.attributes[i];
                        $scope.attributes[attribute.attributeType.uuid] = attribute.value;
                    }
                }
            }
        }

        /**
         * Removes the temporarily assigned unique ids before POSTing data
         * @type {Function}
         */
        self.removeItemTemporaryIds = self.removeItemTemporaryIds || function(){
            ItemFunctions.removeItemTemporaryId($scope.entity.codes);
            ItemFunctions.removeItemTemporaryId($scope.entity.prices);
        }

        /**
         * Binds the selected concept item to entity
         * @type {Function}
         * @parameter concept
         */
        self.selectConcept = self.selectConcept || function(concept){
            $scope.entity.concept = concept;
        }

        /**
         * removes the 'attributes' parameter from the entity object before retiring/unretiring
         * @type {Function}
         */
        self.retireUnretire = self.retireUnretire || function(){
            delete $scope.entity.attributes;
            $scope.retireOrUnretireCall();
        }

        /**
         * removes the 'attributes' parameter from the entity object before purging.
         * @type {Function}
         */
        self.delete = self.delete || function(){
            delete $scope.entity.attributes;
            $scope.purge();
        }

        /* ENTRY POINT: Instantiate the base controller which loads the page */
        $injector.invoke(base.GenericEntityController, self, {
            $scope: $scope,
            $filter: $filter,
            $stateParams: $stateParams,
            EntityRestFactory: EntityRestFactory,
            GenericMetadataModel: ItemModel
        });
    }
})();
