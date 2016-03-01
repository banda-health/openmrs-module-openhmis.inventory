(function() {
    'use strict';

    var baseModel = angular.module('app.genericMetadataModel');

    /* Define model fields */
    function StockroomModel(GenericMetadataModel) {

        var extended = angular.extend(GenericMetadataModel, {});

        var defaultFields = extended.getModelFields();

        // @Override
        extended.getModelFields = function() {
            var fields = ["location"];
            return fields.concat(defaultFields);
        };

        return extended;
    }

    baseModel.factory("StockroomModel", StockroomModel);

    StockroomModel.$inject = ['GenericMetadataModel'];

})();
