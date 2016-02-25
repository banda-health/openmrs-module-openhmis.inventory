(function () {
	'use strict';

	var base = angular.module('app.genericEntityController');
	base.controller("ItemAttributeTypesController", ItemAttributeTypesController);
	ItemAttributeTypesController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory',
		'ItemAttributeTypesModel', 'ItemAttributeTypesRestfulService', 'ItemAttributeTypeFunctions'];

	function ItemAttributeTypesController($stateParams, $injector, $scope, $filter, EntityRestFactory,
				ItemAttributeTypesModel, ItemAttributeTypesRestfulService, ItemAttributeTypeFunctions) {

		var self = this;

		var module_name = 'inventory';
		var entity_name = emr.message("openhmis.inventory.itemAttributeType");
		var cancel_page = 'entities.page';
		var rest_name = emr.message("openhmis.inventory.itemAttributeType_rest");

		// @Override
		self.setRequiredInitParameters = self.setRequiredInitParameters || function () {
				self.bindBaseParameters(module_name, rest_name, entity_name, cancel_page);
			}

		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
			|| function (uuid) {
				if (angular.isDefined($scope.entity) && angular.isDefined($scope.entity.retired)
					&& $scope.entity.retired === true) {
					$scope.retireOrUnretire = $filter('EmrFormat')(emr.message("openhmis.inventory.general.unretire"),
						[self.entity_name]);
				} else {
					$scope.retireOrUnretire = $filter('EmrFormat')(emr.message("openhmis.inventory.general.retire"),
						[self.entity_name]);
				}

				$scope.submitted = $scope.submitted || false;
				$scope.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate;

				// call functions..
				ItemAttributeTypesRestfulService.loadFormatFields(module_name, self.onLoadFormatFieldsSuccessful);

			}

		self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function () {
				if (!angular.isDefined($scope.entity.attributeOrder) || $scope.entity.attributeOrder === '') {
					$scope.entity.attributeOrder = null;
				}
				if (!angular.isDefined($scope.entity.foreignKey) || $scope.entity.foreignKey === '') {
					$scope.entity.foreignKey = null;
				}
				if (!angular.isDefined($scope.entity.name) || $scope.entity.name === '') {
					$scope.submitted = true;
					return false;
				}
				return true;
			}

		// call-back functions.
		self.onLoadFormatFieldsSuccessful = self.onLoadFormatFieldsSuccessful || function (data) {
				$scope.formatFields = data.results;
				return ItemAttributeTypeFunctions.addExtraFormatListElements($scope.formatFields);
			}

		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, self, {
			$scope: $scope,
			$filter: $filter,
			$stateParams: $stateParams,
			EntityRestFactory: EntityRestFactory,
			GenericMetadataModel: ItemAttributeTypesModel
		});
	}
})();
