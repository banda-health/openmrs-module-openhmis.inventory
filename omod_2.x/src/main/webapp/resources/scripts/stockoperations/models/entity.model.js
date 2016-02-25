(function() {
    'use strict';

    var baseModel = angular.module('app.genericMetadataModel');

    /* Define model fields */
    function StockOperationModel(GenericMetadataModel) {

        var extended = angular.extend(GenericMetadataModel, {});

        var defaultFields = extended.getModelFields();

        // @Override
        extended.getModelFields = function() {
            var fields = ["cancelReason", "dateCreated", "instanceType", "operationDate", "operationNumber", "OperationOrder", "status"];
            return fields.concat(defaultFields);
        };

        return extended;
    }

    baseModel.factory("StockOperationModel", StockOperationModel);

    StockOperationModel.$inject = ['GenericMetadataModel'];

})();