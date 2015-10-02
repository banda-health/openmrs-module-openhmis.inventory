/*Creates an institution application/module set to include $resource service*/
var institutionApp = angular.module('institutionApp', ['ngResource']);
var uuid = window.location.search.split("=")[0] === "?uuid" ? window.location.search.split("=")[1] : "";//search looks like; '?uuid=09404'

/*Creates institution service to use $resource*/
institutionApp.factory('InstitutionService', ['$resource',
    function($resource) {
        return $resource('../ws/rest/v2/inventory/institution/:uuid', {
            uuid: '@uuid'
        }, {
            update: {
                method: 'PUT'
            }
        });
    }
]).controller('InstitutionController', function($scope, InstitutionService) {
    var institutionService = new InstitutionService();
	
	$scope.loadInstitution = function(uuid) {
    	institutionService.$get({uuid: uuid}, function(institution) {
        	setInstitutionProperties(emr, $scope, institution.uuid, institution.name, institution.description, institution.retired, institution.retireReason);
        });
    }
    $scope.save = function() {
    	InstitutionService.save({"name" : $scope.institution.name, "description" : $scope.institution.description}, function(data) {
            $scope.loadInstitution(data.uuid);
        });
    }
    $scope.update = function() {
    	institutionService.$update({"name" : $scope.institution.name, "description" : $scope.institution.description}, function(data) {
            $scope.loadInstitution(data.uuid);
        });
    }
    $scope.retire = function() {
    	institutionService.$remove({"uuid" : $scope.institution.uuid, "reason" : $scope.institution.retireReason}, function(data) {
            $scope.loadInstitution(data.uuid);
        });
    }
    $scope.unretire = function() {
    	institutionService.$save({"uuid" : $scope.institution.uuid, "retired": false },
	    	function(data) {
	            	$scope.loadInstitution(data.uuid);
	        });
    }
    $scope.purge = function() {//TODO instead of deleting all, it's retiring
    	InstitutionService.delete({ "uuid" : $scope.institution.uuid }, function() {
    			window.location = "manageInstitutions.page";
    	});
    }
    $scope.saveOrUpdate = function() {
    	if ($scope.institution.name === "") {
        	$scope.nameIsRequiredMsg = emr.message("openhmis.inventory.institution.name.required");
        } else {
        	$scope.nameIsRequiredMsg = "";
	        if ($scope.institution.uuid === "" || $scope.institution.uuid === undefined) {//TODO check if name is at the server
	            $scope.save();
	        } else {
	            $scope.update();
	        }
        }
    }
    $scope.retireOrUnretire = function() {
        if ($scope.institution.retireReason === true) {
            $scope.unretire();
        } else {
        	if($scope.institution.retireReason === "") {
        		$scope.retireReasonIsRequiredMsg = emr.message("openhmis.inventory.institution.retireReason.required");
        	} else {
        		$scope.retireReasonIsRequiredMsg = "";
	            $scope.retire();
        	}
        }
    }
    
    if(uuid === undefined || uuid === null || uuid === "") {
    	setInstitutionProperties(emr, $scope, "", "", "", false, ""); //for create new institution
    } else {
    	$scope.loadInstitution(uuid);
    }
        
});

function setInstitutionProperties(emr, scope, uuid, name, description, retired, retireReason) {
	scope.institution = {};
	scope.institution.uuid = uuid;
    scope.institution.name = name;
    scope.institution.description = description;
    scope.institution.retireReason = retireReason;
    if (uuid === null || uuid === undefined || uuid === "") {
        scope.h2SubString = emr.message("general.new");
    } else {
        scope.h2SubString = emr.message("general.edit");
    }
}