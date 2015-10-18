(function() {
	'use strict';

	angular.module('app.models').value('InstitutionModel', InstitutionModel);

	function InstitutionModel() {
		
		var uuid;
		var name;
		var description;
		var retireReason;
		var retired = false;
		var purge = '';

		setUuid = function(value) {
			uuid = value;
		};

		setName = function(value) {
			name = value;
		};

		setDescription = function(value) {
			description = value;
		};

		setRetireReason = function(value) {
			retireReason = value;
		};

		setRetired = function(value) {
			retired = value;
		};

		setPurge = function(value) {
			purge = value
		};

		populateModels = function(data){
			var institutions = [];
			for(var i = 0; i < data.length; i++){
				var institution = new InstitutionModel();
				institution.setUuid(data[i].uuid);
			    institution.setName(data[i].name);
			    institution.setDescription(data[i].description);
			    institution.setRetired(data[i].retired);
			    	
			    institutions.push(institution);
			}
				
			return institutions;
		};
			
		populateModel = function(data){
			var institution = new InstitutionModel();
				
			if(angular.isDefined(data)){
				console.log('kset data...');
				console.log(data.uuid);
				console.log(data.name);
					
				institution.setUuid(data.uuid);
				institution.setName(data.name);
				institution.setDescription(data.description);
				institution.setRetired(data.retired);
				institution.setRetireReason(data.retireReason);
			}
			    	
			return institution;
		};
			
		newModelInstance = function(){
			return new InstitutionModel();
		};
	}
})();
