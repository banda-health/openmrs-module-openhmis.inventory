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
	        selectedTab: null,
	        titleSelector: '#detailTabs',
	        formSelector: '#stockRoomDetails',

	        initialize: function(options) {
		        this.events = _.extend(this.events, {
			        'click a.createTxLink': 'createTransaction'
		        });

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

	        activateTab: function(event, ui) {
				this.selectedTab = ui.newTab.index();
	        },

	        createTransaction: function() {
				var tx = new openhmis.Transaction();
		        var txDialog = $('#txDialog');

		        var txForm = openhmis.GenericAddEditView.prototype.prepareModelForm.call(this, tx);
		        txDialog.prepend(txForm.el);
		        txDialog.dialog("open");
	        }
        });

        return openhmis;
    }
);