/* load all required modules.. */
require(
    [
     	// load required libs
     	'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/lib/restangular.min.js',
     	'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/lib/dirPagination.js',
     	
     	/* models */
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/models/models.module.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/models/institution.model.js',
        
        /* reusable components */
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/reusable-components.module.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/restful-services/restful-services.module.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/css/css.module.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/restful-services/restful-settings.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/restful-services/restful-service.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/css/css-styles.js',
        
        /* manage institution components (controllers/filters etc) */
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/manage-institution/manage-institution.module.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/manage-institution/factories/manage-institution-rest.factory.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/manage-institution/controllers/manage-institution.controller.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/manage-institution/filters/start-from.filter.js',
    ],
    function () {
        angular.bootstrap(document, ['manageInstitutionApp']);
    });
