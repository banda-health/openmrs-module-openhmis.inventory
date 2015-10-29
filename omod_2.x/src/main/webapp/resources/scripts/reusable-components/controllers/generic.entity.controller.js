(function() {
	'use strict';

	var baseController = angular.module('app.genericController');

	function GenericEntityController($scope) {
		
		this.saveOrUpdate = this.saveOrUpdate || function(){
			console.log('generic save or update');
		}
		
		this.retireOrUnretireCall = this.retireOrUnretireCall || function(retire){
			console.log('generic retire or unretire');
		}
		
		this.purge = this.purge || function(){
			console.log('generic purge');
		}
		
		this.loadEntity = this.loadEntity || function(){
			console.log('generic load entity..');
		}
		
		this.cancel = this.cancel || function(){
			console.log('generic cancel');
		}
		
		this.bindExtraVariablesToScope = this.bindExtraVariablesToScope || function(uuid){
			console.log('generic bind extra variables to scope');
		}
		
		this.initialize = this.initialize || function(){
			$scope.cancel = this.cancel;
			$scope.purge = this.purge;
			$scope.saveOrUpdate = this.saveOrUpdate;
			$scope.retireOrUnretireCall = this.retireOrUnretireCall;
			
			this.bindExtraVariablesToScope('');
		}
		
		this.loadPage = this.loadPage || function(){
			this.loadEntity();
			this.initialize();
		}
		
		/* ENTRY POINT */
		this.loadPage();
	}
	
	baseController.GenericEntityController = GenericEntityController;
})();
