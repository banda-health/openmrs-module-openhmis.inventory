curl(
	{ baseUrl: openhmis.url.resources },
	[
		openhmis.url.backboneBase + 'js/lib/jquery',
		openhmis.url.backboneBase + 'js/openhmis',
		openhmis.url.backboneBase + 'js/lib/backbone-forms',
		openhmis.url.moduleBase + 'js/model/stockRoom',
		openhmis.url.backboneBase + 'js/view/generic'
	],
	function($, openhmis) {
		$(function() {
			openhmis.startAddEditScreen(openhmis.StockRoom, {
				listFields: ['name', 'description']
			});

            var contentDiv = $('#content');
            contentDiv.append('<div id="stockRoomContent" style="width: 100%;"></div>');

            var stockRoomContentDiv = $('#stockRoomContent');
            stockRoomContentDiv.append('<div id="stockRoomList" style="width: 60%;"></div>');
            stockRoomContentDiv.append('<div id="stockRoomInfo" style="width: 40%; float: right"></div>');

            var leftDiv = $('#stockRoomList');
            var rightDiv = $('#stockRoomInfo');

            leftDiv.append($('#existing-form'));
		});
	}
);