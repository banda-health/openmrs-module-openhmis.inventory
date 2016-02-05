(function (){
	'use strict'

	angular.module('app.restfulServices').service('OperationTypesRestfulService', OperationTypesRestfulService);

	OperationTypesRestfulService.$inject = ['EntityRestFactory'];

	function OperationTypesRestfulService(EntityRestFactory) {
		var service;
		service = {
			loadUsers: loadUsers,
			loadRoles: loadRoles,
			loadFormatFields: loadFormatFields
		};

		return service;

		/**
		 * Temporary Function: It will ONLY be used until the Department module is done.
		 * @param limit
		 * @param onLoadUsersSuccessful
		 */
		function loadUsers(module_name,limit, onLoadUsersSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = '';
			requestParams['limit'] = limit;
			EntityRestFactory.setBaseUrl('user', 'v1');
			EntityRestFactory.loadEntities(requestParams,
					onLoadUsersSuccessful,
					function(error){
						console.log(error);
					}
			);
			//reset base url..
			EntityRestFactory.setBaseUrl(module_name);
		}

		function loadRoles(module_name,limit, onLoadRolesSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = '';
			requestParams['limit'] = limit;
			EntityRestFactory.setBaseUrl('role', 'v1');
			EntityRestFactory.loadEntities(requestParams,onLoadRolesSuccessful,
					function(error) {
						console.log(error);
					}
			);
			//reset base url..
			EntityRestFactory.setBaseUrl(module_name);
		}

		function loadFormatFields(module_name, onLoadFormatFieldsSuccessful) {
			var requestParams = [];
			requestParams['resource'] = 'fieldgenhandlers.json';
			EntityRestFactory.setCustomBaseUrl('/openmrs/');
			EntityRestFactory.loadResults(requestParams,
					onLoadFormatFieldsSuccessful,
					function(error){
						console.log(error);
					}
			);
			//reset base url..
			EntityRestFactory.setBaseUrl(module_name);
		}
	}

})();
