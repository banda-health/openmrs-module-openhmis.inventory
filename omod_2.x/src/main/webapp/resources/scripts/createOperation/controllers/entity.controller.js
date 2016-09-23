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
		'CookiesService', 'LineItemModel', 'CommonsRestfulFunctions', '$timeout', 'EntityFunctions'];
	
	function CreateOperationController($stateParams, $injector, $scope, $filter, EntityRestFactory, OperationModel,
	                                   CreateOperationRestfulService, PaginationService, CreateOperationFunctions,
	                                   CookiesService, LineItemModel, CommonsRestfulFunctions, $timeout, EntityFunctions) {
		var self = this;
		var entity_name_message_key = emr.message("openhmis.inventory.stock.operation.name");
		var REST_ENTITY_NAME = "stockOperation";
		var notDefined = {name: ' - Not Defined - '};
		var VIEW_STOCK_OPERATIONS = ROOT_URL + 'openhmis.inventory/stockOperations/entities.page#/';
		var GENERATE_OPERATION_NUMBER = "WILL BE GENERATED";
		
		// @Override
		self.setRequiredInitParameters = self.setRequiredInitParameters || function() {
				self.bindBaseParameters(INVENTORY_MODULE_NAME, REST_ENTITY_NAME, entity_name_message_key, INVENTORY_TASK_DASHBOARD_PAGE_URL);
				self.checkPrivileges(TASK_ACCESS_CREATE_OPERATION_PAGE);
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
					CommonsRestfulFunctions.getSession(INVENTORY_MODULE_NAME, self.onLoadSessionLocationSuccessful);
				}

				$scope.count = 0;
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
				CreateOperationRestfulService.loadStockOperationTypes(INVENTORY_MODULE_NAME, self.onLoadOperationTypesSuccessful);
				$scope.sourceStockrooms = [];
				$scope.destinationStockrooms = [];
				CreateOperationRestfulService.loadStockrooms(INVENTORY_MODULE_NAME, self.onLoadStockroomsSuccessful);
				$scope.institutions = [];
				CreateOperationRestfulService.loadInstitutions(INVENTORY_MODULE_NAME, self.onLoadInstitutionsSuccessful);
				$scope.departments = [];
				CreateOperationRestfulService.loadDepartments(INVENTORY_MODULE_NAME, self.onLoadDepartmentsSuccessful);
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
				var newQuantity;
				if ($scope.operationType.name === 'Adjustment' || $scope.operationType.name === 'Receipt') {
					newQuantity = lineItem.existingQuantity + quantity;
				} else {
					newQuantity = lineItem.existingQuantity - quantity;
				}

				lineItem.setNewQuantity(newQuantity);
			}

		self.loadStockOperations = self.loadStockOperations || function(date) {
				CreateOperationRestfulService.loadStockOperations(INVENTORY_MODULE_NAME, date, self.onLoadStockOperationsSuccessful);
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
					$scope.count = $scope.count + 1;
					lineItem.id = $scope.count;
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
						INVENTORY_MODULE_NAME, $scope.patient, $scope.currentPage,
						$scope.limit, $scope);
				}
			}

		self.selectPatient = self.selectPatient || function(patient) {
				$scope.selectedPatient = patient;
				CommonsRestfulFunctions.loadVisit(INVENTORY_MODULE_NAME, patient.uuid, $scope);
			}

		self.changePatient = self.changePatient || function() {
				$scope.selectedPatient = '';
			}

		self.endVisit = self.endVisit || function() {
				CommonsRestfulFunctions.endVisit(INVENTORY_MODULE_NAME, $scope.visit.uuid, $scope);
			}

		self.searchStockOperationItems = self.searchStockOperationItems || function(search) {
				return CreateOperationRestfulService.searchStockOperationItems(INVENTORY_MODULE_NAME, search);
			}

		self.selectStockOperationItem = self.selectStockOperationItem ||
			function(stockOperationItem, lineItem, index) {
				$scope.lineItem = {};
				lineItem.setInvalidEntry(false);
				lineItem.setExistingQuantity(0);
				lineItem.setNewQuantity('');
				lineItem.setItemStockQuantity(1);
				lineItem.setExpirationHasDatePicker(false);
				if (stockOperationItem !== undefined) {
					lineItem.setItemStock(stockOperationItem);
					lineItem.setItemStockDepartment(stockOperationItem.department);
					lineItem.setItemStockHasExpiration(stockOperationItem.hasExpiration);
					lineItem.setSelected(true);
					self.checkDatePickerExpirationSection(lineItem);
					$scope.lineItem = lineItem;

					self.searchItemStock(stockOperationItem);

					if (lineItem.expirationHasDatePicker) {
						if($scope.count !== lineItem.id){
							lineItem.id = $scope.count;
						}

						lineItem.expirationDates = [];
						CreateOperationFunctions.onChangeDatePicker(self.onLineItemExpDateSuccessfulCallback, undefined, lineItem);
					}

					// load next line item
					self.addLineItem();
				} else {
					lineItem.setItemStockHasExpiration(false);
				}

				EntityFunctions.focusOnElement('quantity-' + index);
			}

		self.searchFieldAttributePatients = self.searchFieldAttributePatients || function(q) {
				return CommonsRestfulFunctions.searchPerson(INVENTORY_MODULE_NAME, q, 'patient');
			}

		self.searchFieldAttributePerson = self.searchFieldAttributePerson || function(q) {
				return CommonsRestfulFunctions.searchPerson(INVENTORY_MODULE_NAME, q, 'person');
			}

		self.searchItemStock = self.searchItemStock || function(stockOperationItem) {
				if ("uuid" in stockOperationItem && $scope.sourceStockroom !== undefined) {
					CreateOperationRestfulService.searchItemStock(INVENTORY_MODULE_NAME, stockOperationItem.uuid, $scope.sourceStockroom.uuid,
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
					CreateOperationRestfulService.loadOperationTypeAttributes(INVENTORY_MODULE_NAME, $scope.operationType.uuid,
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
				if (!$scope.lineItem.expirationHasDatePicker) {
					var itemStockExpirationDates = CreateOperationFunctions.createExpirationDates(itemStocks);
					$scope.lineItem.setItemStockExpirationDate(itemStockExpirationDates[0]);
					$scope.lineItem.setExpirationDates(itemStockExpirationDates);
				}

				if (itemStocks[0] !== null) {
					$scope.lineItem.setItemStockDetails(itemStocks[0]);
					$scope.lineItem.setExistingQuantity(
						CreateOperationFunctions.calculateSumItemStockDetailQuantities(itemStocks[0].details));
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
				window.location = VIEW_STOCK_OPERATIONS;
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
