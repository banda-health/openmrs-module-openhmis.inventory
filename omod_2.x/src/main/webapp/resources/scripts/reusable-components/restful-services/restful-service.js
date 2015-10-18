(function() {
	'use strict';

	angular.module('app.restfulServices').service('RestfulService',
			RestfulService);

	RestfulService.$inject = [ 'Restangular' ];

	function RestfulService(Restangular) {
		var service;

		service = {
			setBaseUrl : setBaseUrl,
			get : get,
			post : post,
			all : all,
			customGET : customGET,
			customPOST : customPOST
		};

		return service;

		function setBaseUrl(restWsUrl) {
			if (!angular.isUndefined(restWsUrl)) {
				Restangular.setBaseUrl(restWsUrl);
			}
		}

		function get(resource, request, successCallback, errorCallback) {
			console.log('get: resource url = ' + resource + ' | request = '
					+ request);
			Restangular.one(resource).get(request).then(function(data) {
				if (typeof successCallback === 'function')
					successCallback(data);
			}, function(error) {
				if (typeof errorCallback === 'function')
					errorCallback(error);
			});
		}

		function post(resource, request, successCallback, errorCallback) {
			console.log('post: resource url = ' + resource + ' | request = '
					+ request);
			Restangular.service(resource).post(request).then(function(data) {
				if (typeof successCallback === 'function')
					successCallback(data);
			}, function(error) {
				if (typeof errorCallback === 'function')
					errorCallback(error);
			});
		}

		/*
		 * Using Restangular.all(resource).getList() requires the response to be
		 * an ARRAY. This is NOT always the case, therefore customGET has been used
		 * instead.
		 */
		function all(resource, request, successCallback, errorCallback) {
			Restangular.all(resource).customGET(request).then(function(data) {
				if (typeof successCallback === 'function')
					successCallback(data);
			}, function(error) {
				if (typeof errorCallback === 'function')
					errorCallback(error);
			});
		}

		function customGET(resource, uuid, request, successCallback,
				errorCallback) {
			var params = '';
			if(angular.isDefined(request)){
				params = JSON.stringify(request);
			}
			console.log('customGET: resource url = ' + resource
					+ ' | request = ' + params);
			Restangular.one(resource, uuid).customGET('/', params).then(
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
			console.log('customPOST: resource url = ' + resource
					+ ' | request = ' + params);
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
