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
			var pendingList = $("#stockRoomList");
			var completedList = $("#stockRoomInfo");

			$('#detailTabs').tabs();

			// Display current stock rooms into list
			openhmis.startAddEditScreen(openhmis.Transaction, {
				listFields: ['name', 'number', 'to', 'from'],
				listElement: pendingList
			});
		});
	}
);