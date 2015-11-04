(function() {
	'use strict';

	/* Factory module which exposes entity CRUD methods for making restful calls. */

	angular.module('app.restfulServices').factory('EntityRestFactory', EntityRestFactory);

	EntityRestFactory.$inject = [ 'RestfulService' ];

	function EntityRestFactory(RestfulService) {

		var service = {
			setBaseUrl : setBaseUrl,
			loadEntity : loadEntity,
			saveOrUpdateEntity : saveOrUpdateEntity,
			retireOrUnretireEntity : retireOrUnretireEntity,
			purgeEntity : purgeEntity,
		}

		return service;

		/* Set base url */
		function setBaseUrl(resource) {
			var baseUrl = "/openmrs/ws/rest/v2/" + resource + "/";
			RestfulService.setBaseUrl(baseUrl);
		}

		/* Required parameters: entity_name and uuid */
		function loadEntity(requestParams, successCallback, errorCallback) {
			if ('entity_name' in requestParams) {
				var entity_name = requestParams['entity_name'];

				var uuid = requestParams['uuid'];

				RestfulService.one(entity_name, uuid, '', successCallback, errorCallback);
			} else {
				var msg = 'openhmis.general.error.entityName'
				errorCallback(emr.message(msg));
			}
		}

		/* Checks for duplicated names */
		function checkExistingEntity(entity_name, search_query, successCallback, errorCallback) {
			var params = {
				includeAll : true,
				q : search_query,
				startIndex : 1,
				limit : 1
			};
			RestfulService.all(entity_name, params, successCallback, errorCallback);
		}

		/*
		 * Either persist a new entity or update an existing one Required params:
		 * resource name, entity_name, uuid, name and/or description
		 */
		function saveOrUpdateEntity(requestParams, successCallback, errorCallback) {
			if ("entity_name" in requestParams) {
				var entity_name = requestParams['entity_name'];
				var uuid = requestParams['uuid'];
				var name = requestParams['name'];
				var resource = requestParams['resource'];

				delete requestParams['entity_name'];
				delete requestParams['uuid'];
				delete requestParams['resource'];

				if (!angular.isDefined(uuid) || uuid === "") {
					checkExistingEntity(entity_name, name, function(data) {
						if (data.results.length > 0) {
							var msg = "openhmis.general.error.duplicate";
							emr.errorMessage(emr.message(msg));
						} else {
							RestfulService.saveOrUpdate(entity_name, '', requestParams, successCallback, errorCallback);
						}
					}, function(error) {
					});
				} else {
					RestfulService.saveOrUpdate(entity_name, uuid, requestParams, successCallback, errorCallback);
				}
			} else {
				var msg = 'openhmis.general.error.entityName'
				emr.errorMessage(emr.message(msg));
			}
		}

		/* Required attributes: entity_name, uuid, retired, retireReason */
		function retireOrUnretireEntity(requestParams, successCallback, errorCallback) {
			if ("entity_name" in requestParams) {
				var entity_name = requestParams['entity_name'];
				var retired = requestParams['retired'];
				var uuid = requestParams['uuid'];

				delete requestParams['entity_name'];
				delete requestParams['uuid'];

				if (!retired) {
					delete requestParams['retired'];
					RestfulService.remove(entity_name, uuid, requestParams, successCallback, errorCallback);
				} else {
					requestParams['retired'] = false;
					RestfulService.saveOrUpdate(entity_name, uuid, requestParams, successCallback, errorCallback);
				}
			} else {
				var msg = 'openhmis.general.error.entityName'
				errorCallback(emr.message(msg));
			}
		}

		/* Delete an entity. Required params: entity_name, uuid, purge */
		function purgeEntity(requestParams, successCallback, errorCallback) {
			if ("entity_name" in requestParams) {
				var entity_name = requestParams['entity_name'];
				var uuid = requestParams['uuid'];

				delete requestParams['entity_name'];
				// delete requestParams['uuid'];

				RestfulService.remove(entity_name, uuid, requestParams, successCallback, errorCallback);
			} else {
				var msg = 'openhmis.general.error.entityName'
				errorCallback(emr.message(msg));
			}
		}
	}
})();