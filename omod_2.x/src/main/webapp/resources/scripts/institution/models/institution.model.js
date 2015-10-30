(function() {
	'use strict';

	var baseModel = angular.module('app.genericMetadataModel'); 
	
	/* Define model fields */
	function InstitutionModel(GenericMetadataModel) {
		
		var extended = angular.extend(GenericMetadataModel, {});

		var defaultFields = extended.getModelFields();
		
		// @Override
		extended.getModelFields = function() {
	        var fields =  ["name", "description", "retireReason", "purge", "retired"];
	        return fields.concat(defaultFields);
	    };
				
		return extended;
	}
	
	baseModel.factory("InstitutionModel", InstitutionModel);
	
	InstitutionModel.$inject = ['GenericMetadataModel'];
	
})();
