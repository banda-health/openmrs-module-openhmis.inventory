/* load all required modules.. */
requirejs(['/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/common.js'], 
		function(){
			requirejs([
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/models/institution.model.js',
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/restful-services/entity-rest.factory.js',
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/controllers/generic.entity.controller.module.js',
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/controllers/generic.entity.controller.js',
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/institution.module.js',
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/controllers/institution.controller.js',
			], function () {
		        angular.bootstrap(document, ['institutionApp']);
		    });
		}
);



