curl(
	{ baseUrl: openhmis.url.resources },
	[
		openhmis.url.backboneBase + 'js/lib/jquery',
		openhmis.url.backboneBase + 'js/openhmis',
		openhmis.url.backboneBase + 'js/lib/backbone-forms',
		openhmis.url.inventoryBase + 'js/model/stockRoom',
        openhmis.url.inventoryBase + 'js/view/stockRoom',
		openhmis.url.backboneBase + 'js/view/generic'
	],
	function($, openhmis) {
		$(function() {
			openhmis.startAddEditScreen(openhmis.StockRoom, {
				listFields: ['name', 'description'],
                listElement: $("#stockRoomList"),
                addEditViewType: openhmis.StockRoomAddEditView,
                addEditElement: $("#stockRoomInfo")
			});

            var stockRoomItemListView = new openhmis.GenericListView();

            stockRoomItemListView,setElement($("#stockRoomList"));
            $("#stockRoomList").append(stockRoomItemListView.render().el);
		});
	}
);