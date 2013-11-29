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
	    openhmis.url.inventoryBase + 'js/model/operation',
	    openhmis.url.inventoryBase + 'js/model/stockRoomItem',
	    'link!' + openhmis.url.inventoryBase + 'css/style.css'
    ],
    function(openhmis) {
	    openhmis.StockRoomDetailList = openhmis.GenericListView.extend({
		    tmplFile: openhmis.url.inventoryBase + 'template/stockRoom.html',
		    tmplSelector: '#stockRoom-list'
	    });

 	    openhmis.StockRoomDetailView = openhmis.GenericAddEditView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/stockRoom.html',
            tmplSelector: '#detail-template',
	        titleSelector: '#detailTabs',
	        formSelector: '#stockRoomDetails',
	        selectedTab: null,
	        currentTx: null,
	        currentTxForm: null,

	        initialize: function(options) {
		        openhmis.GenericAddEditView.prototype.initialize.call(this, options);

		        this.operationsView = new openhmis.StockRoomDetailList({
			        model: new openhmis.GenericCollection([], {
				        model: openhmis.Operation
			        }),
			        showRetiredOption: false,
			        showRetired: true,
			        listFields: ['dateCreated', 'operationNumber', 'status', 'operationType']
		        });

		        this.itemsView = new openhmis.StockRoomDetailList({
			        model: new openhmis.GenericCollection([], {
				        model: openhmis.ItemStock
			        }),
			        showRetiredOption: false,
			        showRetired: true,
			        listFields: ['item', 'quantity']
		        });

		        this.operationsView.on("fetch", this.fetch);
		        this.itemsView.on("fetch", this.fetch);
	        },

	        render: function() {
		        openhmis.GenericAddEditView.prototype.render.call(this);

		        var tabs = $("#detailTabs");
		        if (this.model.id) {
			        if (this.selectedTab) {
				        tabs.tabs({
					        active: this.selectedTab,
					        activate: this.activateTab
				        });
			        } else {
				        tabs.tabs({
					        activate: this.activateTab
				        });
			        }
			        tabs.show();
			        $('#detailTabList').show();

			        this.operationsView.fetch(null);
			        this.itemsView.fetch(null);

			        var transactions = $("#transactions");
			        transactions.append(this.operationsView.el);
			        var items = $("#items");
			        items.append(this.itemsView.el);
			    } else {
			        tabs.hide();
		        }
	        },

	        edit: function(model) {
		        // TODO: Fix this crappy copy-paste hack to hide the details when another tab is selected

		        this.model = model;
		        var self = this;
		        this.model.fetch({
			        success: function(model, resp) {
				        self.render();
				        $(self.titleEl).show();
				        self.modelForm = self.prepareModelForm(self.model);
				        $(self.formEl).prepend(self.modelForm.el);

				        if (this.selectedTab == 0) {
				            $(self.formEl).show();
				        }

				         $(self.retireVoidPurgeEl).show();
				        $(self.formEl).find('input')[0].focus();
			        },
			        error: openhmis.error
		        });
	        },

	        fetch: function(options) {
		        options.queryString = openhmis.addQueryStringParameter(options.queryString, "stock_room_uuid=" + this.model.id);
	        },

	        beginAdd: function() {
		        openhmis.GenericAddEditView.prototype.beginAdd.call(this);

		        $('#detailTabList').hide();
	        },

	        activateTab: function(event, ui) {
				this.selectedTab = ui.newTab.index();
	        }
        });

	   return openhmis;
    }
);