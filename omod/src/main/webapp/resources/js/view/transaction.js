define(
	[
		openhmis.url.backboneBase + 'js/view/generic',
		openhmis.url.inventoryBase + 'js/model/transaction',
		'link!' + openhmis.url.inventoryBase + 'css/style.css'
	],
	function(openhmis) {
		openhmis.StockRoomDetailList = openhmis.GenericListView.extend({
			tmplFile: openhmis.url.inventoryBase + 'template/stockRoom.html',
			tmplSelector: '#stockRoom-list'
		});

		return openhmis;
	}
);