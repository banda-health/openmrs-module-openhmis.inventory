/* load common modules.. */
requirejs([ '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/common.js' ], function() {
	requirejs([
	      
	      '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/models/institution.model.js',
	  		'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/restful-services/manage-entity-rest.factory.js',
	  		'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/pagination/pagination.module.js',
	  		'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/pagination/pagination.service.js',
	  		'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/controllers/generic.manage.controller.module.js',
	  		'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/controllers/generic.manage.controller.js',
	  		'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/manage-institution.module.js',
	  		'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/controllers/manage-institution.controller.js',
	  		
	  		'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/restful-services/entity-rest.factory.js',
	  		'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/controllers/generic.entity.controller.module.js',
	  		'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/controllers/generic.entity.controller.js',
	  		'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/institution.module.js',
	  		
	  		'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/institutions.module.js',
	  		'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/controllers/institution.controller.js',
	      
	  ], function() {
			requirejs(['/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/lib/domReady.js!'], function (domReady) {
				console.log('dom ready..');
				angular.bootstrap(domReady, [ 'institutionsApp' ]);
			});
	});
});
