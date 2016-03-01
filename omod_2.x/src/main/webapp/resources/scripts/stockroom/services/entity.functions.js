
(function() {
    'use strict';

    var app = angular.module('app.stockroomsFunctionsFactory', []);
    app.service('StockroomsFunctions', StockroomsFunctions);

    StockroomsFunctions.$inject = [];

    function StockroomsFunctions() {
        var service;

        service = {
            showItemDetails: showItemDetails,
            addMessageLabels: addMessageLabels,
        };

        return service;

        function showItemDetails(){
            console.log('show item details..');
            var dialog = emr.setupConfirmationDialog({
                selector: '#item-details-dialog',
                actions: {
                    cancel: function() {
                        dialog.close();
                    }
                }
            });

            dialog.show();

            var dialogBox = angular.element('#item-details-dialog');
            dialogBox.attr('style', 'display:b2ock;');
        }

        /**
         * All message labels used in the UI are defined here
         * @returns {{}}
         */
        function addMessageLabels(){
            var messages = {};
            messages['openhmis.inventory.location.name'] = emr.message('openhmis.inventory.location.name');
            messages['openhmis.inventory.stockroom.details'] = emr.message('openhmis.inventory.stockroom.details');
            messages['openhmis.inventory.stockroom.transactions'] = emr.message('openhmis.inventory.stockroom.transactions');
            messages['openhmis.inventory.stockroom.expiration'] = emr.message('openhmis.inventory.stockroom.expiration');
            messages['openhmis.inventory.stockroom.operationNumber'] = emr.message('openhmis.inventory.stockroom.operationNumber');
            messages['openhmis.inventory.stockroom.status'] = emr.message('openhmis.inventory.stockroom.status');
            messages['openhmis.inventory.stockroom.dateCreated'] = emr.message('openhmis.inventory.stockroom.dateCreated');
            messages['openhmis.inventory.stockroom.expiration'] = emr.message('openhmis.inventory.stockroom.expiration');
            messages['openhmis.inventory.stockroom.batchOperation'] = emr.message('openhmis.inventory.stockroom.batchOperation');
            messages['openhmis.inventory.operations.namePlural'] = emr.message('openhmis.inventory.operations.namePlural');
            messages['openhmis.inventory.item.namePlural'] = emr.message('openhmis.inventory.item.namePlural');
            messages['openhmis.inventory.item.name'] = emr.message('openhmis.inventory.item.name');
            messages['openhmis.inventory.item.quantity'] = emr.message('openhmis.inventory.item.quantity');
            messages['openhmis.inventory.stockroom.batchOperation'] = emr.message('openhmis.inventory.stockroom.batchOperation');
            messages['openhmis.inventory.stockroom.expiration'] = emr.message('openhmis.inventory.stockroom.expiration');
            messages['openhmis.inventory.stockroom.dateCreated'] = emr.message('openhmis.inventory.stockroom.dateCreated');
            messages['openhmis.inventory.operations.type.name'] = emr.message('openhmis.inventory.operations.type.name');
            messages['openhmis.inventory.stockroom.operationNumber'] = emr.message('openhmis.inventory.stockroom.operationNumber');
            messages['openhmis.inventory.stockroom.status'] = emr.message('openhmis.inventory.stockroom.status');
            messages['openhmis.inventory.stockroom.details'] = emr.message('openhmis.inventory.stockroom.details');
            messages['openhmis.inventory.stockroom.dateCreated'] = emr.message('openhmis.inventory.stockroom.dateCreated');
            messages['openhmis.inventory.stockroom.status'] = emr.message('openhmis.inventory.stockroom.status');
            messages['openhmis.inventory.stockroom.transactions'] = emr.message('openhmis.inventory.stockroom.transactions');
            return messages;
        }
    }
})();
