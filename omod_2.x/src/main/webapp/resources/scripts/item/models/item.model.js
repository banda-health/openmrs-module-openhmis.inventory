(function() {
    'use strict';

    var baseModel = angular.module('app.genericMetadataModel');

    /* Define model fields */
    function ItemModel(GenericMetadataModel) {

        var extended = angular.extend(GenericMetadataModel, {});

        var defaultFields = extended.getModelFields();

        // @Override
        extended.getModelFields = function() {
            var fields = ["buyingPrice", "codes", "concept", "defaultExpirationPeriod", "defaultPrice",
            "department", "description", "hasExpiration", "hasPhysicalInventory", "minimumQuantity", "prices"];
            return fields.concat(defaultFields);
        };

        return extended;
    }

    baseModel.factory("ItemModel", ItemModel);

    ItemModel.$inject = ['GenericMetadataModel'];

})();
