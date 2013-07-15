define(
    [
        openhmis.url.backboneBase + 'js/view/generic',
	    openhmis.url.inventoryBase + 'js/model/transaction',
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

		        this.transactionsView = new openhmis.StockRoomDetailList({
			        model: new openhmis.GenericCollection([], {
				        model: openhmis.Transaction
			        }),
			        showRetiredOption: false,
			        showRetired: true,
			        listFields: ['dateCreated', 'transactionNumber', 'status', 'transactionType']
		        });

		        this.itemsView = new openhmis.StockRoomDetailList({
			        model: new openhmis.GenericCollection([], {
				        model: openhmis.StockRoomItem
			        }),
			        showRetiredOption: false,
			        showRetired: true,
			        listFields: ['item', 'quantity', 'expiration', 'importTransaction']
		        });

		        this.transactionsView.on("fetch", this.fetch);
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

			        this.transactionsView.fetch(null);
			        this.itemsView.fetch(null);

			        var transactions = $("#transactions");
			        transactions.append(this.transactionsView.el);
			        var items = $("#items");
			        items.append(this.itemsView.el);
			    } else {
			        tabs.hide();
		        }
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

	    openhmis.TransactionView = openhmis.GenericAddEditView.extend({
		    className: 'txDialog',

		    initialize: function(options) {
			    this.events = _.extend(this.events, {
				    'click a.createTxLink': 'createTransaction',
				    'click button.submitTransaction': 'saveTransaction'
			    });

			    openhmis.GenericAddEditView.prototype.initialize.call(this, options);
		    },

		    createTransaction: function() {
			    this.currentTx = new openhmis.Transaction();
			    var txDialog = $('#txDialog');
			    txDialog.empty();

			    this.currentTxForm = openhmis.GenericAddEditView.prototype.prepareModelForm.call(this, this.currentTx);
			    txDialog.prepend(this.currentTxForm.el);
			    txDialog.dialog({
				    modal: true,
				    width: 500,
				    buttons: [{ text: 'Submit', click: function() { this.saveTransaction() }}],
				    title: "Create New Transaction"
			    });
		    },
		    editTransaction: function(tx) {
			    this.currentTx = tx;
			    var txDialog = $('#txDialog');
			    txDialog.empty();

			    this.currentTxForm = openhmis.GenericAddEditView.prototype.prepareModelForm.call(this, this.currentTx);
			    txDialog.prepend(this.currentTxForm.el);
			    txDialog.dialog({
				    modal: true,
				    width: 500,
				    buttons: { class: 'submitTransaction', text: 'Submit' },
				    title: "Edit Transaction"
			    });
		    },

		    saveTransaction: function(event) {
			    if (event) event.preventDefault();

			    var errors = this.currentTxForm.commit();
			    if (errors) return;

			    var view = this;
			    this.currentTx.save(null, {
				    success: function(model, resp) {
					    if (model.collection === undefined) {
						    view.collection.add(model);
					    }

					    model.trigger("sync");
					    $('#txDialog').close();
				    },
				    error: function(model, resp) { openhmis.error(resp); }
			    });
		    }
	    });

        return openhmis;
    }
);