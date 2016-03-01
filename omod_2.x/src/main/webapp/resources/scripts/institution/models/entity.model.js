(function() {
	'use strict';

	var baseModel = angular.module('app.genericMetadataModel');

	/* Define model fields */
	function InstitutionModel(GenericMetadataModel) {

		var extended = angular.extend(GenericMetadataModel, {});

		return extended;
	}

	baseModel.factory("InstitutionModel", InstitutionModel);

	InstitutionModel.$inject = ['GenericMetadataModel'];

})();
