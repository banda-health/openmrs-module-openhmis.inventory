/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
define(
    [
        openhmis.url.backboneBase + 'js/view/generic',
        openhmis.url.inventoryBase + 'js/model/stockroom',
        openhmis.url.inventoryBase + 'js/model/inventoryStockTake',
        'link!' + openhmis.url.inventoryBase + 'css/style.css'
    ],

    function(openhmis) {
        openhmis.InventoryStockTakeAddEditView = Backbone.View.extend({
            edit: function() {/*make sure that list entries are not editable by clicking*/},
        });

        openhmis.InventoryStockTakeSearchableListView = openhmis.GenericSearchableListView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/inventoryStockTake.html',
            tmplSelector: '#inventory-stock-take-list',

            events: {
                'click .submit' : 'save',
            },

            initialize: function(options) {
                openhmis.GenericSearchableListView.prototype.initialize.call(this, options);
                this.itemStockDetails = {};
                this.searchView.on('resetItemStockAdjustments', this.resetItemStockAdjustments);
            },

            resetItemStockAdjustments: function() {
            	this.itemStockDetails = {}
            },

            addOne: function(model, schema, lineNumber) {
                openhmis.GenericSearchableListView.prototype.addOne.call(this, model, schema, lineNumber);
                var self = this
                model.view.on('quantityChange', function() {
                    var uuid = this.model.get('uuid');
                    if(this.model.get('actualQuantity') != null
                            && this.model.get('actualQuantity') != "" && this.model.get('actualQuantity') != this.model.get('quantity')) {
                        self.itemStockDetails[uuid] = this.model;
                    } else {
                        delete self.itemStockDetails[uuid];
                    }
                });
            },

            save: function () {
                var stockTakeDetails = new openhmis.InventoryStockTake();
                var itemStockDetailsArray = this.convertToArray(this.itemStockDetails)
                stockTakeDetails.set("operationNumber", "bla");
                stockTakeDetails.set("stockTakeDetailList", itemStockDetailsArray);

            	stockTakeDetails.save(null, {
					success: function(stockTakeDetails, resp) {

					},
					error: function(stockTakeDetails, resp) {
						openhmis.error(resp);
					}
				});
            },

            convertToArray: function(associativeArray) {
                var array = [];
                for (var key in associativeArray) {
                    array.push(associativeArray[key]);
                }
                return array;
            }
        });

        openhmis.InventoryStockTakeListItemView = openhmis.GenericListItemView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/inventoryStockTake.html',
            tmplSelector: '#inventory-stock-take-list-item',

            events: {
                'change .actual-quantity' : 'changeItemStockDetail'
            },

            changeItemStockDetail: function(event) {
            	var inputValue = $(event.currentTarget).val();
            	if (inputValue < 0) {
            		inputValue = 0;
            		$(event.currentTarget).val(inputValue);
            	};
            	this.model.set('actualQuantity', inputValue);
                this.trigger('quantityChange', this);
            },
        });

        return openhmis;
    }
);