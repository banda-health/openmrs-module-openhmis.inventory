(function() {
	'use strict';

	var baseModel = angular.module('app.genericModel');

	function GenericModel() {
		
		var self = this;
		
		/* default fields. override to implement own fields */
		self.openmrsModel = function() {
	        var fields =  ["uuid", "name", "description", "retireReason", "purge", "retired"];
	        return fields;
	    };
		
		self.populateModels = function(data){
			var entities = [];
			for(var i = 0; i < data.length; i++){
				var entity = self.populateModel(data[i]);
			    entities.push(entity);
			}
				
			return entities;
		};
			
		self.populateModel = function(data){
			var entity = {};
			if(angular.isDefined(data)){
				var fields = self.openmrsModel();
				for(var i = 0; i < fields.length; i++){
					var field = fields[i];
					if(angular.isDefined(data[field])){
						entity[field] = data[field];
					}
					else{
						entity[field] = '';
					}
				}
			}
			
			return entity;
		};
		
		self.newModelInstance = function(){
			var fields = self.openmrsModel();
			var entity = {};
			for(var i = 0; i < fields.length; i++){
				var field = fields[i];
				entity[field] = '';
			}
			return entity;
		};
		
		return self;
	}
	
	baseModel.factory("GenericModel", GenericModel);
})();
