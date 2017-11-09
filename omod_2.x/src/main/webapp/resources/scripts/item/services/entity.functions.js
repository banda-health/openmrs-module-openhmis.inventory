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

	var app = angular.module('app.itemFunctionsFactory', []);
	app.service('ItemFunctions', ItemFunctions);

	ItemFunctions.$inject = ['EntityFunctions'];

	function ItemFunctions(EntityFunctions) {
		var service;

		service = {
			formatItemPrice : formatItemPrice,
			removeItemPrice : removeItemPrice,
			removeItemCode : removeItemCode,
			insertItemTemporaryId : insertItemTemporaryId,
			removeItemTemporaryId : removeItemTemporaryId,
			addItemCode : addItemCode,
			addItemPrice : addItemPrice,
			editItemPrice : editItemPrice,
			editItemCode : editItemCode,
			addMessageLabels : addMessageLabels,
		};

		return service;

		/**
		 * @param itemPrice
         * @returns {*}
         */
		function formatItemPrice(itemPrice){
			var price = itemPrice.price;
			if(price !== undefined){
				price = (Math.round(price * 100)/ 100).toFixed(2);
			}
			else{
				price = '';
			}

			if(itemPrice.name === undefined || itemPrice.name === '' || itemPrice.name === null){
				return price;
			}
			else{
				return price + ' (' + itemPrice.name + ')';
			}
		}

		/**
		 * Removes an item price from the list
		 * @param itemPrice
		 * @param itemPrices
		 */
		function removeItemPrice(itemPrice, itemPrices) {
			removeFromList(itemPrice, itemPrices);
		}

		/**
		 * Removes an item code from the list
		 * @param itemCode
		 * @param itemCodes
		 */
		function removeItemCode(itemCode, itemCodes) {
			removeFromList(itemCode, itemCodes);
		}

		/**
		 * Searches an item and removes it from the list
		 * @param item
		 * @param items
		 */
		function removeFromList(item, items) {
			var index = items.indexOf(item);
			items.splice(index, 1);
		}

		/**
		 * ng-repeat requires that every item have a unique identifier.
		 * This function sets a temporary unique id for all items in the list.
		 * @param items (prices, codes)
		 * @param item - optional
		 */
		function insertItemTemporaryId(items, item) {
			var rand = Math.floor((Math.random() * 99) + 1);
			if (angular.isDefined(item)) {
				var index = items.indexOf(item);
				item.id = index * rand;
			} else {
				for ( var item in items) {
					var index = items.indexOf(item);
					item.id = index * rand;
				}
			}
		}

		/**
		 * Remove the temporary unique id from all items (prices, codes) before submitting.
		 * @param items
		 */
		function removeItemTemporaryId(items) {
			for ( var index in items) {
				var item = items[index];
				delete item.id;
			}
		}

		/**
		 * Displays a popup dialog box with an item code field. Saves the code on clicking the 'Ok' button
		 * @param $scope
		 */
		function addItemCode($scope) {
			$scope.editItemCodeTitle = '';
			$scope.addItemCodeTitle = $scope.messageLabels['openhmis.commons.general.add']
					+ ' '
					+ $scope.messageLabels['openhmis.inventory.item.code.name'];
			var dialog = emr.setupConfirmationDialog({
				selector : '#item-code-dialog',
				actions : {
					confirm : function() {
						$scope.entity.codes = $scope.entity.codes || [];
						$scope.submitted = true;
						if (angular.isDefined($scope.itemCode)
								&& $scope.itemCode.code !== "") {
							$scope.entity.codes.push($scope.itemCode);
							insertItemTemporaryId($scope.entity.codes,
									$scope.itemCode);
							$scope.itemCode = {};
						}
						$scope.$apply();
						dialog.close();
					},
					cancel : function() {
						dialog.close();
					}
				}
			});

			dialog.show();

			EntityFunctions.disableBackground();
		}

		/**
		 * Displays a popup dialog box with price fields and saves the item price to a list.
		 * @param $scope
		 */
		function addItemPrice($scope) {
			$scope.itemPrice = {};
			$scope.editItemPriceTitle = '';
			$scope.addItemPriceTitle = $scope.messageLabels['openhmis.commons.general.add']
					+ ' '
					+ $scope.messageLabels['openhmis.inventory.item.price.name'];
			var dialog = emr.setupConfirmationDialog({
				selector : '#item-price-dialog',
				actions : {
					confirm : function() {
						$scope.entity.prices = $scope.entity.prices || [];
						$scope.entity.prices.push($scope.itemPrice);
						insertItemTemporaryId($scope.entity.prices,
								$scope.itemPrice);
						$scope.entity.defaultPrice = $scope.entity.defaultPrice
								|| $scope.entity.prices[0];
						$scope.itemPrice = {};
						$scope.$apply();
						dialog.close();
					},
					cancel : function() {
						dialog.close();
					}
				}
			});

			dialog.show();

			EntityFunctions.disableBackground();
		}

		/**
		 * Opens a popup dialog box to edit an item price
		 * @param itemPrice
		 * @param ngDialog
		 * @param $scope
		 */
		function editItemPrice(itemPrice, $scope) {
			var tmpItemPrice = itemPrice;

			var editItemPrice = {
				name : itemPrice.name,
				price : itemPrice.price,
			}

			$scope.itemPrice = editItemPrice;
			$scope.addItemPriceTitle = '';
			$scope.editItemPriceTitle = $scope.messageLabels['openhmis.commons.general.edit']
					+ ' '
					+ $scope.messageLabels['openhmis.inventory.item.price.name'];

			var dialog = emr.setupConfirmationDialog({
				selector : '#item-price-dialog',
				actions : {
					confirm : function() {
						tmpItemPrice.name = $scope.itemPrice.name;
						tmpItemPrice.price = $scope.itemPrice.price;

						$scope.$apply();

						$scope.itemPrice = {};
						dialog.close();
					},
					cancel : function() {
						$scope.itemPrice = {};
						dialog.close();
					}
				}
			});

			dialog.show();

			EntityFunctions.disableBackground();
		}

		/**
		 * Opens a popup dialog box to edit an item code
		 * @param itemCode
		 * @param ngDialog
		 * @param $scope
		 */
		function editItemCode(itemCode, $scope) {
			var tmpItemCode = itemCode;

			var editItemCode = {
				code : itemCode.code
			};

			$scope.itemCode = editItemCode;
			$scope.editItemCodeTitle = $scope.messageLabels['openhmis.commons.general.edit']
					+ ' '
					+ $scope.messageLabels['openhmis.inventory.item.code.name'];
			$scope.addItemCodeTitle = '';
			var dialog = emr.setupConfirmationDialog({
				selector : '#item-code-dialog',
				actions : {
					confirm : function() {
						tmpItemCode.code = $scope.itemCode.code;

						$scope.$apply();

						$scope.itemCode = {};
						dialog.close();
					},
					cancel : function() {
						$scope.itemCode = {};
						dialog.close();
					}
				}
			});

			dialog.show();

			EntityFunctions.disableBackground();
		}

		/**
		 * All message labels used in the UI are defined here
		 * @returns {{}}
		 */
		function addMessageLabels() {
			var messages = {};
			messages['openhmis.inventory.item.enterConceptName'] = emr
					.message('openhmis.inventory.item.enterConceptName');
			messages['openhmis.inventory.item.price.name'] = emr
					.message('openhmis.inventory.item.price.name');
			messages['openhmis.inventory.item.code.name'] = emr
					.message('openhmis.inventory.item.code.name');
			messages['openhmis.inventory.department.name'] = emr
					.message('openhmis.inventory.department.name');
			messages['openhmis.inventory.item.hasExpiration'] = emr
					.message('openhmis.inventory.item.hasExpiration');
			messages['openhmis.inventory.item.defaultExpirationPeriod'] = emr
					.message('openhmis.inventory.item.defaultExpirationPeriod');
			messages['Concept'] = emr.message('Concept');
			messages['openhmis.inventory.item.hasPhysicalInventory'] = emr
					.message('openhmis.inventory.item.hasPhysicalInventory');
			messages['openhmis.inventory.item.minimumQuantity'] = emr
					.message('openhmis.inventory.item.minimumQuantity');
			messages['openhmis.inventory.item.buyingPrice'] = emr
					.message('openhmis.inventory.item.buyingPrice');
			messages['openhmis.inventory.item.code.namePlural'] = emr
					.message('openhmis.inventory.item.code.namePlural');
			messages['openhmis.inventory.item.prices'] = emr
					.message('openhmis.inventory.item.prices');
			messages['openhmis.inventory.item.defaultPrice'] = emr
					.message('openhmis.inventory.item.defaultPrice');
			messages['openhmis.inventory.stockroom.name'] = emr
					.message('openhmis.inventory.stockroom.name');
			messages['openhmis.inventory.item.quantity'] = emr
					.message('openhmis.inventory.item.quantity');
			messages['openhmis.commons.general.add'] = emr
					.message('openhmis.commons.general.add');
			messages['openhmis.commons.general.edit'] = emr
					.message('openhmis.commons.general.edit');
			return messages;
		}
	}
})();
