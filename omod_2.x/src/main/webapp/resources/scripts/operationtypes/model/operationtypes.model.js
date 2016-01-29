(function() {
	'use strict';

	var baseModel = angular.module('app.genericMetadataModel');

	/* Define model fields */
	function OperationTypesModel(GenericMetadataModel) {

		var extended = angular.extend(GenericMetadataModel, {});

		return extended;
	}

	baseModel.factory("OperationTypesModel", OperationTypesModel);

	OperationTypesModel.$inject = ['GenericMetadataModel'];

})();
