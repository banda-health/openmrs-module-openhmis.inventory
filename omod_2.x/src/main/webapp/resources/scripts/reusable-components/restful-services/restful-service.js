(function() {
	'use strict';

	angular.module('app.restfulServices').service('RestfulService',
			RestfulService);

	RestfulService.$inject = [ 'Restangular' ];

	function RestfulService(Restangular) {
		var service;

		service = {
			setBaseUrl : setBaseUrl,
			all : all,
			one : one,
			remove: remove,
			saveOrUpdate: saveOrUpdate
		};

		return service;

		function setBaseUrl(restWsUrl) {
			if (!angular.isUndefined(restWsUrl)) {
				Restangular.setBaseUrl(restWsUrl);
			}
		}
		
		/*
		 * Retrieve a List of Objects:
		 * Note: Using Restangular.all(resource).getList() requires the response to be
		 * an ARRAY. This is NOT always the case, therefore customGET has been used
		 * instead.
		 */
		function all(resource, request, successCallback, errorCallback) {
			Restangular.all(resource).customGET('', request).then(function(data) {
				if (typeof successCallback === 'function'){
					successCallback(data);
				}
			}, function(error) {
				if (typeof errorCallback === 'function')
					errorCallback(error);
			});
		}

		/*
		 * Retrieve ONLY one result.
		 */
		function one(resource, uuid, request, successCallback,
				errorCallback) {
			var params = '';
			if(angular.isDefined(request)){
				params = JSON.stringify(request);
			}

			Restangular.one(resource, uuid).customGET('/', params).then(
					function(data) {
						if (typeof successCallback === 'function')
							successCallback(data);
					}, function(error) {
						if (typeof errorCallback === 'function')
							errorCallback(error);
					});
		}

		function saveOrUpdate(resource, uuid, request, successCallback,
				errorCallback) {
			customPOST(resource, uuid, request, successCallback, errorCallback);
		}
		
		function remove(resource, uuid, request, successCallback,
				errorCallback) {
			var params = JSON.stringify(request);

			Restangular.one(resource, uuid).remove(params).then(
					function(data) {
						if (typeof successCallback === 'function')
							successCallback(data);
					}, function(error) {
						if (typeof errorCallback === 'function')
							errorCallback(error);
					});
		}
		
		function customPOST(resource, uuid, request, successCallback,
				errorCallback) {
			var params = JSON.stringify(request);

			Restangular.one(resource, uuid).customPOST(params).then(
					function(data) {
						if (typeof successCallback === 'function')
							successCallback(data);
					}, function(error) {
						if (typeof errorCallback === 'function')
							errorCallback(error);
					});
		}
	}
})();
