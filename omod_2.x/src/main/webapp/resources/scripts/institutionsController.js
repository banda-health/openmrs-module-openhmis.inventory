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
        }, function(responseError) {
        	setInstitutionProperties(emr, $scope, "", "", "", false, "");
            emr.errorMessage("Requested institution was not found!");////TODO after emr upgrades, use emr.message("openhmis.inventory.institution.error.notFound");
        });
    }
    $scope.save = function() {
    	InstitutionService.save({"name" : $scope.institution.name, "description" : $scope.institution.description}, function(data) {
            $scope.loadInstitution(data.uuid);
            emr.successMessage("Successfully Created Institution"/*TODO after emr upgrades, use emr.message("openhmis.inventory.institution.created.success")*/);
        });
    }
    $scope.update = function() {
    	InstitutionService.save({"name" : $scope.institution.name, "description" : $scope.institution.description}, function(data) {
            $scope.loadInstitution(data.uuid);
            emr.successMessage("Successfully Updated Institution"/*TODO after emr upgrades, use emr.message("openhmis.inventory.institution.updated.success")*/);
        });
    }
    $scope.retire = function() {
    	InstitutionService.remove({"uuid" : $scope.institution.uuid, "reason" : $scope.institution.retireReason}, function(data) {
            $scope.loadInstitution(data.uuid);
            emr.successMessage("Successfully retired Institution"/*TODO after emr upgrades, use emr.message("openhmis.inventory.institution.retired.success")*/);
        });
    }
    $scope.unretire = function() {
    	InstitutionService.save({"uuid" : $scope.institution.uuid, "retired" : false },
	    	function(data) {
	            	$scope.loadInstitution(data.uuid);
	            	emr.successMessage("Successfully un-retired Institution"/*TODO after emr upgrades, use emr.message("openhmis.inventory.institution.unretired.success")*/);
	        });
    }
    $scope.purge = function() {//TODO instead of deleting all, it's retiring
    	if(confirm("Do you intentionally intend to delete this Institution?")) {//TODO after emr upgrades, use emr.message("openhmis.inventory.institution.confirm.delete")
	    	InstitutionService.remove({ "uuid" : $scope.institution.uuid, purge: "" }, function() {
	    			window.location = "manageInstitutions.page";
	    			emr.successMessage("Successfully deleted Institution"/*TODO after emr upgrades, use emr.message("openhmis.inventory.institution.deleted.success")*/);
	    	});
    	}
    }
    $scope.saveOrUpdate = function() {
    	if ($scope.institution.name === "") {
        	$scope.nameIsRequiredMsg = "Name is required";//TODO after emr upgrades, use emr.message("openhmis.inventory.institution.name.required");
        } else {
        	$scope.nameIsRequiredMsg = "";
	        if ($scope.institution.uuid === "" || $scope.institution.uuid === undefined) {//TODO check if name is at the server
	            $scope.save();
	        } else {
	            $scope.update();
	        }
        }
    }
    $scope.cancel = function() {
    	window.location = "manageInstitutions.page";
    }
    $scope.retireOrUnretire = function() {
        if ($scope.institution.retired === true) {
        	$scope.unretire();
        } else {
        	if($scope.institution.retireReason === "") {
        		$scope.retireReasonIsRequiredMsg = "Retire reason is required";//TODO after emr upgrades, use emr.message("openhmis.inventory.institution.retireReason.required");
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
    scope.institution.retired = retired;
    scope.institution.retireReason = retireReason;
    if (uuid === null || uuid === undefined || uuid === "") {
        scope.h2SubString = "New";//TODO after emr upgrades, use emr.message("general.new");
    } else {
    	scope.h2SubString = "Edit";//TODO after emr upgrades, use emr.message("general.edit");
    }
    if (scope.institution.retired === true) {
    	scope.retireOrUnretire = "Un-retire";//TODO after emr upgrades, use emr.message("openhmis.inventory.institution.unretire");
    } else {
    	scope.retireOrUnretire = "Retire";//TODO after emr upgrades, use emr.message("openhmis.inventory.institution.retire");
    }
}