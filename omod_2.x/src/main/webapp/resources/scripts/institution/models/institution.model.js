(function() {
	'use strict';

	var baseModel = angular.module('app.genericModel'); 
	
	function InstitutionModel(GenericModel) {
		
		var extended = angular.extend(GenericModel, {});

		// @Override
		extended.openmrsModel = function() {
	        var fields =  ["uuid", "name", "description", "retireReason", "purge", "retired"];
	        return fields;
	    };
				
		return extended;
	}
	
	baseModel.factory("InstitutionModel", InstitutionModel);
	
	InstitutionModel.$inject = ['GenericModel'];
	
})();
