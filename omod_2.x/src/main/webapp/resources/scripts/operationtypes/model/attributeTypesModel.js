(function() {
	'use strict';

	var baseModel = angular.module('app.genericMetadataModel');

	/* Define model fields */
	function AttributeTypesModel(GenericMetadataModel) {

		var extended = angular.extend(GenericMetadataModel, {});

		var defaultFields = extended.getModelFields();

		// @Override
		extended.getModelFields = function() {
			var fields = ["attributeOrder", "description", "foreignKey", "format", "regExp", "required"];
			return fields.concat(defaultFields);
		};

		return extended;
	}

	baseModel.factory("AttributeTypesModel", AttributeTypesModel);

	AttributeTypesModel.$inject = ['GenericMetadataModel'];

})();
