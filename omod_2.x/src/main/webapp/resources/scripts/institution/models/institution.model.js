(function() {
	'use strict';

	angular.module('app.models').factory('InstitutionModel', function() {
		
		function InstitutionModel(uuid, name, description, retireReason, purge, retired) {
			this.uuid = uuid;
			this.name = name;
			this.description = description;
			this.retireReason = retireReason;
			this.purge = purge;
			this.retired = retired;
		}
				
		InstitutionModel.populateModels = function(data){
			var institutions = [];
			
			for(var i = 0; i < data.length; i++){
				var uuid = data[i].uuid;
				var name = data[i].name;
				var description = data[i].description;
				var retired = data[i].retired;
				
				//uuid, name, description, retireReason, purge, retired
				var institution = new InstitutionModel(uuid, name, description, '', '', retired);
		    	
			    institutions.push(institution);
			}
				
			return institutions;
		};
			
		InstitutionModel.populateModel = function(data){
			
			var institution;
			
			if(angular.isDefined(data)){
				
				var uuid = data.uuid;
				var name = data.name;
				var description = data.description;
				var retired = data.retired;
				var retireReason = data.retireReason;
				
				//uuid, name, description, retireReason, purge, retired
				institution = new InstitutionModel(uuid, name, description, retireReason, '', retired);
			}
			    	
			return institution;
		};
			
		InstitutionModel.newModelInstance = function(){
			return new InstitutionModel('', '', '', '', '', false);
		};
		
		return InstitutionModel;
	});
})();
