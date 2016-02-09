(function() {
	'use strict';

	var app = angular.module('app.operationsTypeFunctionsFactory', []);
	app.service('OperationsTypeFunctions', OperationsTypeFunctions);

	OperationsTypeFunctions.$inject = [];

	function OperationsTypeFunctions() {
		var service;
		service = {
			//removeAttributeTypes: removeAttributeTypes,
			addMessageLabels : addMessageLabels,
			addAttributeType : addAttributeType
		};

		return service;

		/**
		 * Displays a popup dialog box with the attribute types . Saves the code on clicking the 'Ok' button
		 * @param $scope
		 */
		function addAttributeType($scope){
			$scope.editAttributeTypeTitle = '';
			$scope.addAttributeTypeTitle = $scope.messageLabels['openhmis.inventory.general.add'] + ' ' +  $scope.messageLabels['openhmis.backboneforms.attribute.type.name'];
			console.log(addAttributeTypeTitle);
			var dialog = emr.setupConfirmationDialog({
				selector: '#attribute-types-dialog',
				actions: {
					confirm: function() {
//						$scope.entity.codes = $scope.entity.codes || [];
//						$scope.submitted = true;
//						if(angular.isDefined($scope.itemCode) && $scope.itemCode.code !== ""){
//							$scope.entity.codes.push($scope.itemCode);
//							insertItemTemporaryId($scope.entity.codes, $scope.itemCode);
//							$scope.itemCode = {};
//						}
//						$scope.$apply();
						dialog.close();
					},
					cancel: function() {
						dialog.close();
					}
				}
			});

			dialog.show();
		}

		function addMessageLabels() {
			var messages = {};
			messages['openhmis.inventory.general.add'] = emr.message('openhmis.inventory.general.add');
			messages['openhmis.backboneforms.attribute.type.name'] = emr.message('openhmis.backboneforms.attribute.type.name');
			messages['openhmis.inventory.general.edit'] = emr.message('openhmis.inventory.general.edit');
			return messages;
		}

	}

})();
