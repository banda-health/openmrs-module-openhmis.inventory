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
                this.itemStockDetails = [];
                this.searchView.on('resetItemStockAdjustments', this.resetItemStockAdjustments);
                this.itemView.on('quantityChange', this);
            },

            resetItemStockAdjustments: function() {
            	console.log('resetItemStockAdjustments');
            	this.itemStockDetails = []
            },

            save: function () {
                console.log("save");
            }
        });

        openhmis.InventoryStockTakeListItemView = openhmis.GenericListItemView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/inventoryStockTake.html',
            tmplSelector: '#inventory-stock-take-list-item',

            events: {
                'change .actual-quantity' : 'changeItemStockDetail'
            },

            changeItemStockDetail: function(event) {
                console.log('change');
            },
        });

        return openhmis;
    }
);