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

                var departmentsLimit = 10;
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

                $scope.removeItemTemporaryIds = self.removeItemTemporaryIds;

                // call functions..
                ItemRestfulService.loadDepartments(departmentsLimit, self.onLoadDepartmentsSuccessful);
                ItemRestfulService.loadItemStock($scope.uuid, $scope.itemStockLimit, self.onLoadItemStockSuccessful);

            }

        // call-back functions.
        self.onLoadDepartmentsSuccessful = self.onLoadDepartmentsSuccessful || function(data){
            $scope.departments = data.results;
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
            console.log('add item code');
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
            console.log('add a price..');
            ngDialog.openConfirm({template: '/openmrs/openhmis.inventory/item/addItemPrice.page',
                scope: $scope
            }).then(
                function(value){
                    $scope.entity.prices = $scope.entity.prices || [];
                    $scope.entity.prices.push($scope.itemPrice);
                    ItemFunctions.insertItemTemporaryId($scope.entity.prices, $scope.itemPrice);
                    console.log('added..');
                    console.log($scope.entity.prices);
                    $scope.itemPrice = [];
                },
                function(value){
                    console.log('cancel');
                }
            );
        }

        self.removeItemTemporaryIds = self.removeItemTemporaryIds || function(){
                ItemFunctions.removeItemTemporaryId($scope.entity.codes);
                ItemFunctions.removeItemTemporaryId($scope.entity.prices);
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
