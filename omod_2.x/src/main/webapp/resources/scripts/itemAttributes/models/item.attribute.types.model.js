
(function() {
    'use strict';

    var baseModel = angular.module('app.genericMetadataModel');

    /* Define model fields */
    function ItemAttributeTypesModel(GenericMetadataModel) {

        var extended = angular.extend(GenericMetadataModel, {});

        var defaultFields = extended.getModelFields();

        // @Override
        extended.getModelFields = function() {
            var fields = ["attributeOrder", "description", "foreignKey", "format", "regExp", "required"];
            return fields.concat(defaultFields);
        };

        return extended;
    }

    baseModel.factory("ItemAttributeTypesModel", ItemAttributeTypesModel);

    ItemAttributeTypesModel.$inject = ['GenericMetadataModel'];

})();
