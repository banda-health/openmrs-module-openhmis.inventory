var departmentsRestUrl = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/ws/rest/v2/inventory/department";
var departmentUrl = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/openhmis.inventory/department.page";
var departmentApp = angular.module('departmentsApp', []);

/*
 * Defines the Angular Application/Module and controller for Departments used in OpenMRS 2.x
 */
departmentApp.controller('departmentsController', function($scope, $http) {
    $http.get(departmentsRestUrl)
        .success(function(response) {
            $scope.includeRetired = false;
            $scope.currentPage = 0;
            $scope.initialDepartments = response.results;
            var departments = [];
            
            for(i = 0; i < $scope.initialDepartments.length; i++) {
            	if(!$scope.includeRetired && $scope.initialDepartments[i].retired === false) {
            		departments.push($scope.initialDepartments[i]);
            	}
            }
            $scope.departments = departments.length > 0 ? departments : $scope.initialDepartments;
            $scope.initialDepartments = $scope.departments;
            $scope.length = $scope.departments.length;
            $scope.loadDepartment = function(uuid) {
            	window.location = departmentUrl + "?uuid=" + uuid;
            };
            $scope.numberOfPages=function(){
                return Math.ceil($scope.departments.length/10);                
            };
            $scope.pagingFrom = function() {
            	return $scope.currentPage <= 0 ? 0 : ($scope.currentPage) * 10;
            };
            $scope.pagingTo = function() {
            	return $scope.currentPage <= 0 ? 10 : ($scope.currentPage + 1) * 10;
            };
            $scope.includeRetiredDepartments = function() {//TODO not yet working; $scope.initialDepartments doesn't contain retired Departments
            	if($scope.includeRetired) {
            		$scope.departments = $scope.initialDepartments;
            	}
            };
        });
});

//initialize a new startFrom filter
departmentApp.filter('startFrom', function() {
  return function(input, start) {
      start = +start; //parse to int
      return input.slice(start);
  }
});
