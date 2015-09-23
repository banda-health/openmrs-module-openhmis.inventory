var departmentsRestUrl = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/ws/rest/v2/inventory/department";
var departmentUrl = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/openhmis.inventory/department.page";
var departmentApp = angular.module('departmentsApp', []);

/*
 * Defines the Angular Application/Module and controller for Departments used in OpenMRS 2.x
 */
departmentApp.controller('departmentsController', function($scope, $http) {
    $http.get(departmentsRestUrl)
        .success(function(response) {
            $scope.departments = response.results;
            $scope.length = response.length;
            $scope.currentPage = 0;
            $scope.numberToShow = "5";
            $scope.loadDepartment = function(uuid) {
            	window.location = departmentUrl + "?uuid=" + uuid;
            };
            $scope.numberOfPages=function(){
                return Math.ceil($scope.departments.length/parseInt($scope.numberToShow));                
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
