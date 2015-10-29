(function() {
	'use strict';

	var baseController = angular.module('app.genericManageController');
	
	function GenericManageController($scope) {
		
		/* This method must be implemented by the inheriting class/module */
		this.paginate = this.paginate || function(start, limit){
			console.log('generic paginate...');
		}
		
		this.bindExtraVariablesToScope = this.bindExtraVariablesToScope || function(){
			console.log('bind and/or override variables');
		}
		
		this.reloadPage = this.reloadPage || function(){
			console.log('reload page..');
			this.paginate($scope.currentPage, $scope.limit);
		}
		
		this.loadPage = this.loadPage || function(){
			console.log('load page..');
			//load 1st page..
			this.paginate($scope.currentPage, $scope.limit);
			//bind required functions
			this.initialize();
		}
		
		this.loadEntityPage = this.loadEntityPage || function(url) {
			console.log('load entity page..');
			window.location = url;
		}
		
		this.initialize =  function() {
			console.log('initialize..');
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
			
			$scope.reloadPage = this.reloadPage;
			$scope.loadEntityPage = this.loadEntityPage;
		    $scope.paginate = this.paginate;
		    this.bindExtraVariablesToScope();
		}
		
		//load page..
		this.loadPage();
	}
	
	baseController.GenericManageController = GenericManageController;
})();
