define(
    [
        openhmis.url.backboneBase + 'js/view/generic'
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

	        edit: function(model) {
		        openhmis.GenericAddEditView.prototype.edit.call(this, model);

		        var tabs = $("#detailTabs");
		        tabs.tabs();
		        tabs.show();
	        }
        });

        return openhmis;
    }
);