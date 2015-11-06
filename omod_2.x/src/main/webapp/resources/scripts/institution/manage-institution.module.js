(function() {
'use strict';
console.log('manage institutions..');
angular
    .module('manageInstitutionApp', ['app.genericEntityController', 'ui.router', 'app.genericManageController', 'app.restfulServices', 'app.css', 'app.genericMetadataModel', 'angularUtils.directives.dirPagination', 'app.pagination', 'app.filters'])
    .config(function(paginationTemplateProvider, $stateProvider){
    	
    	paginationTemplateProvider.setPath('/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/lib/dirPagination.tpl.html');
    	
    });
})();
