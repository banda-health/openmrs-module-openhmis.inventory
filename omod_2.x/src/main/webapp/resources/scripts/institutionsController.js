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
            },
            query: {
            	method: 'GET',
            	isArray: false
            }
        });
    }
]).controller('InstitutionController', function($scope, InstitutionService) {
    var institutionService = new InstitutionService();
	
    var institutions = InstitutionService.query(function(response) {
    	$scope.fetchedInstitutions = response.results;
    	initialize($scope, response, false);
    });
    
    $scope.loadInstitutionFromManagePage = function(uuid) {
    	window.location = "institution.page?uuid=" + uuid;
    }
    
    $scope.includeRetiredInstitutions = function() {
    	if($scope.includeRetired) {
    		InstitutionService.query({"includeAll" : true}, function(resp) {
    			initialize($scope, resp, true);
            });
    	} else {
    		InstitutionService.query(function(resp) {
    			initialize($scope, resp, false);
            });
    	}
    }
    
    $scope.strikeThrough = function(retired) {
    	if(retired) {
    		 return { "text-decoration" : "line-through" };
    	} else {
    		return {};
    	}
    }
    
	$scope.loadInstitution = function(uuid) {
    	institutionService.$get({uuid: uuid}, function(institution) {
        	setInstitutionProperties(emr, $scope, institution.uuid, institution.name, institution.description, institution.retired, institution.retireReason);
        }, function(responseError) {
        	setInstitutionProperties(emr, $scope, "", "", "", false, "");
            emr.errorMessage("Requested institution was not found!");//TODO after emr upgrades, use emr.message("openhmis.inventory.institution.error.notFound");
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
    	InstitutionService.remove({"uuid" : $scope.institution.uuid, "reason" : $scope.institution.retireReason}, function() {
            $scope.loadInstitution($scope.institution.uuid);
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
    	if ($scope.institution.name === undefined || $scope.institution.name === "") {
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
    $scope.retireOrUnretireFunction = function() {
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
    scope.institution.retireReason = retired ? retireReason : "";
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

/*Adds a new a-disabled directive to povide the same functionality as ng-disabled for anchors/links(<a>)*/
institutionApp.directive('aDisabled', function() {
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
institutionApp.filter('startFrom', function() {
  return function(input, start) {
      if (!input || !input.length) { return; }
      start = +start; //parse to int
      return input.slice(start);
  }
});

function initialize(scopeObj, response, includeRetired) {
	scopeObj.includeRetired = includeRetired;
    scopeObj.currentPage = 0;
    scopeObj.fetchedInstitutions = response.results;
    scopeObj.length = scopeObj.fetchedInstitutions.length;
    scopeObj.loadInstitutionFromManagePage = function(uuid) {
    	window.location = "institution.page?uuid=" + uuid;
    }
    scopeObj.numberOfPages = function(){
        return Math.ceil(scopeObj.fetchedInstitutions.length/10);                
    }
    scopeObj.pagingFrom = function() {
    	return scopeObj.currentPage <= 0 ? 1 : (scopeObj.currentPage) * 10;//TODO must the default number to be paged = 10 be available for change by the user?
    }
    scopeObj.pagingTo = function() {
    	return scopeObj.currentPage <= 0 ? 10 : (scopeObj.currentPage + 1) * 10;
    }
    scopeObj.existingPages = function() {
    	var pages = [];
    	
    	for(i = 1; i <= scopeObj.numberOfPages(); i++) {
    		pages.push(i);
    	}
    	return pages;
    }
    scopeObj.loadPageByNumber = function(page) {
    	scopeObj.currentPage = page - 1;
    }
    scopeObj.disAbleSinglePage = function(page) {
    	if(page === scopeObj.currentPage + 1 || (page === scopeObj.currentPage + 1 && (page - 1 === 0 || page === scopeObj.numberOfPages()))) {
    		return true;
    	} else {
    		return false;
    	}
    }
}