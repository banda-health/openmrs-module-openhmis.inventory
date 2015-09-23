var departmentsRestUrl = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/ws/rest/v2/inventory/department";

/*
 * Defines the Angular Application/Module and controller for either an existing or empty department used in OpenMRS 2.x
 */
angular.module('departmentApp', []).controller('departmentController', function($scope, $http) {
	var uuid = window.location.search.split("=")[1];//search looks like; '?uuid=09404'
    var departmentRestUrl = departmentsRestUrl + "/" + (uuid === undefined ? "" : uuid);
    
    $scope.name = "";
    $scope.description = "";
    $scope.uuid = "";
    $scope.retired = false;
    
    if(departmentRestUrl !== departmentsRestUrl) {
	    $http.get(departmentRestUrl)
	        .success(function(response) {
	        	$scope.name = response.name;
	            $scope.description = response.description;
	            $scope.uuid = response.uuid;
	            $scope.retired = response.retired;
	        });
    }
});
