(function() {
  'use strict';

  /* Factory module which exposes methods for making restful calls */
  
  angular
    .module('app.restfulServices')
          .factory('ManageInstitutionRestFactory', ManageInstitutionRestFactory);

  ManageInstitutionRestFactory.$inject = ['RestfulService'];

  function ManageInstitutionRestFactory(RestfulService) {
	 
	var baseUrl = "../ws/rest/v2/inventory/";
	
    var service = {
      loadInstitutions: loadInstitutions,
      includeRetiredInstitutions: includeRetiredInstitutions,
    }
    
    initialize();
    
    return service;
    
    // set ws url
    function initialize(){
    	RestfulService.setBaseUrl(baseUrl);
    }
    
    function loadInstitutions(requestParams, successCallback, errorCallback){
    	console.log("load institutions: request params = " + requestParams);
    	RestfulService.all('institution', requestParams, successCallback, errorCallback);
    }
    
    function includeRetiredInstitutions(params, successCallback, errorCallback) {
    	console.log('includeRetiredInstitutions: params = ' + params);
    	
    	RestfulService.all('institution', params, successCallback, errorCallback);
      }
  }

})();