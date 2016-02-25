(function() {
    'use strict';

    var app = angular.module('app.stockOperationFunctionsFactory', []);
    app.service('StockOperationFunctions', StockOperationFunctions);

    StockOperationFunctions.$inject = [];

    function StockOperationFunctions() {
        var service;

        service = {
            addMessageLabels: addMessageLabels,
        };

        return service;

        /**
         * All message labels used in the UI are defined here
         * @returns {{}}
         */
        function addMessageLabels(){
            var messages = {};
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
            messages['openhmis.inventory.general.status'] = emr.message('openhmis.inventory.general.status');
            messages['openhmis.inventory.general.details'] = emr.message('openhmis.inventory.general.details');
            messages['openhmis.inventory.stockroom.transaction.name'] = emr.message('openhmis.inventory.stockroom.transaction.name');
            messages['openhmis.inventory.operations.operationCreators'] = emr.message('openhmis.inventory.operations.operationCreators');
            messages['openhmis.inventory.operations.sourceStockroom'] = emr.message('openhmis.inventory.operations.sourceStockroom');
            messages['openhmis.inventory.operations.destinationStockroom'] = emr.message('openhmis.inventory.operations.destinationStockroom');
            messages['openhmis.inventory.general.rollbackOperation'] = emr.message('openhmis.inventory.general.rollbackOperation');
            messages['openhmis.inventory.general.batchOperation'] = emr.message('openhmis.inventory.general.batchOperation');
            messages['openhmis.inventory.general.expiration'] = emr.message('openhmis.inventory.general.expiration');
            messages['openhmis.inventory.stockroom.name'] = emr.message('openhmis.inventory.stockroom.name');
            messages['openhmis.inventory.operations.operationDate'] = emr.message('openhmis.inventory.operations.operationDate');
            messages['openhmis.inventory.operations.operationNumber'] = emr.message('openhmis.inventory.operations.operationNumber');
            messages['openhmis.inventory.operations.operationType'] = emr.message('openhmis.inventory.operations.operationType');
            messages['openhmis.inventory.general.close'] = emr.message('openhmis.inventory.general.close');

            return messages;
        }
    }
})();