(function() {
	'use strict';

	angular.module('app.pagination').service('PaginationService', PaginationService);

	PaginationService.$inject = ['ManageEntityRestFactory', 'PaginateModel'];

	/*
	 * TODO: Write a method that accepts Generic rest factory and GenericMetaData
	 * as parameters and returns a re-usable pagination object
	 */
	function PaginationService() {
		var service;

		service = {
			pagingTo : pagingTo,
			pagingFrom : pagingFrom,
			paginateParams : paginateParams,
			computeNumberOfPages : computeNumberOfPages,
			paginate : paginate
		};

		return service;

		function pagingTo(currentPage, limit, totalNumOfResults) {
			if (currentPage <= 0) {
				return limit;
			} else {
				var num = currentPage * limit;
				if (num > totalNumOfResults) {
					return totalNumOfResults;
				}
				return num;
			}
		}

		function pagingFrom(currentPage, limit) {
			return currentPage <= 1 ? 1 : (currentPage - 1) * limit;
		}

		function paginateParams(start, limit, includeRetired, q) {
			var startIndex = ((start - 1) * limit) + 1;
			var params;

			if (includeRetired) {
				params = {
					limit : limit,
					includeAll : true,
					startIndex : startIndex
				};
			} else {
				params = {
					limit : limit,
					startIndex : startIndex
				};
			}

			if (!angular.isUndefined(q) && q !== '') {
				params['q'] = q;
			}

			return params;
		}

		function computeNumberOfPages(totalNumOfResults, limit) {
			return Math.ceil(totalNumOfResults / limit);
		}
		
		/*
		 * Fetch a list of paginated entities and return a paginate model.
		 */
		function paginate(params){
			var model = new PaginateModel();
			ManageEntityRestFactory.loadEntities(params, function(data){
				var entities = PaginateModel.populateModels(data.results);
				var pages = computeNumberOfPages(data.length);
				var totalResults = data.length;
				
				model.setEntities(entities);
				model.setTotalNumOfResults(totalResults);
				model.setNumberOfPages(pages);
				
				return model;
			}, function(error){
				console.log(error);
			});
		}
	}
})();
