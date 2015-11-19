(function() {
  'use strict';

  /* Factory module which exposes entity CRUD methods for making restful calls. */

  angular.module('app.restfulServices').factory('EntityRestFactory', EntityRestFactory);

  EntityRestFactory.$inject = ['RestfulService'];

  function EntityRestFactory(RestfulService) {

    var service = {
      setBaseUrl: setBaseUrl,
      loadEntity: loadEntity,
      saveOrUpdateEntity: saveOrUpdateEntity,
      retireOrUnretireEntity: retireOrUnretireEntity,
      purgeEntity: purgeEntity,
      loadEntities: loadEntities
    }

    return service;

    /* Set base url */
    function setBaseUrl(resource) {
      var baseUrl = "/openmrs/ws/rest/v2/" + resource + "/";
      RestfulService.setBaseUrl(baseUrl);
    }

    /* Required parameters: entity_name and uuid */
    function loadEntity(requestParams, successCallback, errorCallback) {
      var rest_entity_name;
      var uuid;

      if ("rest_entity_name" in requestParams) {
        rest_entity_name = requestParams['rest_entity_name'];
      } else {
        var msg = emr.message('openhmis.general.error.restName');
        commonErrorHandler(errorCallback, emr.message(msg));
      }

      if ("uuid" in requestParams) {
        uuid = requestParams['uuid'];
      } else {
        var msg = emr.message('openhmis.general.error.uuid');
        commonErrorHandler(errorCallback, emr.message(msg));
      }

      RestfulService.one(rest_entity_name, uuid, '', successCallback, errorCallback);
    }

    /* Checks for duplicated names */
    function checkExistingEntity(rest_entity_name, search_query, successCallback, errorCallback) {
      var params = {
        includeAll: true,
        q: search_query,
        startIndex: 1,
        limit: 1
      };
      RestfulService.all(rest_entity_name, params, successCallback, errorCallback);
    }

    /*
     * Either persist a new entity or update an existing one Required params:
     * name, rest_entity_name, uuid
     */
    function saveOrUpdateEntity(requestParams, successCallback, errorCallback) {
      var rest_entity_name;
      var uuid;
      var name;

      if ("rest_entity_name" in requestParams) {
        rest_entity_name = requestParams['rest_entity_name'];
      } else {
        var msg = emr.message('openhmis.general.error.restName');
        commonErrorHandler(errorCallback, emr.message(msg));
      }

      if ("uuid" in requestParams) {
        uuid = requestParams['uuid'];
      } else {
        var msg = emr.message('openhmis.general.error.uuid');
        commonErrorHandler(errorCallback, emr.message(msg));
      }

      if ("name" in requestParams) {
        name = requestParams['name'];
      } else {
        var msg = emr.message('openhmis.general.error.entityName');
        commonErrorHandler(errorCallback, emr.message(msg));
      }

      delete requestParams['rest_entity_name'];
      delete requestParams['uuid'];

      if (!angular.isDefined(uuid) || uuid === "") {
        checkExistingEntity(rest_entity_name, name, function(data) {
          if (data.results.length > 0) {
            var msg = emr.message("openhmis.general.error.duplicate");
            commonErrorHandler(errorCallback, emr.message(msg));
          } else {
            RestfulService.saveOrUpdate(rest_entity_name, '', requestParams, successCallback, errorCallback);
          }
        }, function(error) {
        });
      } else {
        RestfulService.saveOrUpdate(rest_entity_name, uuid, requestParams, successCallback, errorCallback);
      }
    }

    /* Required attributes: entity_name, uuid, retired, retireReason */
    function retireOrUnretireEntity(requestParams, successCallback, errorCallback) {
      var rest_entity_name;
      var retired;
      var uuid;

      if ("rest_entity_name" in requestParams) {
        rest_entity_name = requestParams['rest_entity_name'];
      } else {
        var msg = 'openhmis.general.error.restName'
        errorCallback(emr.message(msg));
      }

      if ("uuid" in requestParams) {
        uuid = requestParams['uuid'];
      } else {
        var msg = emr.message('openhmis.general.error.uuid');
        commonErrorHandler(errorCallback, emr.message(msg));
      }

      if ("retired" in requestParams) {
        retired = requestParams['retired'];
      } else {
        var msg = emr.message('openhmis.general.error.retired');
        commonErrorHandler(errorCallback, emr.message(msg));
      }

      delete requestParams['rest_entity_name'];
      delete requestParams['uuid'];

      if (!retired) {
        delete requestParams['retired'];
        RestfulService.remove(rest_entity_name, uuid, requestParams, successCallback, errorCallback);
      } else {
        requestParams['retired'] = false;
        RestfulService.saveOrUpdate(rest_entity_name, uuid, requestParams, successCallback, errorCallback);
      }
    }

    /* Delete an entity. Required params: entity_name, uuid, purge */
    function purgeEntity(requestParams, successCallback, errorCallback) {
      var rest_entity_name;
      var uuid;

      if ("rest_entity_name" in requestParams) {
        rest_entity_name = requestParams['rest_entity_name'];
      } else {
        var msg = 'openhmis.general.error.restName'
        errorCallback(emr.message(msg));
      }

      if ("uuid" in requestParams) {
        uuid = requestParams['uuid'];
      } else {
        var msg = emr.message('openhmis.general.error.uuid');
        commonErrorHandler(errorCallback, emr.message(msg));
      }

      delete requestParams['rest_entity_name'];
      delete requestParams['uuid'];

      RestfulService.remove(rest_entity_name, uuid, requestParams, successCallback, errorCallback);
    }

    function loadEntities(requestParams, successCallback, errorCallback) {
      var rest_entity_name;
      if ("rest_entity_name" in requestParams) {
        rest_entity_name = requestParams['rest_entity_name'];
      } else {
        var msg = 'openhmis.general.error.restName'
        errorCallback(emr.message(msg));
      }

      delete requestParams['rest_entity_name'];

      RestfulService.all(rest_entity_name, requestParams, successCallback, errorCallback);
    }

    function commonErrorHandler(errorCallback, msg) {
      console.log(msg);
      errorCallback(emr.message(msg));
    }
  }
})();