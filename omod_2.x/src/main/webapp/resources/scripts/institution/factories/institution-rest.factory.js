(function() {
  'use strict';

  /* Factory module which exposes methods for making restful calls */
  
  angular
    .module('app.restfulServices')
          .factory('InstitutionRestFactory', InstitutionRestFactory);

  InstitutionRestFactory.$inject = ['RestfulService'];

  function InstitutionRestFactory(RestfulService) {
	 
	var baseUrl = "../ws/rest/v2/inventory/";
	
    var service = {
      loadInstitution: loadInstitution,
      saveInstitution: saveInstitution,
      updateInstitution: updateInstitution,
      retireInstitution: retireInstitution,
      unretireInstitution: unretireInstitution,
      purgeInstitution: purgeInstitution,
    }
    
    initialize();
    
    return service;
    
    // set ws url
    function initialize(){
    	RestfulService.setBaseUrl(baseUrl);
    }
    
    function loadInstitution(uuid, successCallback, errorCallback){
    	 console.log('loadinstitution: uuid = ' + uuid);
    	 RestfulService.customGET('institution', uuid, '', successCallback, errorCallback); 
    }
    
    function saveInstitution(institution, successCallback, errorCallback){
    	var _institution = JSON.parse(institution);
    	var uuid = _institution.uuid;
        var name = _institution.name;
        var description = _institution.description;
          
        var request = {"name": name, "description": description};
        console.log('saveInstitution: request = ' + request);

        RestfulService.customPOST('institution', uuid, request, successCallback, errorCallback);
    }
        
    function updateInstitution(institution, successCallback, errorCallback){
       	var _institution = JSON.parse(institution);
       	var uuid = _institution.uuid;
        var name = _institution.name;
        var description = _institution.description;
            
        var request = {"name": name, "description": description};
        console.log('updateInstiution: request = ' + request);
       	
        RestfulService.customPOST('institution', uuid, request, successCallback, errorCallback);
     }
        
    function retireInstitution(institution, successCallback, errorCallback){
       	var _institution = JSON.parse(institution);
       	var uuid = _institution.uuid;
       	var reason = _institution.reason;
            
        var request = {"reason": reason};
        console.log('retireInstituion: request = ' + request);
            
        RestfulService.customPOST('institution', uuid, request, successCallback, errorCallback);
    }
        
    function unretireInstitution(institution, successCallback, errorCallback){
    	var _institution = JSON.parse(institution);
        var uuid = _institution.uuid;
        var retired = _institution.retired;
           
        var request = {"retired": retired};
        console.log('unretireInstitution: request = ' + request);
            
        RestfulService.customPOST('institution', uuid, request, successCallback, errorCallback);
    }
        
    function purgeInstitution(institution, successCallback, errorCallback){
       	var _institution = JSON.parse(institution);
       	var uuid = _institution.uuid;
       	var purge = _institution.purge;
            
        var request = {"purge": purge};
        console.log('purgeInstitution: request = ' + request);
            
        RestfulService.customPOST('institution', uuid, request, successCallback, errorCallback);
      }
    }
})();