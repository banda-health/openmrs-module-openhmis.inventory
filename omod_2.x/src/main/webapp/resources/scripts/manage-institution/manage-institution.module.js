(function() {
'use strict';
angular
    .module('manageInstitutionApp', ['app.genericManageController', 'app.restfulServices', 'app.css', 'app.genericMetadataModel', 'angularUtils.directives.dirPagination', 'app.pagination'])
    .config(function(paginationTemplateProvider){
    	paginationTemplateProvider.setPath('/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/lib/dirPagination.tpl.html');
    });
})();
