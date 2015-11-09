(function() {
	'use strict';

	var baseController = angular.module('app.genericManageController');

	function GenericManageController($scope, $filter, ManageEntityRestFactory, PaginationService, CssStylesFactory, GenericMetadataModel, CookiesService) {

		var self = this;

		self.module_name = '';
		self.entity_rest_name = '';
		self.entity_name = '';

		// protected
		self.getModelAndEntityName = self.getModelAndEntityName || function() {
			console.log('generic get entity name and url');
		}

		// protected
		self.bindBaseParameters = function(module_name, entity_rest_name, entity_name) {
			self.module_name = module_name;
			self.entity_rest_name = entity_rest_name;
			self.entity_name = entity_name;
		}

		// public
		self.paginate = function(start) {
			CookiesService.set('currentPage', start);
			CookiesService.set('limit', $scope.limit);

			var params = PaginationService.paginateParams(CookiesService.get('currentPage'), $scope.limit, CookiesService.get('includeRetired'), $scope.searchByName);
			params['entity_name'] = self.entity_rest_name;
			PaginationService.paginate(params, self.onPaginateSuccess, self.onPaginateError);
		}

		// protected
		self.onPaginateSuccess = self.onPaginateSuccess || function(paginateModel) {
			$scope.fetchedEntities = paginateModel.getEntities();
			$scope.totalNumOfResults = paginateModel.getTotalNumOfResults();
			$scope.numberOfPages = paginateModel.getNumberOfPages();
		}

		// protected
		self.onPaginateError = self.onPaginateError || function(error) {
			console.error(error);
		}

		// protected
		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope || function() {
			// console.log('generic bind extra variables to scope');
		}

		// public
		self.updateContent = function() {
			CookiesService.set('includeRetired', $scope.includeRetired);
			self.paginate($scope.currentPage);
		}

		// protected
		self.loadPage = function() {
			// define and/or instantiate variables
			self.initialize();
			// load 1st page..
			self.paginate($scope.currentPage);
		}

		// public
		// navigate to entity page
		self.loadEntityPage = function(url) {
			window.location = url;
		}

		// protected
		self.initialize = function() {
			// initialize restful webservice..
			self.getModelAndEntityName();
			ManageEntityRestFactory.setBaseUrl(self.module_name);

			if (!angular.isDefined($scope.fetchedEntities)) {
				$scope.fetchedEntities = [];
			}

			if (!angular.isDefined($scope.searchByName)) {
				$scope.searchByName = '';
			}

			if (!angular.isDefined(CookiesService.get('currentPage')) || (CookiesService.get('currentPage') === "undefined")) {
				$scope.currentPage = 1;
			} else {
				$scope.currentPage = CookiesService.get('currentPage');
			}

			if (!angular.isDefined(CookiesService.get('limit')) || (CookiesService.get('limit') === "undefined")) {
				$scope.limit = 10;
			} else {
				$scope.limit = CookiesService.get('limit');
			}
			if (!angular.isDefined($scope.numberOfPages)) {
				$scope.numberOfPages = 0;
			}
			if (!angular.isDefined($scope.totalNumOfResults)) {
				$scope.totalNumOfResults = 0;
			}

			if (!angular.isDefined(CookiesService.get('includeRetired'))) {
				$scope.includeRetired = false;
				CookiesService.set('includeRetired', $scope.includeRetired);
			} else {
				$scope.includeRetired = (CookiesService.get('includeRetired') === 'true');
			}

			$scope.updateContent = self.updateContent;
			$scope.loadEntityPage = self.loadEntityPage;
			$scope.paginate = self.paginate;

			$scope.pagingFrom = PaginationService.pagingFrom;
			$scope.pagingTo = PaginationService.pagingTo;
			$scope.strikeThrough = CssStylesFactory.strikeThrough;

			self.bindExtraVariablesToScope();

			$scope.newEntityLabel = $filter('EmrFormat')(emr.message("openhmis.inventory.general.new"), [ self.entity_name ]);
		}

		// load page..
		self.loadPage();
	}
	baseController.GenericManageController = GenericManageController;
})();
