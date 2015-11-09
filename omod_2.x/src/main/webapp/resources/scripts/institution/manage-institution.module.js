(function() {
'use strict';
console.log('manage institutions..');
angular
    .module('manageInstitutionApp', ['app.genericEntityController', 'app.genericManageController', 'app.restfulServices', 'app.css', 'app.genericMetadataModel', 'angularUtils.directives.dirPagination', 'app.pagination', 'app.filters']);
})();
