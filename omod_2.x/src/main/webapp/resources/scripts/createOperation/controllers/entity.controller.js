/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 *
 */

(function() {
	'use strict';

	var base = angular.module('app.genericEntityController');
	base.controller("CreateOperationController", CreateOperationController);
	CreateOperationController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory',
		'OperationModel', 'CreateOperationRestfulService', 'PaginationService', 'CreateOperationFunctions',
		'CookiesService', 'LineItemModel', 'CommonsRestfulFunctions'];

	var ROOT_URL = '/' + OPENMRS_CONTEXT_PATH + '/';
	var MY_OPERATIONS_URL = '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/myOperations/entities.page';
	var GENERATE_OPERATION_NUMBER = "WILL BE GENERATED";

	function CreateOperationController($stateParams, $injector, $scope, $filter, EntityRestFactory, OperationModel,
	                                   CreateOperationRestfulService, PaginationService, CreateOperationFunctions,
	                                   CookiesService, LineItemModel, CommonsRestfulFunctions) {
		var self = this;
		var module_name = 'inventory';
		var entity_name_message_key = emr.message("openhmis.inventory.stock.operation.name");
		var cancel_page = '/' + OPENMRS_CONTEXT_PATH + '/openhmis.inventory/inventory/inventoryTasksDashboard.page';
		var rest_entity_name = emr.message("openhmis.inventory.stock.operation.rest_name");
		var notDefined = {name: ' - Not Defined - '};

		// @Override
		self.setRequiredInitParameters = self.setRequiredInitParameters || function() {
				self.bindBaseParameters(module_name, rest_entity_name, entity_name_message_key, cancel_page);
			}

		/**
		 * Initializes and binds any required variable and/or function specific to entity.page
		 * @type {Function}
		 */
			// @Override
		self.bindExtraVariablesToScope = self.bindExtraVariablesToScope
			|| function() {
				$scope.loading = true;
				if (self.sessionLocation === undefined) {
					CommonsRestfulFunctions.getSessionLocation(module_name, self.onLoadSessionLocationSuccessful);
				}

				$scope.isOperationNumberGenerated = false;
				$scope.isNegativeStockRestricted = false;
				CreateOperationRestfulService.isOperationNumberGenerated(self.onLoadOpNumGenSuccessful);
				CreateOperationRestfulService.isNegativeStockRestricted(self.onLoadNegativeStockRestrictedSuccessful);
				$scope.totalNumOfResults = 0;
				$scope.limit = CookiesService.get('limit') || 5;
				$scope.currentPage = CookiesService.get('currentPage') || 1;
				$scope.pagingFrom = PaginationService.pagingFrom;
				$scope.pagingTo = PaginationService.pagingTo;
				$scope.operationDate = CreateOperationFunctions.formatDate(new Date(), true);
				$scope.changeOperationDate = self.changeOperationDate;
				$scope.expirationDate = '';
				$scope.operationTypes = [];
				CreateOperationRestfulService.loadStockOperationTypes(module_name, self.onLoadOperationTypesSuccessful);
				$scope.sourceStockrooms = [];
				$scope.destinationStockrooms = [];
				CreateOperationRestfulService.loadStockrooms(module_name, self.onLoadStockroomsSuccessful);
				$scope.institutions = [];
				CreateOperationRestfulService.loadInstitutions(module_name, self.onLoadInstitutionsSuccessful);
				$scope.departments = [];
				CreateOperationRestfulService.loadDepartments(module_name, self.onLoadDepartmentsSuccessful);
				$scope.loadOperationTypeAttributes = self.loadOperationTypeAttributes;
				$scope.patient;
				$scope.patients = [];
				$scope.searchPatients = self.searchPatients;
				$scope.searchFieldAttributePerson = self.searchFieldAttributePerson;
				$scope.searchFieldAttributePatients = self.searchFieldAttributePatients;
				$scope.selectPatient = self.selectPatient;
				$scope.changePatient = self.changePatient;
				$scope.selectedPatient = '';
				$scope.visit = '';
				$scope.endVisit = self.endVisit;
				$scope.postSearchMessage =
					$filter('EmrFormat')(emr.message("openhmis.commons.general.postSearchMessage"), ['patient']);
				$scope.searchStockOperationItems = self.searchStockOperationItems;
				$scope.selectStockOperationItem = self.selectStockOperationItem;
				$scope.searchItemStock = self.searchItemStock;
				$scope.lineItems = [];
				$scope.addLineItem = self.addLineItem;
				$scope.removeLineItem = self.removeLineItem;
				$scope.warningDialog = self.warningDialog;
				$scope.distributionTypes = ["Patient", "Department", "Institution"];
				$scope.distributionType = $scope.distributionTypes[0];
				$scope.returnOperationTypes = ["Department", "Institution", "Patient"];
				$scope.returnOperationType = $scope.returnOperationTypes[0];
				$scope.showOperationItemsSection = self.showOperationItemsSection;
				$scope.changeItemQuantity = self.changeItemQuantity;
				$scope.changeExpiration = self.changeExpiration;
				CreateOperationFunctions.onChangeDatePicker(
					self.onOperationDateSuccessfulCallback,
					'operationDateId-display');
				$scope.loading = false;
			}

		/**
		 * All post-submit validations are done here.
		 * @return boolean
		 */
			// @Override
		self.validateBeforeSaveOrUpdate = self.validateBeforeSaveOrUpdate || function() {
				$scope.submitted = false;
				$scope.loading = false;
				// validate operation number
				if (!CreateOperationFunctions.validateOperationNumber($scope)) {
					return false;
				}

				$scope.entity.status = "NEW";
				$scope.entity.instanceType = $scope.operationType.uuid;

				if ($scope.operationDate !== undefined) {
					$scope.entity.operationDate = $scope.operationDate;
				}

				if ($scope.sourceStockroom !== undefined) {
					$scope.entity.source = $scope.sourceStockroom.uuid;
				}

				if ($scope.destinationStockroom !== undefined && $scope.operationType.hasDestination) {
					$scope.entity.destination = $scope.destinationStockroom.uuid;
				}

				// validate institution
				if ($scope.institutionStockroom !== undefined &&
					(($scope.operationType.name === 'Distribution' && $scope.distributionType === 'Institution') ||
					$scope.operationType.name === 'Return' && $scope.returnOperationType === 'Institution')) {
					if ($scope.institutionStockroom.name !== notDefined.name) {
						$scope.entity.institution = $scope.institutionStockroom.uuid;
					} else {
						emr.errorAlert("openhmis.inventory.operations.required.institution");
						return false;
					}
				}

				// validate department
				if ($scope.department !== undefined &&
					(($scope.operationType.name === 'Distribution' && $scope.distributionType === 'Department') ||
					$scope.operationType.name === 'Return' && $scope.returnOperationType === 'Department')) {
					if ($scope.department.name !== notDefined.name) {
						$scope.entity.department = $scope.department.uuid;
					} else {
						emr.errorAlert("openhmis.inventory.operations.required.department");
						return false;
					}
				}

				// validate selected patient
				if ($scope.entity.institution === "" && $scope.entity.department === "" && !CreateOperationFunctions.validatePatient($scope)) {
					return false;
				}

				// validate attribute types
				if (!CreateOperationFunctions.validateAttributeTypes($scope)) {
					return false;
				}

				// validate selected line items.
				if (!CreateOperationFunctions.validateLineItems($scope)) {
					return false;
				}

				$scope.loading = true;
				return true;
			};

		self.checkDatePickerExpirationSection = self.checkDatePickerExpirationSection || function(lineItem) {
				return CreateOperationFunctions.checkDatePickerExpirationSection(lineItem, $scope);
			}

		self.showOperationItemsSection = self.showOperationItemsSection || function() {
				return CreateOperationFunctions.showOperationItemsSection($scope);
			}

		self.changeOperationDate = self.changeOperationDate || function() {
				CreateOperationFunctions.changeOperationDate($scope);
			}

		self.changeItemQuantity = self.changeItemQuantity || function(lineItem) {
				var quantity = lineItem.itemStockQuantity;
				if (quantity == 0 || ($scope.operationType.name !== 'Adjustment' && quantity <= 0)) {
					emr.errorAlert("openhmis.inventory.operations.error.itemError");
					lineItem.itemStockQuantity = 1;
				} else {
					var newQuantity;
					if ($scope.operationType.name === 'Adjustment' || $scope.operationType.name === 'Receipt') {
						newQuantity = lineItem.existingQuantity + quantity;
					} else {
						newQuantity = lineItem.existingQuantity - quantity;
					}

					lineItem.setNewQuantity(newQuantity);
				}
			}

		self.loadStockOperations = self.loadStockOperations || function(date) {
				CreateOperationRestfulService.loadStockOperations(module_name, date, self.onLoadStockOperationsSuccessful);
			}

		self.warningDialog = self.warningDialog || function(newVal, oldVal, source) {
				self.loadOperationTypeAttributes();
				if ($scope.lineItems.length > 0) {
					var lineItem = $scope.lineItems[0];
					if (lineItem.itemStock !== "") {
						CreateOperationFunctions.changeWarningDialog($scope, newVal, oldVal, source);
					}
				} else {
					self.addLineItem();
				}
			}

		self.addLineItem = self.addLineItem || function() {
				var addItem = true;
				for (var i = 0; i < $scope.lineItems.length; i++) {
					var lineItem = $scope.lineItems[i];
					if (!lineItem.selected) {
						addItem = false;
						break;
					}
				}
				if (addItem) {
					var lineItem = new LineItemModel('', '', 1, '', false);
					$scope.lineItems.push(lineItem);
				}
			}

		self.removeLineItem = self.removeLineItem || function(lineItem) {
				if (lineItem.selected) {
					var index = $scope.lineItems.indexOf(lineItem);
					if (index !== -1) {
						$scope.lineItems.splice(index, 1);
					}

					if ($scope.lineItems.length == 0) {
						self.addLineItem();
					}
				}
			}

		self.searchPatients = self.searchPatients || function(currentPage) {
				if ($scope.patient !== undefined) {
					$scope.currentPage = $scope.currentPage || currentPage;
					$scope.patients = CommonsRestfulFunctions.searchPatients(
						module_name, $scope.patient, $scope.currentPage,
						$scope.limit, $scope);
				}
			}

		self.selectPatient = self.selectPatient || function(patient) {
				$scope.selectedPatient = patient;
				CommonsRestfulFunctions.loadVisit(module_name, patient.uuid, $scope);
			}

		self.changePatient = self.changePatient || function() {
				$scope.selectedPatient = '';
			}

		self.endVisit = self.endVisit || function() {
				CommonsRestfulFunctions.endVisit(module_name, $scope.visit.uuid, $scope);
			}

		self.searchStockOperationItems = self.searchStockOperationItems || function(search) {
				return CreateOperationRestfulService.searchStockOperationItems(module_name, search);
			}

		self.selectStockOperationItem = self.selectStockOperationItem || function(stockOperationItem, lineItem) {
				$scope.lineItem = {};
				lineItem.setInvalidEntry(false);
				lineItem.setExistingQuantity(0);
				lineItem.setNewQuantity('');
				lineItem.setItemStockQuantity(1);
				if (stockOperationItem !== undefined) {
					lineItem.setItemStock(stockOperationItem);
					lineItem.setItemStockDepartment(stockOperationItem.department);
					lineItem.setItemStockHasExpiration(stockOperationItem.hasExpiration);
					lineItem.setSelected(true);
					self.checkDatePickerExpirationSection(lineItem);
					$scope.lineItem = lineItem;

					self.searchItemStock(stockOperationItem);

					if (lineItem.expirationHasDatePicker) {
						CreateOperationFunctions.onChangeDatePicker(self.onLineItemExpDateSuccessfulCallback);
					}

					// load next line item
					self.addLineItem();
				} else {
					lineItem.setItemStockHasExpiration(false);
				}
			}

		self.searchFieldAttributePatients = self.searchFieldAttributePatients || function(q) {
				return CommonsRestfulFunctions.searchPerson(module_name, q, 'patient');
			}

		self.searchFieldAttributePerson = self.searchFieldAttributePerson || function(q) {
				return CommonsRestfulFunctions.searchPerson(module_name, q, 'person');
			}

		self.searchItemStock = self.searchItemStock || function(stockOperationItem) {
				if ("uuid" in stockOperationItem && $scope.sourceStockroom !== undefined) {
					CreateOperationRestfulService.searchItemStock(module_name, stockOperationItem.uuid, $scope.sourceStockroom.uuid,
						self.onLoadItemStockSuccessful);
				}
			}

		self.changeExpiration = self.changeExpiration || function(lineItem) {
				if (lineItem.itemStockExpirationDate !== 'Auto') {
					var selectedExpiration = lineItem.itemStockExpirationDate;
					var existingQuantity = 0;
					if (selectedExpiration === 'None') {
						selectedExpiration = null;
					}
					for (var i = 0; i < lineItem.itemStockDetails.details.length; i++) {
						var detail = lineItem.itemStockDetails.details[i];
						var expiration = detail.expiration;
						if (expiration !== null) {
							expiration = expiration.split("T")[0];
							expiration = CreateOperationFunctions.formatDate(expiration);
						}

						if (expiration === selectedExpiration) {
							existingQuantity += detail.quantity;
						}
					}

					lineItem.existingQuantity = existingQuantity;
				} else {
					lineItem.existingQuantity = lineItem.itemStockDetails.quantity;
				}

				self.changeItemQuantity(lineItem);
			}

		self.loadOperationTypeAttributes = self.loadOperationTypeAttributes || function() {
				if ($scope.operationType !== undefined) {
					CreateOperationRestfulService.loadOperationTypeAttributes(module_name, $scope.operationType.uuid,
						self.onLoadOperationTypeAttributesSuccessful);
				}
			}

		// callbacks..
		self.onLoadOpNumGenSuccessful = self.onLoadOpNumGenSuccessful || function(data) {
				$scope.isOperationNumberGenerated = false;
				if (data.results && data.results === "true") {
					$scope.isOperationNumberGenerated = true;
					$scope.entity.operationNumber = GENERATE_OPERATION_NUMBER;
				}
			}

		self.onLoadNegativeStockRestrictedSuccessful = self.onLoadNegativeStockRestrictedSuccessful || function(data) {
				$scope.isNegativeStockRestricted = false;
				if (data.results && data.results === "true") {
					$scope.isNegativeStockRestricted = true;
				}
			}

		self.onLoadOperationTypesSuccessful = self.onLoadOperationTypesSuccessful || function(data) {
				$scope.operationTypes = data.results;
				if ($scope.operationType === undefined) {
					for (var i = 0; $scope.operationTypes.length; i++) {
						var operationType = $scope.operationTypes[i];
						if (operationType.canProcess) {
							$scope.operationType = operationType;
							break;
						}
					}
				}

				// load operation type attributes first time the page loads.
				self.loadOperationTypeAttributes();
			}

		self.onLoadStockroomsSuccessful = self.onLoadStockroomsSuccessful || function(data) {
				var stockrooms = data.results;
				stockrooms.unshift(notDefined);
				$scope.destinationStockrooms = stockrooms;
				$scope.destinationStockroom = $scope.destinationStockroom || $scope.destinationStockrooms[0]

				$scope.sourceStockrooms = stockrooms;
				if (self.sessionLocation !== undefined && $scope.sourceStockroom === undefined) {
					for (var i = 0; i < $scope.sourceStockrooms.length; i++) {
						var stockroom = $scope.sourceStockrooms[i];
						if (stockroom !== undefined && stockroom.name === self.sessionLocation) {
							$scope.sourceStockroom = stockroom;
						}
					}
				}

				if ($scope.sourceStockroom !== undefined) {
					self.warningDialog();
				}

				$scope.sourceStockroom = $scope.sourceStockroom || $scope.sourceStockrooms[0]
			}

		self.onLoadInstitutionsSuccessful = self.onLoadInstitutionsSuccessful || function(data) {
				$scope.institutions = data.results;
				$scope.institutions.unshift(notDefined);
				$scope.institutionStockroom = $scope.institutions[0];
			}

		self.onLoadDepartmentsSuccessful = self.onLoadDepartmentsSuccessful || function(data) {
				$scope.departments = data.results;
				$scope.departments.unshift(notDefined);
				$scope.department = $scope.departments[0];
				$scope.itemStockDepartments = data.results;
			}

		self.onLoadOperationTypeAttributesSuccessful = self.onLoadOperationTypeAttributesSuccessful || function(data) {
				var results = data;
				$scope.fieldAttributesData = [];
				if ("attributeTypes" in results) {
					$scope.attributeTypeAttributes = results.attributeTypes;
					$scope.attributes = {};
					if ($scope.attributeTypeAttributes != null && $scope.attributeTypeAttributes.length > 0) {
						for (var i = 0; i < $scope.attributeTypeAttributes.length; i++) {
							var attribute = {
								attributeType: $scope.attributeTypeAttributes[i].uuid,
								value: "",
								required: $scope.attributeTypeAttributes[i].required,
							};
							$scope.attributes[$scope.attributeTypeAttributes[i].uuid] = attribute;
						}
					}

					// load field attributes data
					CommonsRestfulFunctions.populateFieldAttributesData(ROOT_URL,
						$scope.fieldAttributesData, $scope.attributeTypeAttributes);
				}
			}

		self.onLoadStockOperationItemSuccessful = self.onLoadStockOperationItemSuccessful || function(data) {
				$scope.stockOperationItems = data.results;
				$scope.stockOperationItemTotalNumberOfResults = data.length;
			}

		self.onLoadItemStockSuccessful = self.onLoadItemStockSuccessful || function(data) {
				var itemStocks = data.results;
				var itemStockExpirationDates = CreateOperationFunctions.createExpirationDates(itemStocks);
				$scope.lineItem.setItemStockExpirationDate(itemStockExpirationDates[0]);
				$scope.lineItem.setExpirationDates(itemStockExpirationDates);
				if (itemStocks[0] !== null) {
					$scope.lineItem.setItemStockDetails(itemStocks[0]);
					$scope.lineItem.setExistingQuantity(itemStocks[0].quantity);
					self.changeItemQuantity($scope.lineItem);
				}
			}

		self.onLoadStockOperationsSuccessful = self.onLoadStockOperationsSuccessful || function(data) {
				CreateOperationFunctions.populateOccurDates($scope, data.results);
			}

		self.onOperationDateSuccessfulCallback = self.onOperationDateSuccessfulCallback || function(date) {
				$scope.operationOccurDate = undefined;
				if (date !== undefined) {
					var operationDate = CreateOperationFunctions.formatDate(new Date(date));
					self.loadStockOperations(operationDate);
				}
			}

		self.onLineItemExpDateSuccessfulCallback = self.onLineItemExpDateSuccessfulCallback || function(date) {
				$scope.lineItem.itemStockExpirationDate = CreateOperationFunctions.formatDate(new Date(date));
			}

		self.onLoadSessionLocationSuccessful = self.onLoadSessionLocationSuccessful || function(data) {
				self.sessionLocation = data.sessionLocation.display;
			}

		self.onChangeEntityError = self.onChangeEntityError || function(error) {
				emr.errorAlert(error);
				$scope.loading = false;
			}

		self.onChangeEntitySuccessful = self.onChangeEntitySuccessful || function() {
				window.location = MY_OPERATIONS_URL;
			}

		// @Override
		self.setAdditionalMessageLabels = self.setAdditionalMessageLabels || function() {
			}

		/* ENTRY POINT: Instantiate the base controller which loads the page */
		$injector.invoke(base.GenericEntityController, self, {
			$scope: $scope,
			$filter: $filter,
			$stateParams: $stateParams,
			EntityRestFactory: EntityRestFactory,
			GenericMetadataModel: OperationModel
		});
	}
})();
