(function() {
  'use strict';

  /* Factory module which exposes methods for making restful calls */
  
  angular
    .module('app.restfulServices')
          .factory('EntityRestFactory', EntityRestFactory);

  EntityRestFactory.$inject = ['RestfulService'];

  function EntityRestFactory(RestfulService) {
	var resource = '';
	var entity_name = '';
	
    var service = {
      setBaseParameters: setBaseParameters,
      loadEntity: loadEntity,
      saveOrUpdateEntity: saveOrUpdateEntity,
      retireOrUnretireEntity: retireOrUnretireEntity,
      purgeEntity: purgeEntity,
    }
    
    return service;
    
    /* Set base parameters: the resource name e.g 'inventory' and entity_name e.g 'institution' */
    function setBaseParameters(_resource, _entity_name){
    	resource = _resource;
    	entity_name = _entity_name;
    	var baseUrl = "/openmrs/ws/rest/v2/{0}/{1}/".format(resource, entity_name);
    	RestfulService.setBaseUrl(baseUrl);
    }
    
    /* Retrieve only one entity */
    function loadEntity(uuid, successCallback, errorCallback){
    	 RestfulService.one(entity_name, uuid, '', successCallback, errorCallback); 
    }
    
    /* Check if an entity exists! */
    function checkExistingEntity(name, successCallback, errorCallback){
    	var params = {
    		includeAll : true,
    		q : name,
    		startIndex : 1,
    		limit : 1
    	};
    	RestfulService.all(entity_name, params, successCallback, errorCallback);
    }
    
    /* Either persist a new entity or update an existing one */
    function saveOrUpdateEntity(request, successCallback, errorCallback){
    	var uuid = request['uuid'];
    	var name = request['name'];
    	delete request['uuid'];
    	delete request['name'];
        if(!angular.isDefined(uuid) || uuid === ""){
        	checkExistingEntity(name, function(data){
	        		if(data.length > 0){
	        			var msg = "openhmis." + resource + "." + entity_name + ".error.duplicate";
	    	        	emr.errorMessage(emr.message(msg));
	    	        	errorCallback(emr.message(msg));
	        		}
	        		else{
	    	        	RestfulService.saveOrUpdate(entity_name, uuid, request, successCallback, errorCallback);
	    	        }
        		}, function(error){});
        }
        else{
        	RestfulService.saveOrUpdate(entity_name, uuid, request, successCallback, errorCallback);
        }
    }
    
    /* Checks retired attribute and makes the appropriate call */
    function retireOrUnretireEntity(request, successCallback, errorCallback){
    	var retired = request['retired'];
    	var uuid = request['uuid'];
    	
    	delete request['retired'];
    	delete request['uuid'];
       	if(!retired){
            RestfulService.remove(entity_name, uuid, request, successCallback, errorCallback);
       	}
       	else{
       		RestfulService.saveOrUpdate(entity_name, uuid, request, successCallback, errorCallback);
       	}
    }
        
    /* Delete an entity */
    function purgeEntity(request, successCallback, errorCallback){
    	var uuid = request['uuid'];
    	delete request['uuid'];
        RestfulService.remove(resource, uuid, request, successCallback, errorCallback);
      }
    }
})();