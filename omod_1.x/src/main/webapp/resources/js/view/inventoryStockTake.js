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
        openhmis.url.inventoryBase + 'js/view/stockroom',
        openhmis.url.inventoryBase + 'js/model/inventoryStockTake',
        openhmis.url.inventoryBase + 'js/view/operation',
        'js!' + openhmis.url.inventoryBase + 'js/util.js',
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
                'click #show-details' : 'toggleAdjustmentChangesDetail',
            },

            initialize: function(options) {
                openhmis.GenericSearchableListView.prototype.initialize.call(this, options);
                openhmis.StockTakeChangeCounter = 0;
                this.itemStockDetails = {};
                this.searchView.on('resetItemStockAdjustments', this.resetItemStockAdjustments);
                this.stockTakeDetailsView = new openhmis.StockTakeAdjustmentsList({
                    model: new openhmis.GenericCollection([], {
                        model: openhmis.ItemStockSummary
                    }),
                    showRetiredOption: false,
                    showRetired: false,
                    listFields: ['item','expiration', 'quantity', 'actualQuantity'],
                    itemView: openhmis.InventoryStockTakeListDetailItemView
                });
            },

            resetItemStockAdjustments: function() {
                this.itemStockDetails = {};
                openhmis.StockTakeChangeCounter = 0;
            },

            onSearch: function(options, sender) {
                openhmis.GenericSearchableListView.prototype.onSearch.call(this, options, sender);
                this.stockroom = sender.searchFilter ? sender.searchFilter.stockroom_uuid : null;
            },

            addOne: function(model, schema, lineNumber) {
                openhmis.GenericSearchableListView.prototype.addOne.call(this, model, schema, lineNumber);
                var self = this
                model.view.on('quantityChange', function() {
                    var hash = this.model.get('item').get('uuid') + '_' + this.model.get('expiration');
                    if(this.model.get('actualQuantity') != null
                            && !isNaN(this.model.get('actualQuantity'))
                            && this.model.get('actualQuantity') != this.model.get('quantity')) {
                        self.itemStockDetails[hash] = this.model;
                    } else {
                        delete self.itemStockDetails[hash];
                    }
                    self.updateGlobalStockTakeChangeCounter()
                    self.renderAdjustmentChangesShort();
                });
                var hash = model.get('item').get('uuid') + '_' + model.get('expiration');
                $('.actual-quantity').forceNumericOnly();
                if (hash in self.itemStockDetails) {
                	var actual_quantity_id = '#actual-quantity-' + hash;
                    $(actual_quantity_id).val(this.itemStockDetails[hash].get('actualQuantity'));
                }
            },

            showProcessingDialog: function() {
                $('.cancel').prop('disabled', true);
                $('.submit').prop('disabled', true);

                $('#processingDialog').dialog({
                    dialogClass: "no-close",
                    title: "Processing Operation",
                    draggable: false,
                    resizable: false,
                    modal: true,
                    width: 350
                });
            },

            hideProcessingDialog: function() {
                $('.cancel').prop('disabled', false);
                $('.submit').prop('disabled', false);

                $('#processingDialog').dialog("close");
            },

            save: function() {
                var $operationNumber = "";
                if ($('.isOperationNumberAutoGenerated').val() != 'true') {
                    $operationNumber = prompt(openhmis.getMessage('openhmis.inventory.stocktake.prompt.operationNumber'));
                    if ($operationNumber == null || $operationNumber == "") {
                        alert(openhmis.getMessage('openhmis.inventory.stocktake.error.operationNumber'));
                        return;
                    }
                }
                this.showProcessingDialog();
                var inventoryStockTake = new openhmis.InventoryStockTake();
                var itemStockDetailsArray = this.convertToArray(this.itemStockDetails);
                inventoryStockTake.set("operationNumber", $operationNumber);
                inventoryStockTake.set("stockroom", this.stockroom);
                inventoryStockTake.set("itemStockSummaryList", itemStockDetailsArray);

                var self = this;
                inventoryStockTake.save(null, {
                    success: function(stockTakeDetails, resp) {
                        self.hideProcessingDialog();
                        $(location).attr('href','inventory.form');
                    },
                    error: function(stockTakeDetails, resp) {
                        self.hideProcessingDialog();
                        openhmis.error(resp);
                    }
                });
            },

            render: function() {
                openhmis.GenericSearchableListView.prototype.render.call(this);
                this.renderAdjustmentChangesShort();
            },

            updateGlobalStockTakeChangeCounter: function() {
                openhmis.StockTakeChangeCounter = Object.keys(this.itemStockDetails).length;
            },

            renderAdjustmentChangesShort: function() {
                $('#stockTakeDetailMessages').empty();
                if(Object.keys(this.itemStockDetails).length > 0) {
                    $('#stockTakeDetailMessages').append('<div id="message">Changes made: ' + Object.keys(this.itemStockDetails).length +
                    		' <a id="show-details">Show Details</a></div><div id="render-detail"></div>');
                    $('#submitStockTake').show();
                } else {
                    $('#stockTakeDetailMessages').append('<div id="message">No changes made yet</div>');
                    $('#submitStockTake').hide();
                }
            },

            toggleAdjustmentChangesDetail: function() {
                var itemStockDetailsArray = this.convertToArray(this.itemStockDetails);
                itemStockDetailsArray.sort(function(a, b){
                    if(a.get('item').get('name') < b.get('item').get('name')) return -1;
                    if(a.get('item').get('name') > b.get('item').get('name')) return 1;
                    return 0;
                });
                this.stockTakeDetailsView.model.models = itemStockDetailsArray;
                if($('#adjustmentList').length) {
                    $('#show-details').text('Show Details');
                    $('#adjustmentList').remove();
                } else {
                    $('#show-details').text('Hide Details');
                    $('#render-detail').append('<div id="adjustmentList"></div>');
                    $('#adjustmentList').append(this.stockTakeDetailsView.el);
                    this.stockTakeDetailsView.render();
                }
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
                'change .actual-quantity' : 'changeItemStockDetail',
            },

            changeItemStockDetail: function(event) {
                var inputValue = $(event.currentTarget).val();
                if (inputValue < 0) {
                    inputValue = 0;
                    $(event.currentTarget).val(inputValue);
                };
                this.model.set('actualQuantity', parseInt(inputValue));
                this.trigger('quantityChange', this);
            },
        });

        openhmis.StockTakeAdjustmentsList = openhmis.GenericListView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/inventoryStockTake.html',
            tmplSelector: '#stockTakeAdjustments-list',

            render: function(extraContext) {
                openhmis.GenericListView.prototype.render.call(this, extraContext);
            }
        });

        openhmis.InventoryStockTakeListDetailItemView = openhmis.GenericListItemView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/inventoryStockTake.html',
            tmplSelector: '#inventory-stock-take-list-detail-item',
        });

        return openhmis;
    }
);
