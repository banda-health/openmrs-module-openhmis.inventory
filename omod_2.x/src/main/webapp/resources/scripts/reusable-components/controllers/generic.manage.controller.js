(function() {
	'use strict';

	var baseController = angular.module('app.genericManageController');
	
	function GenericManageController($scope, ManageEntityRestFactory, PaginationService, CssStylesFactory) {
		
		var self = this;
		
		self.resource = '';
		self.entity_name = '';
		
		// protected
		self.getResourceAndEntityName = self.getResourceAndEntityName || function(){
			console.log('generic get entity name and url');
		}

		// protected
		self.bindBaseParameters = function(resource, entity_name){
			self.resource = resource;
			self.entity_name = entity_name;
		}
		
		// public
		self.paginate = function(start){
			var params = PaginationService.paginateParams(start, $scope.limit, $scope.includeRetired, $scope.searchByName);
			self.loadEntities(params, self.onLoadEntitiesSuccess, self.onLoadEntitiesError);
		}
		
		// protected
		self.loadEntities = function(params, onLoadEntitiesSuccess, onLoadEntitiesError){
			params['entity_name'] = self.entity_name;
			ManageEntityRestFactory.loadEntities(params, self.onLoadEntitiesSuccess, self.onLoadEntitiesError);
		}
		
		// protected
		self.onLoadEntitiesSuccess = self.onLoadEntitiesSuccess || function(data){
			console.log('generic callback success..');
		}
		
		// protected
		self.onLoadEntitiesError = self.onLoadEntitiesError || function(error){
			console.log('generic callback error')
		}
		
		// protected
		self.computeNumberOfPages = function(totalNumOfResults){
			var numberOfPages = PaginationService.computeNumberOfPages(totalNumOfResults, $scope.limit);
			$scope.totalNumOfResults = totalNumOfResults;
			$scope.numberOfPages = numberOfPages;
		}
		
		// protected
		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope || function(){
			console.log('generic bind extra variables to scope');
		}
		
		// public
		self.reloadPage = function(){
			self.paginate($scope.currentPage);
		}
		
		// protected
		self.loadPage = function(){
			// define and/or instantiate variables
			self.initialize();
			//load 1st page..
			self.paginate($scope.currentPage);
		}
		
		// public
		// navigate to entity page
		self.loadEntityPage = function(url) {
			window.location = url;
		}
		
		// protected
		self.initialize =  function() {
			// initialize restful webservice..
			self.getResourceAndEntityName();
			ManageEntityRestFactory.setBaseUrl(self.resource);
			
			if(!angular.isDefined($scope.searchByName)){
				$scope.searchByName = '';	
			}
			
			if(!angular.isDefined($scope.currentPage)){
				$scope.currentPage = 1;
			}
			
			if(!angular.isDefined($scope.limit)){
				$scope.limit = 10;
			}
			if(!angular.isDefined($scope.numberOfPages)){
				$scope.numberOfPages = 0;
			}
			if(!angular.isDefined($scope.totalNumOfResults)){
				$scope.totalNumOfResults = 0;
			}
			
			if(!angular.isDefined($scope.includeRetired)){
				$scope.includeRetired = false;
			}
			
			$scope.reloadPage = self.reloadPage;
			$scope.loadEntityPage = self.loadEntityPage;
		    $scope.paginate = self.paginate;
		    
		    $scope.pagingFrom = PaginationService.pagingFrom;
			$scope.pagingTo = PaginationService.pagingTo;
			$scope.strikeThrough = CssStylesFactory.strikeThrough;
			
			self.bindExtraVariablesToScope();
		}
		
		//load page..
		self.loadPage();
	}
	
	baseController.GenericManageController = GenericManageController;
})();
