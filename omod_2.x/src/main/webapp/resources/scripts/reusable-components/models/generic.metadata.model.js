(function() {
	'use strict';

	var baseModel = angular.module('app.genericMetadataModel');

	function GenericMetadataModel(GenericObjectModel) {
		
		var extended = angular.extend(GenericObjectModel, {});

		/* Default fields */
		extended.getModelFields = function() {
	        return ["uuid"];
	    };
				
		return extended;
	}
	
	baseModel.factory("GenericMetadataModel", GenericMetadataModel);
	GenericMetadataModel.$inject = ['GenericObjectModel'];
})();
