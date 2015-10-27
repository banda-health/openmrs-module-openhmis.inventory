require(
    [
     	'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/lib/restangular.min.js',
     	'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/institution.module.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/models/models.module.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/reusable-components.module.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/restful-services/restful-services.module.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/models/institution.model.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/restful-services/restful-settings.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/reusable-components/restful-services/restful-service.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/factories/institution-rest.factory.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/directives/institution-disable.directive.js',
        '/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/controllers/institution.controller.js'
    ],
    function () {
        angular.bootstrap(document, ['institutionApp']);
    });



