var departmentsRestUrl = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/ws/rest/v2/inventory/department";
var departmentUrl = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/openhmis.inventory/department.page";
var departmentApp = angular.module('departmentsApp', []);

/*
 * Defines the Angular Application/Module and controller for Departments used in OpenMRS 2.x
 */
departmentApp.controller('departmentsController', function($scope, $http) {
    $http.get(departmentsRestUrl)
        .success(function(response) {
        	initialize($scope, response, departmentUrl, false);
            $scope.includeRetiredDepartments = function() {
            	if($scope.includeRetired) {
            		$http.get(departmentsRestUrl + "?includeAll=true").success(function(resp) {//returns retired departments as well
            			initialize($scope, resp, departmentUrl, true);
                    });
            	} else {
            		$http.get(departmentsRestUrl).success(function(resp) {
            			initialize($scope, resp, departmentUrl, false);
                    });
            	}
            };
        });
});

//initialize a new startFrom filter
departmentApp.filter('startFrom', function() {
  return function(input, start) {
      if (!input || !input.length) { return; }
      start = +start; //parse to int
      return input.slice(start);
  }
});

function initialize(scopeObj, response, departmentUrl, includeRetired) {
	scopeObj.includeRetired = includeRetired;
    scopeObj.currentPage = 0;
    scopeObj.departments = response.results;
    scopeObj.length = scopeObj.departments.length;
    scopeObj.loadDepartment = function(uuid) {
    	window.location = departmentUrl + "?uuid=" + uuid;
    };
    scopeObj.numberOfPages=function(){
        return Math.ceil(scopeObj.departments.length/10);                
    };
    scopeObj.pagingFrom = function() {
    	return scopeObj.currentPage <= 0 ? 0 : (scopeObj.currentPage) * 10;//TODO must the default number to be paged = 10 be available for change by the user?
    };
    scopeObj.pagingTo = function() {
    	return scopeObj.currentPage <= 0 ? 10 : (scopeObj.currentPage + 1) * 10;
    };
}
