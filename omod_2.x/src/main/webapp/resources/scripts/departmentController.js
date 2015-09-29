var departmentsRestUrl = "../ws/rest/v2/inventory/department";
var manageDepartmentsUrl = "department.page";

/*
 * Defines the Angular Application/Module and controller for either an existing or empty department used in OpenMRS 2.x
 */
angular.module('departmentApp', []).controller('departmentController', function($scope, $http) {
	var uuid = window.location.search.split("=")[1];//search looks like; '?uuid=09404'
    var departmentRestUrl = departmentsRestUrl + (uuid === undefined ? "" :  "/" + uuid);
    
    $scope.name = "";
    $scope.description = "";
    $scope.uuid = "";
    $scope.retired = false;
    $scope.h2SubString = "New";
    $scope.thisIsANewBill = true;
    $scope.retireReason = "";
    $scope.retireOrUnretire = "Retire";
    $scope.nameIsRequiredMsg = "";
    $scope.retireReasonIsRequiredMsg = "";
    $scope.successfulMsg = "";
    $scope.canNotRetire = function() {
    	if($scope.retireReason !== "" && $scope.retireReason !== undefined && $scope.retireReason !== null) {
    		$scope.retireReasonIsRequiredMsg = "";
    		return false;
    	} else {
    		return true;
    	}
    };

    $scope.cancel = function() {
    	window.location = "manageDepartments.page";
    };
    $scope.save = function() {
    	if($scope.name === null || $scope.name === undefined || $scope.name === "") {
    		$scope.nameIsRequiredMsg = "Name is Required";
    	} else {
    		persistDepartment($scope, $http, departmentsRestUrl, "save");
    	}
    };
    $scope.retire = function() {
    	if($scope.retireReason === null || $scope.retireReason === undefined || $scope.retireReason === "") {
    		$scope.retireReasonIsRequiredMsg = "Retire Reason is Required";
    	} else {
    		$scope.retireReasonIsRequiredMsg = "";
    		if($scope.retireOrUnretire === "Retire") {
    			$scope.retired = true;
    		} else {
    			$scope.retired = false;
    		}
    		persistDepartment($scope, $http, departmentsRestUrl, "retire");
    	}
    };
    $scope.purge = function() {
    	persistDepartment($scope, $http, departmentsRestUrl, "purge");
    };
    
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
		            scope.h2SubString = "Edit";
		            scope.thisIsANewBill = false;
		            scope.retireReason = response.retireReason;
		            if(scope.retired === true) {
		            	scope.retireOrUnretire = "Un-Retire";
		            }
		        });
	    }
}

function persistDepartment(scope, http, departmentsRestUrl, action) {
	var department = {"name" : scope.name, "description": scope.description};
	var message = "Successfully ";
	
	scope.successfulMsg = "";
	if(action === "save") {
		scope.nameIsRequiredMsg = "";
		
		if(!checkIfDepartmentExists(scope, departmentsRestUrl, http)) {
			http.post(scope.uuid === "" ? departmentsRestUrl : departmentsRestUrl + "/" + scope.uuid, department).success(function(savedDepartment) {
				message += "Saved Department";
				
				initializeDepartment(departmentsRestUrl + "/" + savedDepartment.uuid, departmentsRestUrl, http, scope);
				scope.successfulMsg = message;
		    });
		} else {
			
		}
	} else if(action === "retire") {
		scope.retireReasonIsRequiredMsg = "";
		http.delete(departmentsRestUrl + "/" + scope.uuid + "?!purge", {"retireReason" : scope.retireReason}).success(function(retiredDepartment) {
			message += scope.retireOrUnretire + "d Department";
			
			initializeDepartment(departmentsRestUrl + "/" + retiredDepartment.uuid, departmentsRestUrl, http, scope);
			scope.successfulMsg = message;
	    });
	} else if(action === "purge") {
		if(confirm("Do you surely want to delete: " + scope.name + " ?")) {
			http.delete(departmentsRestUrl + "/" + scope.uuid + "?purge").success(function(response) {
				window.location = "manageDepartments.page";
		    });
		}
	}
}

function checkIfDepartmentExists(scope, departmentsRestUrl) {
	var exists = true;
	
	if(scope.thisIsANewBill) {
		var names = [];
		
		http({
	        url : departmentsRestUrl,
	        method : 'GET',
	        async: false,
	        dataType : "json",
	        headers : {'Content-Type': 'application/json'}
        }).success(function(departments) {
			for(i = 0; i < departments.length; i++) {
				names.push(departments[i].name);
			}
		});
		if(names.indexOf(scope.name) === -1) {
			exists = false;
		}
	}
	return exists;
}
