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

	var baseModel = angular.module('app.lineItemModel', []);

	function LineItemModel() {

		function LineItemModel(itemStockDepartment, itemStock,
		                       itemStockQuantity, itemStockExpirationDate,
		                       itemStockHasExpiration, expirationDates, existingQuantity) {
			this.itemStockDepartment = itemStockDepartment;
			this.itemStock = itemStock;
			this.itemStockQuantity = itemStockQuantity;
			this.itemStockExpirationDate = itemStockExpirationDate;
			this.itemStockHasExpiration = itemStockHasExpiration;
			this.expirationDates = expirationDates;
			this.existingQuantity = existingQuantity;
			this.selected = false;
			this.expirationHasDatePicker = false;
			this.newQuantity = '';
			this.itemStockDetails = [];
			this.invalidEntry = false;
		}

		LineItemModel.prototype = {

			getItemStockDepartment: function() {
				return this.itemStockDepartment;
			},

			setItemStockDepartment: function(itemStockDepartment) {
				this.itemStockDepartment = itemStockDepartment;
			},

			getItemStock: function() {
				return this.itemStock;
			},

			setItemStock: function(itemStock) {
				this.itemStock = itemStock;
			},

			getItemStockQuantity: function() {
				return this.itemStockQuantity;
			},

			setItemStockQuantity: function(itemStockQuantity) {
				this.itemStockQuantity = itemStockQuantity;
			},

			getItemStockExpirationDate: function() {
				return this.itemStockExpirationDate;
			},

			setItemStockExpirationDate: function(itemStockExpirationDate) {
				this.itemStockExpirationDate = itemStockExpirationDate;
			},

			getItemStockHasExpiration: function() {
				return this.itemStockHasExpiration;
			},

			setItemStockHasExpiration: function(itemStockHasExpiration) {
				this.itemStockHasExpiration = itemStockHasExpiration;
			},

			setExpirationDates: function(expirationDates) {
				this.expirationDates = expirationDates;
			},

			getExpirationDates: function() {
				return this.expirationDates;
			},

			setExistingQuantity: function(existingQuantity) {
				this.existingQuantity = existingQuantity;
			},

			getExistingQuantity: function() {
				return this.existingQuantity;
			},

			setSelected: function(selected) {
				this.selected = selected;
			},

			isSelected: function() {
				return this.selected;
			},

			setExpirationHasDatePicker: function(expirationHasDatePicker) {
				this.expirationHasDatePicker = expirationHasDatePicker;
			},

			isExpirationHasDatePicker: function() {
				return this.expirationHasDatePicker;
			},

			setNewQuantity: function(newQuantity) {
				this.newQuantity = newQuantity;
			},

			getNewQuantity: function() {
				return this.newQuantity;
			},

			setItemStockDetails: function(itemStockDetails) {
				this.itemStockDetails = itemStockDetails;
			},

			getItemStockDetails: function() {
				return this.itemStockDetails;
			},

			isInvalidEntry: function() {
				return this.invalidEntry;
			},

			setInvalidEntry: function(invalidEntry) {
				this.invalidEntry = invalidEntry;
			},
		};

		return LineItemModel;
	}

	baseModel.factory("LineItemModel", LineItemModel);
	LineItemModel.$inject = [];
})();
