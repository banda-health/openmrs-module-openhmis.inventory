(function() {
  'use strict';

  /* Factory module which exposes methods for making restful calls */
  
  angular
    .module('app.restfulServices')
          .factory('InstitutionRestFactory', InstitutionRestFactory);

  InstitutionRestFactory.$inject = ['RestfulService'];

  function InstitutionRestFactory(RestfulService) {
	 
	var baseUrl = "/openmrs/ws/rest/v2/inventory/";
	
    var service = {
      loadInstitution: loadInstitution,
      saveOrUpdateInstitution: saveOrUpdateInstitution,
      retireOrUnretireInstitution: retireOrUnretireInstitution,
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
    
    function checkExistingInstitution(name, successCallback, errorCallback){
    	console.log('check existing institution..');
    	var params = {
    		includeAll : true,
    		q : name,
    		startIndex : 1,
    		limit : 1
    	};
    	console.log(params);
    	RestfulService.all('institution', params, successCallback, errorCallback);
    }
    
    function saveOrUpdateInstitution(_institution, successCallback, errorCallback){
    	var uuid = _institution.uuid;
        var name = _institution.name;
        var description = _institution.description;
          
        var request = {"name": name, "description": description};
        
        if(!angular.isDefined(uuid) || uuid === ""){
        	checkExistingInstitution(name, function(data){
	        		if(data.length > 0){
	    	        	emr.errorMessage(emr.message("openhmis.inventory.institution.error.duplicate"));
	    	        	successCallback(emr.message("openhmis.inventory.institution.error.duplicate"));
	        		}
	        		else{
	    	        	RestfulService.saveOrUpdate('institution', uuid, request, successCallback, errorCallback);
	    	        }
        		}, function(error){});
        }
        else{
        	RestfulService.saveOrUpdate('institution', uuid, request, successCallback, errorCallback);
        }
    }
        
    function retireOrUnretireInstitution(_institution, successCallback, errorCallback){
    	var request;
    	var uuid = _institution.uuid;
       	var retireReason = _institution.retireReason;
       	var retired = _institution.retired;
       	
       	if(!retired){
            request = {"reason": retireReason};
            RestfulService.remove('institution', uuid, request, successCallback, errorCallback);
       	}
       	else{
       		request = {"retired": false};
       		RestfulService.saveOrUpdate('institution', uuid, request, successCallback, errorCallback);
       	}
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