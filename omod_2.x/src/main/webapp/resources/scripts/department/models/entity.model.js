(function() {
    'use strict';

    var baseModel = angular.module('app.genericMetadataModel');

    /* Define model fields */
    function DepartmentModel(GenericMetadataModel) {

        var extended = angular.extend(GenericMetadataModel, {});

        var defaultFields = extended.getModelFields();

        // @Override
        extended.getModelFields = function() {
            return defaultFields;
        };

        return extended;
    }

    baseModel.factory("DepartmentModel", DepartmentModel);

    DepartmentModel.$inject = ['GenericMetadataModel'];

})();
