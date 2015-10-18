(function() {
	  'use strict';

	  angular
	    .module('manageInstitutionApp')
	     	.filter('startFrom', function() {
			    return function(input, start) {
			        if (!input || !input.length) {
			            return;
			        }
			        start = +start; //parse to int
			        return input.slice(start);
			    }
	     	});
})();