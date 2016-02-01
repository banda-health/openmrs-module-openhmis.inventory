(function() {
    'use strict';

    var app = angular.module('app.itemFunctionsFactory', []);
    app.service('ItemFunctions', ItemFunctions);

    ItemFunctions.$inject = [];

    function ItemFunctions() {
        var service;

        service = {
            removeItemPrice: removeItemPrice,
            removeItemCode: removeItemCode,
            insertItemTemporaryId: insertItemTemporaryId,
            removeItemTemporaryId: removeItemTemporaryId,
            itemPriceNameFormatter: itemPriceNameFormatter,
            addItemCode: addItemCode,
            addItemPrice: addItemPrice,
            editItemPrice: editItemPrice,
            editItemCode: editItemCode,
            addMessageLabels: addMessageLabels,
        };

        return service;

        /**
         * Removes an item price from the list
         * @param itemPrice
         * @param itemPrices
         */
        function removeItemPrice(itemPrice, itemPrices){
            removeFromList(itemPrice, itemPrices);
        }

        /**
         * Removes an item code from the list
         * @param itemCode
         * @param itemCodes
         */
        function removeItemCode(itemCode, itemCodes){
            removeFromList(itemCode, itemCodes);
        }

        /**
         * Searches an item and removes it from the list
         * @param item
         * @param items
         */
        function removeFromList(item, items){
            var index = items.indexOf(item);
            items.splice(index, 1);
        }

        /**
         * ng-repeat requires that every item have a unique identifier.
         * This function sets a temporary unique id for all items in the list.
         * @param items (prices, codes)
         * @param item - optional
         */
        function insertItemTemporaryId(items, item){
            if(angular.isDefined(item)){
                var index = items.indexOf(item);
                item.id = index;
            }
            else{
                for(var item in items){
                    var index = items.indexOf(item);
                    item.id = index;
                }
            }
        }

        /**
         * Remove the temporary unique id from all items (prices, codes) before submitting.
         * @param items
         */
        function removeItemTemporaryId(items){
            for(var index in items){
                var item = items[index];
                delete item.id;
            }
        }

        /**
         * Format the item price name
         * @param itemPrice
         * @returns {string}
         */
        function itemPriceNameFormatter(itemPrice){
            var priceName;
            if(angular.isDefined(itemPrice.name) && itemPrice.name != '' && itemPrice.name != undefined){
                priceName = itemPrice.price.toFixed(2) + " (" + itemPrice.name + ")";
            }
            else{
                priceName = itemPrice.price.toFixed(2);
            }

            return priceName;
        }

        /**
         * Displays a popup dialog box with an item code field. Saves the code on clicking the 'Ok' button
         * @param ngDialog
         * @param $scope
         */
        function addItemCode(ngDialog, $scope){
            ngDialog.openConfirm({template: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/item/addItemCode.page',
                scope: $scope
            }).then(
                function(value){
                    $scope.entity.codes = $scope.entity.codes || [];
                    $scope.submitted = true;
                    if(angular.isDefined($scope.itemCode) && $scope.itemCode.code !== ""){
                        $scope.entity.codes.push($scope.itemCode);
                        insertItemTemporaryId($scope.entity.codes, $scope.itemCode);
                        $scope.itemCode = [];
                    }
                },
                function(value){
                    console.log('cancel');
                }
            );
        }

        /**
         * Displays a popup dialog box with price fields and saves the item price to a list.
         * @param ngDialog
         * @param $scope
         */
        function addItemPrice(ngDialog, $scope){
            ngDialog.openConfirm({template: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/item/addItemPrice.page',
                scope: $scope
            }).then(
                function(value){
                    $scope.entity.prices = $scope.entity.prices || [];
                    $scope.entity.prices.push($scope.itemPrice);
                    insertItemTemporaryId($scope.entity.prices, $scope.itemPrice);
                    $scope.itemPrice = [];
                    $scope.entity.defaultPrice = $scope.entity.defaultPrice || $scope.entity.prices[0];
                },
                function(value){
                    console.log('cancel');
                }
            );
        }

        /**
         * Opens a popup dialog box to edit an item price
         * @param itemPrice
         * @param ngDialog
         * @param $scope
         */
        function editItemPrice(itemPrice, ngDialog, $scope) {
            $scope.itemPrice = itemPrice;
            ngDialog.openConfirm({template: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/item/addItemPrice.page',
                scope: $scope
            });
        }

        /**
         * Opens a popup dialog box to edit an item code
         * @param itemCode
         * @param ngDialog
         * @param $scope
         */
        function editItemCode(itemCode, ngDialog, $scope){
            $scope.itemCode = itemCode;
            ngDialog.openConfirm({
                template: '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/item/addItemCode.page',
                scope: $scope
            });
        }

        /**
         * All message labels used in the UI are defined here
         * @returns {{}}
         */
        function addMessageLabels(){
            var messages = {};
            messages['openhmis.inventory.item.enterConceptName'] = emr.message('openhmis.inventory.item.enterConceptName');
            messages['openhmis.inventory.item.price.name'] = emr.message('openhmis.inventory.item.price.name');
            messages['openhmis.inventory.item.code.name'] = emr.message('openhmis.inventory.item.code.name');
            messages['openhmis.inventory.department.name'] = emr.message('openhmis.inventory.department.name');
            messages['openhmis.inventory.item.hasExpiration'] = emr.message('openhmis.inventory.item.hasExpiration');
            messages['openhmis.inventory.item.defaultExpirationPeriod'] = emr.message('openhmis.inventory.item.defaultExpirationPeriod');
            messages['Concept'] = emr.message('Concept');
            messages['openhmis.inventory.item.hasPhysicalInventory'] = emr.message('openhmis.inventory.item.hasPhysicalInventory');
            messages['openhmis.inventory.item.minimumQuantity'] = emr.message('openhmis.inventory.item.minimumQuantity');
            messages['openhmis.inventory.item.buyingPrice'] = emr.message('openhmis.inventory.item.buyingPrice');
            messages['openhmis.inventory.item.code.namePlural'] = emr.message('openhmis.inventory.item.code.namePlural');
            messages['openhmis.inventory.item.prices'] = emr.message('openhmis.inventory.item.prices');
            messages['openhmis.inventory.item.defaultPrice'] = emr.message('openhmis.inventory.item.defaultPrice');
            messages['openhmis.inventory.stockroom.name'] = emr.message('openhmis.inventory.stockroom.name');
            messages['openhmis.inventory.item.quantity'] = emr.message('openhmis.inventory.item.quantity');
            return messages;
        }
    }
})();
