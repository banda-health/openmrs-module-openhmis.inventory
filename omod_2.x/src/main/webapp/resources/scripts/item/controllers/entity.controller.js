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

    var base = angular.module('app.genericEntityController');
    base.controller("ItemController", ItemController);
    ItemController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory',
        'ItemModel', 'ItemFunctions', 'ItemRestfulService'];

    function ItemController($stateParams, $injector, $scope, $filter, EntityRestFactory, ItemModel, ItemFunctions, ItemRestfulService) {

        var self = this;

        var entity_name_message_key = "openhmis.inventory.item.name";
        var REST_ENTITY_NAME = "item";

        // @Override
        self.setRequiredInitParameters = self.setRequiredInitParameters || function() {
                self.bindBaseParameters(INVENTORY_MODULE_NAME, REST_ENTITY_NAME, entity_name_message_key, RELATIVE_CANCEL_PAGE_URL);
                self.checkPrivileges(TASK_MANAGE_METADATA);
            }

        /**
         * Initializes and binds any required variable and/or function specific to item.page
         * @type {Function}
         */
        // @Override
        self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
            || function(uuid) {
                /* bind variables.. */
                $scope.itemPrice = {};
                $scope.tmpItemPrice = {};
                $scope.itemCode = {};

                $scope.uuid = uuid;
                $scope.itemStock = '';

                /* bind functions.. */
                // auto-complete search concept function
                $scope.searchConcepts = function (search) {
                    return ItemRestfulService.searchConcepts(INVENTORY_MODULE_NAME, search);
                };

                // retrieve stocks (if any) associated to the item
                $scope.loadItemStock = function () {
                    ItemRestfulService.loadItemStock($scope.uuid, self.onLoadItemStockSuccessful);
                }

                // open dialog box to add an item price
                $scope.addItemPrice = function () {
                    ItemFunctions.addItemPrice($scope);
                }

                // open dialog box to add an item code
                $scope.addItemCode = function () {
                    ItemFunctions.addItemCode($scope);
                }

                // open dialog box to edit an item price
                $scope.editItemPrice = function (itemPrice) {
                    ItemFunctions.editItemPrice(itemPrice, $scope);
                }

                // open dialog box to edit an item code
                $scope.editItemCode = function (itemCode) {
                    ItemFunctions.editItemCode(itemCode, $scope);
                }

                // deletes an item price
                $scope.removeItemPrice = function (itemPrice) {
                    var removeDefaultPrice = false;
                    if ("id" in itemPrice && "id" in $scope.entity.defaultPrice && itemPrice.id === $scope.entity.defaultPrice.id) {
                        removeDefaultPrice = true;
                    }
                    else if ("uuid" in itemPrice && "uuid" in $scope.entity.defaultPrice && itemPrice.uuid === $scope.entity.defaultPrice.uuid) {
                        removeDefaultPrice = true;
                    }

                    ItemFunctions.removeItemPrice(itemPrice, $scope.entity.prices);

                    if ($scope.entity.prices.length === 0) {
                        $scope.entity.defaultPrice = null;
                    }

                    //default price removed. set the first item on the list as the new default price.
                    if ((removeDefaultPrice && $scope.entity.prices.length > 0) || $scope.entity.defaultPrice === null) {
                        $scope.entity.defaultPrice = $scope.entity.prices[0];
                    }
                }

                // deletes an item code
                $scope.removeItemCode = function (itemCode) {
                    ItemFunctions.removeItemCode(itemCode, $scope.entity.codes);
                }

                /* bind functions to scope */
                $scope.selectConcept = self.selectConcept;
                $scope.retireUnretire = self.retireUnretire;
                $scope.delete = self.delete;
                $scope.itemPriceNameFormatter = ItemFunctions.itemPriceNameFormatter;
                $scope.formatItemPrice = ItemFunctions.formatItemPrice;

                // call functions..
                ItemRestfulService.loadDepartments(self.onLoadDepartmentsSuccessful);
                ItemRestfulService.loadItemStock($scope.uuid, self.onLoadItemStockSuccessful);
                ItemRestfulService.loadItemAttributeTypes(self.onLoadItemAttributeTypesSuccessful);

                if ($scope.entity !== undefined) {
                    if ("department" in $scope.entity) {
                        $scope.department = $scope.entity.department;
                    }

                    if ("concept" in $scope.entity) {
                        $scope.concept = $scope.entity.concept;
                    }
                }
            }

        /**
         * All post-submit validations are done here.
         * @return boolean
         */
        // @Override
        self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function(){
                $scope.submitted = false;
                // validate name.
                if(!angular.isDefined($scope.entity.name) || $scope.entity.name === ''){
                    $scope.submitted = true;
                    emr.errorAlert(emr.message("openhmis.commons.general.name.required"));
                }

                // validate prices.
                if($scope.entity.prices.length === 0){
                    $scope.submitted = true;
                    emr.errorAlert(emr.message("openhmis.commons.general.required.prices"));
                }
                else {
                    var defaultPriceSet = false;
                    for (var i = 0; i < $scope.entity.prices.length; i++) {
                        var price = $scope.entity.prices[i];

                        // checks for invalid prices.
                        if(price['price'] === undefined){
                            $scope.submitted = true;
                            var errorMsg = $filter('EmrFormat')(emr.message("openhmis.commons.general.required.priceValue"), [price['name']]);
                            emr.errorAlert(errorMsg);
                        }

                        // check if the default price has been set correctly.
                        if ("id" in price) {
                            if ("id" in $scope.entity.defaultPrice && price.id === $scope.entity.defaultPrice.id) {
                                defaultPriceSet = true;
                            }
                        }
                        else {
                            if ("uuid" in $scope.entity.defaultPrice && price.uuid === $scope.entity.defaultPrice.uuid) {
                                defaultPriceSet = true;
                            }
                        }
                    }

                    if(!defaultPriceSet ){
                        $scope.submitted = true;
                        emr.errorAlert(emr.message("openhmis.commons.general.required.defaultPrice"));
                    }
                }

                // validate item attribute types.
                if(angular.isDefined($scope.itemAttributeTypes)){
                    var requestItemAttributeTypes = [];
                    var failItemAttributeTypeValidation = false;
                    for(var i = 0; i < $scope.itemAttributeTypes.length; i++){
                        var itemAttributeType = $scope.itemAttributeTypes[i];
                        var required = itemAttributeType.required;
                        var requestItemAttributeType = {};
                        requestItemAttributeType['attributeType'] = itemAttributeType.uuid;
                        var value = $scope.attributes[itemAttributeType.uuid] || "";
                        if(required && value === ""){
                            $scope.submitted = true;
                            var errorMsg = $filter('EmrFormat')(emr.message("openhmis.commons.general.required.itemAttribute"), [itemAttributeType.name]);
                            emr.errorAlert(errorMsg);
                            failItemAttributeTypeValidation = true;
                        }
                        else{
                            requestItemAttributeType['attributeType'] = itemAttributeType.uuid;
                            var value = $scope.attributes[itemAttributeType.uuid] || "";
                            requestItemAttributeType['value'] = value;
                            requestItemAttributeTypes.push(requestItemAttributeType);
                        }
                    }

                    if(failItemAttributeTypeValidation){
                        $scope.submitted = true;
                    }
                    else{
                        $scope.entity.attributes = requestItemAttributeTypes;
                    }
                }

                if($scope.submitted){
                    return false;
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
                /*Set the default price name to empty if the use didn't add it. This is to avoid it being saved as null
                 which causes the empty parantheses when you view the items*/
                if (!angular.isDefined($scope.entity.defaultPrice.name)) {
                    $scope.entity.defaultPrice.name = '';
                }
                // retrieve and send the concept uuid.
                var concept = $scope.concept;
                if(angular.isDefined(concept) && concept !== '' && concept !== undefined && concept !== null){
                    $scope.entity.concept = concept.uuid;
                }
                // remove temporarily assigned ids from the prices and codes array lists.
                self.removeItemTemporaryIds();

                // bind department uuid
                var department = $scope.department;
                if(angular.isDefined(department)){
                    $scope.entity.department = department.uuid;
                }

                $scope.loading = true;
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
                $scope.department = $scope.department || $scope.departments[0];
            }
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
            $scope.concept = concept;
            $scope.entity.concept = $scope.concept;    
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
