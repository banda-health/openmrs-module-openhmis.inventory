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
    	openhmis.StockroomDetailSearchList = openhmis.GenericSearchableListView.extend({
		    tmplFile: openhmis.url.inventoryBase + 'template/stockroom.html',
		    tmplSelector: '#stockroom-list',

	    });

    	openhmis.StockroomDetailItemSearchList = openhmis.StockroomDetailSearchList.extend({
    		initialize: function(options) {
		    	openhmis.StockroomDetailSearchList.prototype.initialize.call(this, options);
		    	this.itemDetailsTemplate = this.getTemplate(openhmis.url.inventoryBase + "template/stockroom.html", '#item-detail-template');
		    },

		    onItemSelected: function(view) {
		    	var item = view.model.get("item");
		    	this.itemDetails = view.model.get("details");
		    	this.tableRowString = "";
		    	for (var i in this.itemDetails) {
		    		var detail = this.itemDetails[i];
		    		var batchOperationNumber = detail.get("batchOperation") != null ? detail.get("batchOperation").operationNumber : "No Batch";
		    		var date = detail.get("expiration") != null ? openhmis.dateFormat( detail.get("expiration")) : "No Expiration"

		    		this.tableRowString +=  "<tr class='" + "'><td class='field-batchOperation'>" + batchOperationNumber +
		    								"</td><td class='field-expiration numeric'>" + date +
		    								"</td><td class='field-quantity numeric'>" + detail.get("quantity") + "</td></tr>";
		    	}
		    	var self = this;
		    	$('#itemDetailsTable').remove();
		    	$('#itemDetailsDialog').append(this.itemDetailsTemplate({
		    			details: self.tableRowString
		    		})).dialog({
	                    dialogClass: "no-close",
	                    title: "Stock Details for " + item.get("name"),
	                    draggable: false,
	                    resizable: false,
	                    modal: true,
	                    width: 500,
                });
		    	openhmis.StockroomDetailSearchList.prototype.onItemSelected(this, view);
		    }
	    });

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

		        this.itemsView = new openhmis.StockroomDetailItemSearchList({
			        model: new openhmis.GenericCollection([], {
				        model: openhmis.ItemStock
			        }),
			        showRetiredOption: false,
			        showRetired: true,
			        listFields: ['item', 'quantity'],
			        searchView: openhmis.ByNameSearchView
		        });
		        this.operationsView = new openhmis.StockroomDetailSearchList({
			        model: new openhmis.GenericCollection([], {
				        model: openhmis.Operation
			        }),
			        showRetiredOption: false,
			        showRetired: true,
			        listFields: ['dateCreated', 'instanceType', 'operationNumber', 'status'],
			        searchView: openhmis.SearchByOperationItemView
		        });
		        this.transactionsView = new openhmis.StockroomDetailSearchList({
			        model: new openhmis.GenericCollection([], {
				        model: openhmis.OperationTransaction
			        }),
			        showRetiredOption: false,
			        showRetired: true,
			        listFields: ['dateCreated', 'operation', 'item', 'batchOperation',  'expiration', 'quantity'],
			        searchView: openhmis.SearchByTransactionItemView
		        });

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
		        this.$el.addClass('footer-padding');
	        },

	        edit: function(model) {
		        // TODO: Fix this crappy copy-paste hack to hide the details when another tab is selected

		        this.model = model;
		        var self = this;
		        this.model.fetch({
			        success: function(model, resp) {
				        self.render();
				        $('#addLink').hide();
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
		        options.queryString = openhmis.addQueryStringParameter(options.queryString, "stockroom_uuid=" + this.model.id);
	        },

	        save: function() {
	        	//trigger POST only for stockroom details update
	        	if (this.selectedTab === 0 || this.selectedTab === null) {
	        		openhmis.GenericAddEditView.prototype.save.call(this);
	        	}
	        },

	        beginAdd: function() {
		        openhmis.GenericAddEditView.prototype.beginAdd.call(this);
		        $('#addLink').hide();
		        $('#detailTabList').hide();
	        },

	        cancel: function() {
	        	openhmis.GenericAddEditView.prototype.cancel.call(this);
				$('#addLink').show();
			},

	        activateTab: function(event, ui) {
				this.selectedTab = ui.newTab.index();
	        }
        });

	   return openhmis;
    }
);