(function() {
  'use strict';

  /* Factory module which exposes methods for making restful calls */
  
  angular
    .module('app.restfulServices')
          .factory('ManageEntityRestFactory', ManageEntityRestFactory);

  ManageEntityRestFactory.$inject = ['RestfulService'];

  function ManageEntityRestFactory(RestfulService) {
	  
	var self = this;
	  
    var service = {
    		setBaseUrl: setBaseUrl,
      loadEntities: loadEntities
    }
    
    return service;
    
    /* Set base url */
    function setBaseUrl(resource){
    	var baseUrl = "/openmrs/ws/rest/v2/" + resource + "/";
    	RestfulService.setBaseUrl(baseUrl);
    }
    
    function loadEntities(requestParams, successCallback, errorCallback){
    	if('entity_name' in requestParams){
    		var entity_name = requestParams['entity_name']; 
    		delete requestParams['entity_name'];
    		
    		RestfulService.all(entity_name, requestParams, successCallback, errorCallback);
    	}
    	else{
    		var msg = 'openhmis.general.error.entityName'
        	errorCallback(emr.message(msg));
    	}
    }
  }
})();