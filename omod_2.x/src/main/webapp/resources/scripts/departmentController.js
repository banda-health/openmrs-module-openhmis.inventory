var departmentsRestUrl = "../ws/rest/v2/inventory/department";
var manageDepartmentsUrl = "department.page";
var uuid = window.location.search.split("=")[0] === "?uuid" ? window.location.search.split("=")[1] : "";//search looks like; '?uuid=09404'

/*
 * Defines the Angular Application/Module and controller for either an existing or empty department used in OpenMRS 2.x
 */
angular.module('departmentApp', []).controller('departmentController', function($scope, $http) {
	var departmentRestUrl = departmentsRestUrl + (uuid === undefined ? "/" :  "/" + uuid);
    
    $scope.name = "";
    $scope.description = "";
    $scope.uuid = "";
    $scope.retired = false;
    $scope.h2SubString = (emr.message("general.new") === "general.new") ? "New" : emr.message("general.new");
    $scope.thisIsANewBill = true;
    $scope.retireReason = "";
    $scope.retireOrUnretire = ((emr.message("general.retire") === emr.message("general.retire")) ? "Retire" :emr.message("general.retire"));
    $scope.nameIsRequiredMsg = "";
    $scope.retireReasonIsRequiredMsg = "";
    $scope.successfulMsg = "";
    $scope.canNotRetire = function() {
    	if(($scope.retireReason !== "" && $scope.retireReason !== undefined && $scope.retireReason !== null) || !$scope.retired) {
    		$scope.retireReasonIsRequiredMsg = "";
    		return false;
    	} else {
    		return true;
    	}
    }

    $scope.cancel = function() {
    	window.location = "manageDepartments.page";
    };
    $scope.save = function() {
    	if($scope.name === null || $scope.name === undefined || $scope.name === "") {
    		$scope.nameIsRequiredMsg = emr.message("Module.error.nameCannotBeEmpty");
    	} else {
    		persistDepartment(emr, $scope, $http, departmentsRestUrl, "save");
    	}
    }
    
    $scope.retire = function() {
    	if(($scope.retireReason === null || $scope.retireReason === undefined || $scope.retireReason === "") && !$scope.retired) {
    		$scope.retireReasonIsRequiredMsg = emr.message("general.retiredReason.empty");
    	} else {
    		$scope.retireReasonIsRequiredMsg = "";
    		persistDepartment(emr, $scope, $http, departmentsRestUrl, "retire");
    	}
    }
    
    $scope.purge = function() {
    	persistDepartment(emr, $scope, $http, departmentsRestUrl, "purge");
    }
    
    initializeDepartment(departmentRestUrl, departmentsRestUrl, $http, $scope);
});

function initializeDepartment(departmentRestUrl, departmentsRestUrl, http, scope) {
	 if(departmentRestUrl !== departmentsRestUrl) {
		    http.get(departmentRestUrl)
		        .success(function(response) {
		        	scope.name = response.name;
		            scope.description = response.description;
		            scope.uuid = response.uuid;
		            scope.retired = response.retired;
		            scope.h2SubString = emr.message("general.edit");
		            scope.thisIsANewBill = false;
		            scope.retireReason = scope.retired ? "" : response.retireReason;
		            if(scope.retired === true) {
		            	scope.retireOrUnretire = (emr.message("general.unretire") === "general.unretire") ? "Un-Retire" : emr.message("general.unretire");
		            }
		        }).error(function (data, status){
		        	if(status === 404) {
		        		emr.errorMessage(emr.message("openhmis.inventory.depatment.error.notFound"));
		        	}
	            });
	    }
}

function persistDepartment(emr, scope, http, departmentsRestUrl, action) {
	var department = {"name" : scope.name, "description": scope.description};
	
	scope.successfulMsg = "";
	if(action === "save") {
		scope.nameIsRequiredMsg = "";
		
		if(scope.thisIsANewBill) {
			http.get(departmentsRestUrl).success(function(departments) {
				var names = [];
				
				for(i = 0; i < departments.length; i++) {
					names.push(departments.results[i].name);
				}
				if(names.indexOf(scope.name) === -1) {//save new department only if it doesn't exist
					saveDepartment(emr, http, scope, departmentsRestUrl, department, departmentsRestUrl);
					scope.successfulMsg = emr.message("openhmis.inventory.department.success.saved");
					emr.successMessage(scope.successfulMsg);
				}
			});
		} else {
			saveDepartment(emr, http, scope, departmentsRestUrl + scope.uuid, department, departmentsRestUrl)
			scope.successfulMsg = emr.message("openhmis.inventory.department.success.updated");
			emr.successMessage(scope.successfulMsg);
		}
	} else if(action === "retire") {
		scope.retireReasonIsRequiredMsg = "";
		if(!scope.retired) {
			http.delete(departmentsRestUrl + "/" + scope.uuid, {"uuid": scope.uuid,"reason" : scope.retireReason}).success(function(response) {
				initializeDepartment(departmentsRestUrl + "/" + scope.uuid, departmentsRestUrl, http, scope);
				scope.successfulMsg = emr.message("openhmis.inventory.department.success.retired");
				emr.successMessage(scope.successfulMsg);
			});
		} else {
			saveDepartment(emr, http, scope, departmentsRestUrl + "/" + scope.uuid, {"uuid" : scope.uuid, "retired" : false}, departmentsRestUrl);
			scope.successfulMsg = emr.message("openhmis.inventory.department.success.unretired");
			emr.successMessage(scope.successfulMsg);
		}
	} else if(action === "purge") {
		if(confirm(emr.message("openhmis.inventory.department.confirm.delete"))) {
			http.delete(departmentsRestUrl + "/" + scope.uuid + "?purge").success(function(response) {
				window.location = "manageDepartments.page";
		    });
		}
	}
}

function saveDepartment(emr, http, scope, url, department, departmentsRestUrl) {
	http.post(url, department).success(function(savedDepartment) {
		initializeDepartment(departmentsRestUrl + "/" + savedDepartment.uuid, departmentsRestUrl, http, scope);
	});
}
