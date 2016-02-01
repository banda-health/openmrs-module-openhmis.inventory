(function (){
	'use strict'

	angular.module('app.restfulServices').service('ItemRestfulService', OperationTypesRestfulService);

	OperationTypesRestfulService.$inject = ['EntityRestFactory'];

	function OperationTypesRestfulService(EntityRestFactory) {
		var service;

		service = {
			loadUsers: loadUsers
		};

		return service;

		/**
		 * Temporary Function: It will ONLY be used until the Department module is done.
		 * @param limit
		 * @param onLoadUsersSuccessful
		 */
		function loadUsers(limit, onLoadUsersSuccessful) {
			var requestParams = [];
			requestParams['rest_entity_name'] = 'user';
			requestParams['limit'] = limit;
			EntityRestFactory.setBaseUrl('concept', 'v1');
			EntityRestFactory.loadEntities(requestParams,
					onLoadUsersSuccessful,
					function(error){
						console.log(error);
					}
			);
		}
	}

})();
