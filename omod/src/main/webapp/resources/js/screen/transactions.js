curl(
	{ baseUrl: openhmis.url.resources },
	[
		openhmis.url.backboneBase + 'js/lib/jquery',
		openhmis.url.backboneBase + 'js/openhmis',
		openhmis.url.backboneBase + 'js/lib/backbone-forms',
		openhmis.url.inventoryBase + 'js/model/stockRoom',
		openhmis.url.inventoryBase + 'js/model/transaction',
		openhmis.url.inventoryBase + 'js/view/transaction',
		openhmis.url.backboneBase + 'js/view/generic'
	],
	function($, openhmis) {
		$(function() {
			var stockRoomList = $("#stockRoomList");
			var stockRoomInfo = $("#stockRoomInfo");
			var stockRoomEdit = $("#stockRoomEdit");

			// Display current stock rooms into list
			openhmis.startAddEditScreen(openhmis.StockRoom, {
				listFields: ['name', 'description'],
				listElement: stockRoomList,
				addEditViewType: openhmis.StockRoomDetailView,
				addEditElement: stockRoomInfo
			});

			var collection = new openhmis.GenericCollection([], {
				url: openhmis.Transaction.prototype.meta.restUrl,
				model: openhmis.Transaction
			});
			var txView = new openhmis.TransactionView({ collection: collection });
			txView.setElement($('#txDialog'));
		});
	}
);