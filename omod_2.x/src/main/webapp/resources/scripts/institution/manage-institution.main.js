/* load all required modules.. */
requirejs(['/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/common.js'], 
		function(){
			requirejs([
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/models/institution.model.js',
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/restful-services/manage-entity-rest.factory.js',
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/pagination/pagination.module.js',
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/pagination/pagination.service.js',
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/controllers/generic.manage.controller.module.js',
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/controllers/generic.manage.controller.js',
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/manage-institution.module.js',
				'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/controllers/manage-institution.controller.js',
			], function () {
		        angular.bootstrap(document, ['manageInstitutionApp']);
		    });
		}
);
