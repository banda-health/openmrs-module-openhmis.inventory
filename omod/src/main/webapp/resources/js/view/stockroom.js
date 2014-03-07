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
	    openhmis.url.inventoryBase + 'js/model/stockroom',
	    'link!' + openhmis.url.inventoryBase + 'css/style.css'
    ],
    function(openhmis) {
	    openhmis.StockroomDetailList = openhmis.GenericListView.extend({
		    tmplFile: openhmis.url.inventoryBase + 'template/stockroom.html',
		    tmplSelector: '#stockroom-list'
	    });

 	    openhmis.StockroomDetailView = openhmis.GenericAddEditView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/stockroom.html',
            tmplSelector: '#detail-template',
	        titleSelector: '#detailTabs',
	        formSelector: '#stockroomDetails',
	        selectedTab: null,
	        currentTx: null,
	        currentTxForm: null,

	        initialize: function(options) {
		        openhmis.GenericAddEditView.prototype.initialize.call(this, options);

		        this.itemsView = new openhmis.StockroomDetailList({
			        model: new openhmis.GenericCollection([], {
				        model: openhmis.ItemStock
			        }),
			        showRetiredOption: false,
			        showRetired: true,
			        listFields: ['item', 'quantity']
		        });
		        this.operationsView = new openhmis.StockroomDetailList({
			        model: new openhmis.GenericCollection([], {
				        model: openhmis.Operation
			        }),
			        showRetiredOption: false,
			        showRetired: true,
			        listFields: ['dateCreated', 'operationNumber', 'status', 'operationType']
		        });
		        this.transactionsView = new openhmis.StockroomDetailList({
			        model: new openhmis.GenericCollection([], {
				        model: openhmis.OperationTransaction
			        }),
			        showRetiredOption: false,
			        showRetired: true,
			        listFields: ['dateCreated', 'item', 'expiration', 'batchOperation', 'quantity']
		        })

		        this.itemsView.on("fetch", this.fetch);
		        this.operationsView.on("fetch", this.fetch);
		        this.transactionsView.on("fetch", this.fetch);
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

			        this.itemsView.fetch(null);
			        this.operationsView.fetch(null);
			        this.transactionsView.fetch(null);

			        var items = $("#items");
			        items.append(this.itemsView.el);
			        var operations = $("#operations");
			        operations.append(this.operationsView.el);
			        var transactions = $("#transactions");
			        transactions.append(this.transactionsView.el);
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