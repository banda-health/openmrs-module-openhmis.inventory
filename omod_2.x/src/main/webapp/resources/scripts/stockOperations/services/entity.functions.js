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

    var app = angular.module('app.stockOperationFunctionsFactory', []);
    app.service('StockOperationFunctions', StockOperationFunctions);

    StockOperationFunctions.$inject = ['EntityFunctions'];

    function StockOperationFunctions(EntityFunctions) {
        var service;

        service = {
            showOperationActionsDialog: showOperationActionsDialog,
            addMessageLabels: addMessageLabels,
        };

        return service;

        function showOperationActionsDialog(selectorId){
            var dialog = emr.setupConfirmationDialog({
                selector: '#' + selectorId,
                actions: {
                    cancel: function(){
                        dialog.close();
                    }
                }
            });

            dialog.show();

            EntityFunctions.disableBackground();
        }

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
            messages['openhmis.commons.general.status'] = emr.message('openhmis.commons.general.status');
            messages['openhmis.commons.general.details'] = emr.message('openhmis.commons.general.details');
            messages['openhmis.inventory.stockroom.transaction.name'] = emr.message('openhmis.inventory.stockroom.transaction.name');
            messages['openhmis.inventory.operations.operationCreators'] = emr.message('openhmis.inventory.operations.operationCreators');
            messages['openhmis.inventory.operations.sourceStockroom'] = emr.message('openhmis.inventory.operations.sourceStockroom');
            messages['openhmis.inventory.operations.destinationStockroom'] = emr.message('openhmis.inventory.operations.destinationStockroom');
            messages['openhmis.commons.general.rollbackOperation'] = emr.message('openhmis.commons.general.rollbackOperation');
            messages['openhmis.commons.general.batchOperation'] = emr.message('openhmis.commons.general.batchOperation');
            messages['openhmis.commons.general.expiration'] = emr.message('openhmis.commons.general.expiration');
            messages['openhmis.inventory.stockroom.name'] = emr.message('openhmis.inventory.stockroom.name');
            messages['openhmis.inventory.operations.operationDate'] = emr.message('openhmis.inventory.operations.operationDate');
            messages['openhmis.inventory.operations.operationNumber'] = emr.message('openhmis.inventory.operations.operationNumber');
            messages['openhmis.inventory.operations.operationType'] = emr.message('openhmis.inventory.operations.operationType');
            messages['openhmis.commons.general.close'] = emr.message('openhmis.commons.general.close');
            messages['openhmis.commons.general.auto'] = emr.message('openhmis.commons.general.auto');
            messages['openhmis.commons.general.cancelReason'] = emr.message('openhmis.commons.general.cancelReason');

            return messages;
        }
    }
})();
