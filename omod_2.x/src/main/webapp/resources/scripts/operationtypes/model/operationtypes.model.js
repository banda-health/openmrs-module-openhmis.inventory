(function() {
	'use strict';

	var baseModel = angular.module('app.genericMetadataModel');

	/* Define model fields */
	function OperationTypesModel(GenericMetadataModel) {

		var extended = angular.extend(GenericMetadataModel, {});
		var defaultFields = extended.getModelFields();

		// @Override
		extended.getModelFields = function() {
			var fields = ["hasSource", "hasDestination", "hasRecipient", "recipientRequired", "availableWhenReserved",
				"user", "role","attributeTypes"];
			return fields.concat(defaultFields);
		};

		return extended;
	}

	baseModel.factory("OperationTypesModel", OperationTypesModel);

	OperationTypesModel.$inject = ['GenericMetadataModel'];

})();
