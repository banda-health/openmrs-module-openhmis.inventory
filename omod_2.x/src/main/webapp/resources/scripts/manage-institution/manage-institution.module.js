(function() {
'use strict';
angular
    .module('manageInstitutionApp', ['app.restfulServices', 'app.css', 'app.models', 'angularUtils.directives.dirPagination'])
    .config(function(paginationTemplateProvider){
    	paginationTemplateProvider.setPath('/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/lib/dirPagination.tpl.html');
    });
})();
