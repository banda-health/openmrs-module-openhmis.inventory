(function() {
	'use strict';

	angular.module('app.restfulServices').service('RestfulSettingsService',
			RestfulSettingsService);

	RestfulSettingsService.$inject = [];

	function RestfulSettingsService() {
		var service;

		var url;

		service = {
				getUrl: getUrl,
				setUrl: setUrl,
				url: url
		};
		
		return service;

		function getUrl() {
			return url;
		}

		function setUrl(restUrl) {
			url = restUrl;
		}

		function getUrl() {
			return url;
		}

	}
})();
