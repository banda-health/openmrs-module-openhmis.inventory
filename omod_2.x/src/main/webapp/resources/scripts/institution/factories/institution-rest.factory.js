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
      checkExistingInstitution: checkExistingInstitution,
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
    	 RestfulService.one('institution', uuid, '', successCallback, errorCallback); 
    }
    
    /* Checks for an existing institution. 
     * It's very expensive fetching all records and checking for matching names. 
     * A search by name service should be exposed. */
    function checkExistingInstitution(name){
    	var uuid;
    	var params = [];
    	params["includeAll"] = true;
    	var request = { "name": name };
    	RestfulService.all('institution', uuid, params, 
    			function(data){
    				for(var i = 0; i < data.results.length; i++){
    					if(data.results[i].name.toLowerCase() === name.toLowerCase()){
    						return true;
    					}
    				}
    			}, 
    			function(error){
    				//return false;
    			});
    	
    	return false;
    }
    
    function saveInstitution(_institution, successCallback, errorCallback){
    	var uuid = _institution.uuid;
        var name = _institution.name;
        var description = _institution.description;
          
        var request = {"name": name, "description": description};

        RestfulService.save('institution', uuid, request, successCallback, errorCallback);
    }
        
    function updateInstitution(_institution, successCallback, errorCallback){
       	var uuid = _institution.uuid;
        var name = _institution.name;
        var description = _institution.description;
            
        var request = {"name": name, "description": description};
       	
        RestfulService.update('institution', uuid, request, successCallback, errorCallback);
     }
        
    function retireInstitution(_institution, successCallback, errorCallback){
       	var uuid = _institution.uuid;
       	var retireReason = _institution.retireReason;
            
        var request = {"reason": retireReason};
        
        RestfulService.remove('institution', uuid, request, successCallback, errorCallback);
    }
        
    function unretireInstitution(_institution, successCallback, errorCallback){
        var uuid = _institution.uuid;
        var retired = false;
           
        var request = {"retired": retired};
            
        RestfulService.save('institution', uuid, request, successCallback, errorCallback);
    }
    
    /*
     * Remove institution.
     */
    function purgeInstitution(_institution, successCallback, errorCallback){
       	var uuid = _institution.uuid;
       	var purge = _institution.purge;
            
        var request = {"purge": purge};
            
        RestfulService.remove('institution', uuid, request, successCallback, errorCallback);
      }
    }
})();