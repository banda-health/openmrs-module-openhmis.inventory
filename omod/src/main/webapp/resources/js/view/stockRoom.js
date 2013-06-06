define(
    [
        openhmis.url.backboneBase + 'js/view/generic',
	    openhmis.url.inventoryBase + 'js/model/transaction'
    ],
    function(openhmis) {

        openhmis.ItemFetchable = function(options) {
            if (options) {
                this.stockRoomUuid = options.stockRoomUuid;
            }
        };

        openhmis.ItemFetchable.prototype.getFetchOptions = function() {
            return "stockRoomUuid=" + this.stockRoomUuid;
        };

        openhmis.StockRoomAddEditView = openhmis.GenericAddEditView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/stockRoom.html',
            tmplSelector: '#add-edit-template'
        });

        openhmis.StockRoomDetailView = openhmis.GenericAddEditView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/stockRoom.html',
            tmplSelector: '#detail-template',

	        initialize: function(options) {
		        openhmis.GenericAddEditView.prototype.initialize.call(this, options);
		        this.transactionsView = new openhmis.GenericListView({
			        model: new openhmis.GenericCollection([], {
				        model: openhmis.Transaction
			        }),
			        showRetiredOption: false,
			        listFields: ['dateCreated', 'transactionNumber', 'status', 'transactionType']
		        });
	        },

	        render: function() {
		        openhmis.GenericAddEditView.prototype.render.call(this);

		        var tabs = $("#detailTabs");
		        if (this.model.id) {
			        tabs.tabs();
			        tabs.show();

			        this.transactionsView.model.search("stock_room_uuid=" + this.model.id);
			        var transactions = $("#transactions");
			        transactions.append(
				        this.transactionsView.el
			        );
			    } else {
			        tabs.hide();
		        }
	        }
        });

        return openhmis;
    }
);