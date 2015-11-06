(function() {
	'use strict';

	var baseModel = angular.module('app.genericMetadataModel');

	/* Define model fields */
	function PaginateModel(GenericMetadataModel) {

		var extended = angular.extend(GenericMetadataModel, {});

		var defaultFields = extended.getModelFields();
		
		// @Override
		extended.getModelFields = function() {
			var fields = [ "totalNumOfResults", "numberOfPages", "entities" ];
			return fields.concat(defaultFields);
		};
		
		extended.getTotalNumOfResults = function(){
			return extended.totalNumOfResults;
		};
		
		extended.setTotalNumOfResults = function(totalNumOfResults){
			extended.totalNumOfResults = totalNumOfResults;
		};
		
		extended.getNumberOfPages = function(){
			return extended.numberOfPages;
		};
		
		extended.setNumberOfPages = function(numberOfPages){
			extended.numberOfPages = numberOfPages;
		};
		
		extended.setEntities = function(entities){
			extended.entities = entities;
		}

		extended.getEntities = function(){
			return extended.entities;
		};
		
		return extended;
	}

	baseModel.factory("PaginateModel", PaginateModel);

	PaginateModel.$inject = [ 'GenericMetadataModel' ];

})();
