(function() {
	'use strict';

	var baseModel = angular.module('app.genericMetadataModel');

	function PaginateModel(GenericMetadataModel) {

		var extended = angular.extend(GenericMetadataModel, {});

		var defaultFields = extended.getModelFields();

		// @Override
		extended.getModelFields = function() {
			var fields = [ "totalNumOfResults", "numberOfPages", "entities" ];
			return fields.concat(defaultFields);
		};

		function Paginate(totalNumOfResults, numberOfPages, entities) {
			this.totalNumOfResults = totalNumOfResults;
			this.numberOfPages = numberOfPages;
			this.entities = entities;
		}

		Paginate.prototype = {

			getTotalNumOfResults : function() {
				return this.totalNumOfResults;
			},

			getNumberOfPages : function() {
				return this.numberOfPages;
			},

			getEntities : function() {
				return this.entities;
			}
		};

		Paginate.populateModels = extended.populateModels;
		
		return Paginate;
	}
	baseModel.factory("PaginateModel", PaginateModel);
	PaginateModel.$inject = [ 'GenericMetadataModel' ];
})();
