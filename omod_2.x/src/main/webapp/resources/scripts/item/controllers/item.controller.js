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

                var departmentsLimit;
                var conceptsLimit = 10;

                //bind variables..
                $scope.itemPrice = {};
                $scope.itemCode = {};
                $scope.uuid = uuid;
                $scope.itemStockLimit = $scope.itemStockLimit || 5;
                $scope.itemStock = '';

                // bind functions..
                $scope.searchConcepts = function(){
                    ItemRestfulService.searchConcepts(module_name, $scope.entity.concept, conceptsLimit, self.onSearchConceptsSuccessful);
                }

                $scope.loadItemStock = function(){
                    ItemRestfulService.loadItemStock($scope.uuid, $scope.itemStockLimit, self.onLoadItemStockSuccessful);
                }

                $scope.addItemPrice = self.addItemPrice;
                $scope.addItemCode = self.addItemCode;

                $scope.removeItemPrice = function(itemPrice){
                    ItemFunctions.removeItemPrice(itemPrice, $scope.entity.prices);
                }

                $scope.removeItemCode = function(itemCode){
                    ItemFunctions.removeItemCode(itemCode, $scope.entity.codes);
                }

                $scope.selectConcept = self.selectConcept;

                // call functions..
                ItemRestfulService.loadDepartments(departmentsLimit, self.onLoadDepartmentsSuccessful);
                ItemRestfulService.loadItemStock($scope.uuid, $scope.itemStockLimit, self.onLoadItemStockSuccessful);

            }

        self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function(){
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
            }

        // call-back functions.
        self.onLoadDepartmentsSuccessful = self.onLoadDepartmentsSuccessful || function(data){
            $scope.departments = data.results;
            $scope.entity.department = $scope.entity.department || $scope.departments[0];
        }

        self.onSearchConceptsSuccessful = self.onSearchConceptsSuccessful || function(data){
            $scope.concepts = data.results;
        }

        self.onLoadItemStockSuccessful = self.onLoadItemStockSuccessful || function(data){
            $scope.itemStock = data.results;
        }

        /**
         * Displays a popup dialog box with an item code field. Saves the code on clicking the 'Ok' button
         */
        self.addItemCode = self.addItemCode || function(){
            ngDialog.openConfirm({template: '/openmrs/openhmis.inventory/item/addItemCode.page',
                scope: $scope
            }).then(
                function(value){
                    $scope.entity.codes = $scope.entity.codes || [];
                    $scope.entity.codes.push($scope.itemCode);
                    ItemFunctions.insertItemTemporaryId($scope.entity.codes, $scope.itemCode);
                    $scope.itemCode = [];
                },
                function(value){
                    console.log('cancel');
                }
            );
        }

        /**
         * Displays a popup dialog box with price fields and saves the item price to a list.
         */
        self.addItemPrice = self.addItemPrice || function(){
            ngDialog.openConfirm({template: '/openmrs/openhmis.inventory/item/addItemPrice.page',
                scope: $scope
            }).then(
                function(value){
                    $scope.entity.prices = $scope.entity.prices || [];
                    $scope.entity.prices.push($scope.itemPrice);
                    ItemFunctions.insertItemTemporaryId($scope.entity.prices, $scope.itemPrice);
                    $scope.itemPrice = [];
                    $scope.entity.defaultPrice = $scope.entity.defaultPrice || $scope.entity.prices[0];
                },
                function(value){
                    console.log('cancel');
                }
            );
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
         * binds the selected concept item to entity
         * */
        self.selectConcept = self.selectConcept || function(concept){
                $scope.entity.concept = concept;
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
