(function() {
	'use strict';

	var app = angular.module('app.operationsTypeFunctionsFactory', []);
	app.service('OperationsTypeFunctions', OperationsTypeFunctions);

	OperationsTypeFunctions.$inject = [];

	function OperationsTypeFunctions() {
		var service;
		service = {
			addMessageLabels : addMessageLabels,
			addAttributeType : addAttributeType,
			insertOperationTypesTemporaryId : insertOperationTypesTemporaryId,
			removeOperationTypesTemporaryId : removeOperationTypesTemporaryId,
			removeAttributeType : removeAttributeType,
			removeFromList : removeFromList,
			editAttributeType : editAttributeType,
		};

		return service;

		/**
		 * Displays a popup dialog box with the attribute types . Saves the code on clicking the 'Ok' button
		 * @param $scope
		 */
		function addAttributeType($scope){
			$scope.editAttributeTypeTitle = '';
			$scope.addAttributeTypeTitle = $scope.messageLabels['openhmis.inventory.general.add'] + ' ' +  $scope.messageLabels['openhmis.backboneforms.attribute.type.name'];
			$scope.saveButton = $scope.messageLabels['general.save'];
			var dialog = emr.setupConfirmationDialog({
				selector: '#attribute-types-dialog',
				actions: {
					confirm: function() {
						$scope.entity.attributeTypes = $scope.entity.attributeTypes || [];
						$scope.submitted = true;
						if(angular.isDefined($scope.attributeType) && $scope.attributeType.name !== ""){
							$scope.entity.attributeTypes.push($scope.attributeType);
							insertOperationTypesTemporaryId($scope.entity.attributeTypes, $scope.attributeType);
							$scope.attributeType = {};
							console.log($scope.attributeTypes);
						}
						$scope.$apply();
						dialog.close();
					},
					cancel: function() {
						dialog.close();
					}
				}
			});

			dialog.show();
		}

		/**
		 * Opens a popup dialog box to edit an item code
		 * @param attributeType
		 * @param ngDialog
		 * @param $scope
		 */
		function editAttributeType(attributeType, $scope){
			$scope.attributeType = attributeType;
			$scope.editAttributeTypeTitle = $scope.messageLabels['openhmis.inventory.general.edit'] + ' ' + $scope.messageLabels['openhmis.backboneforms.attribute.type.name'];
			$scope.editButton = $scope.messageLabels['general.update'];
			$scope.addAttributeTypeTitle = '';
			var dialog = emr.setupConfirmationDialog({
				selector: '#attribute-types-dialog',
				actions: {
					confirm: function() {
						$scope.attributeType = {};
						dialog.close();
					},
					cancel: function() {
						$scope.attributeType = {};
						dialog.close();
					}
				}
			});

			dialog.show();
		}


		/**
		 * ng-repeat requires that every item have a unique identifier.
		 * This function sets a temporary unique id for all items in the list.
		 * @param items (prices, codes)
		 * @param item - optional
		 */
		function insertOperationTypesTemporaryId(attributeTypes, attributeType){
			if(angular.isDefined(attributeType)){
				var index = attributeTypes.indexOf(attributeType);
				attributeType.id = index;
			}
			else{
				for(var attributeType in attributeTypes){
					var index = attributeTypes.indexOf(attributeType);
					attributeType.id = index;
				}
			}
		}

		/**
		 * Removes an attribute Type from the list
		 * @param attribute Type
		 * @param attribute Types
		 */
		function removeAttributeType(attributeType, attributeTypes){
			removeFromList(attributeType, attributeTypes);
		}

		/**
		 * Searches an item and removes it from the list
		 * @param item
		 * @param items
		 */
		function removeFromList(attributeType, attributeTypes){
			var index = attributeTypes.indexOf(attributeType);
			attributeTypes.splice(index, 1);
		}

		/**
		 * Remove the temporary unique id from all items (prices, codes) before submitting.
		 * @param items
		 */
		function removeOperationTypesTemporaryId(attributeTypes){
			for(var index in attributeTypes){
				var attributeType = attributeTypes[index];
				delete attributeType.id;
			}
		}


		function addMessageLabels() {
			var messages = {};
			messages['openhmis.inventory.general.add'] = emr.message('openhmis.inventory.general.add');
			messages['openhmis.backboneforms.attribute.type.name'] = emr.message('openhmis.backboneforms.attribute.type.name');
			messages['openhmis.inventory.general.edit'] = emr.message('openhmis.inventory.general.edit');
			messages['openhmis.inventory.general.saveChanges'] = emr.message("openhmis.inventory.general.saveChanges");
			messages['openhmis.inventory.general.confirm'] = emr.message("openhmis.inventory.general.confirm");
			return messages;
		}

	}

})();
