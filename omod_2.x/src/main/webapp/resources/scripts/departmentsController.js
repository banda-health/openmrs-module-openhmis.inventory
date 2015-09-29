var departmentsRestUrl = "../ws/rest/v2/inventory/department";
var departmentUrl = "department.page";
var departmentsApp = angular.module('departmentsApp', []);

/*
 * Defines the Angular Application/Module and controller for Departments used in OpenMRS 2.x
 */
departmentsApp.controller('departmentsController', function($scope, $http) {
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

/*Adds a new a-disabled directive to povide the same functionality as ng-disabled for anchors/links(<a>)*/
departmentsApp.directive('aDisabled', function() {
    return {
        compile: function(tElement, tAttrs, transclude) {
            //Disable ngClick
            tAttrs["ngClick"] = "!(" + tAttrs["aDisabled"] + ") && (" + tAttrs["ngClick"] + ")";

            //return a link function
            return function (scope, iElement, iAttrs) {

                //Toggle "disabled" to class when aDisabled becomes true
                scope.$watch(iAttrs["aDisabled"], function(newValue) {
                    if (newValue !== undefined) {
                        iElement.toggleClass("disabled", newValue);
                    }
                });

                //Disable href on click
                iElement.on("click", function(e) {
                    if (scope.$eval(iAttrs["aDisabled"])) {
                        e.preventDefault();
                    }
                });
            };
        }
    };
});

//initialize a new startFrom filter
departmentsApp.filter('startFrom', function() {
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
    	return scopeObj.currentPage <= 0 ? 1 : (scopeObj.currentPage) * 10;//TODO must the default number to be paged = 10 be available for change by the user?
    };
    scopeObj.pagingTo = function() {
    	return scopeObj.currentPage <= 0 ? 10 : (scopeObj.currentPage + 1) * 10;
    };
    scopeObj.existingPages = function() {
    	var pages = [];
    	
    	for(i = 1; i <= scopeObj.numberOfPages(); i++) {
    		pages.push(i);
    	}
    	return pages;
    };
    scopeObj.loadPageByNumber = function(page) {
    	scopeObj.currentPage = page - 1;
    };
    scopeObj.disAbleSinglePage = function(page) {
    	if(page === scopeObj.currentPage + 1 || (page === scopeObj.currentPage + 1 && (page - 1 === 0 || page === scopeObj.numberOfPages()))) {
    		return true;
    	} else {
    		return false;
    	}
    }
}
