(function() {
	'use strict';

	var baseController = angular.module('app.genericManageController');

	function GenericManageController($scope, $filter, ManageEntityRestFactory, PaginationService, CssStylesFactory, GenericMetadataModel) {

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
			var params = PaginationService.paginateParams(start, $scope.limit, $scope.includeRetired, $scope.searchByName);
			self.loadEntities(params, self.onLoadEntitiesSuccess, self.onLoadEntitiesError);
		}

		// protected
		self.loadEntities = function(params, onLoadEntitiesSuccess, onLoadEntitiesError) {
			params['entity_name'] = self.entity_rest_name;
			ManageEntityRestFactory.loadEntities(params, self.onLoadEntitiesSuccess, self.onLoadEntitiesError);
		}

		// protected
		self.onLoadEntitiesSuccess = self.onLoadEntitiesSuccess || function(data) {
			$scope.fetchedEntities = GenericMetadataModel.populateModels(data.results);
			self.computeNumberOfPages(data.length);
		}

		// protected
		self.onLoadEntitiesError = self.onLoadEntitiesError || function(error) {
			console.error(error);
			emr.errorMessage(error);
		}

		// protected
		self.computeNumberOfPages = function(totalNumOfResults) {
			var numberOfPages = PaginationService.computeNumberOfPages(totalNumOfResults, $scope.limit);
			$scope.totalNumOfResults = totalNumOfResults;
			$scope.numberOfPages = numberOfPages;
		}

		// protected
		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope || function() {
			// console.log('generic bind extra variables to scope');
		}

		// public
		self.updateContent = function() {
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

			if (!angular.isDefined($scope.currentPage)) {
				$scope.currentPage = 1;
			}

			if (!angular.isDefined($scope.limit)) {
				$scope.limit = 10;
			}
			if (!angular.isDefined($scope.numberOfPages)) {
				$scope.numberOfPages = 0;
			}
			if (!angular.isDefined($scope.totalNumOfResults)) {
				$scope.totalNumOfResults = 0;
			}

			if (!angular.isDefined($scope.includeRetired)) {
				$scope.includeRetired = false;
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
