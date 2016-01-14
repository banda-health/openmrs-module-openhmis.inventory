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
    }
})();
