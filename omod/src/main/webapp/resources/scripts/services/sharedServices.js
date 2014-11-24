angular.module('sharedServices', [])
	.service('httpService', ['$http', function($http) {
		return {
			purge: function(entitiyType, uuid) {
				var url = '/openmrs/ws/rest/v2/inventory/' + entitiyType+ '/' + uuid + '?purge=true';
				return $http.delete(url);
			},
			retire: function(entitiyType, uuid, reason) {
				var url = '/openmrs/ws/rest/v2/inventory/' + entitiyType+ '/' + uuid + '?reason=' + reason;
		    	return $http.delete(url);
			}
		};
	}]);