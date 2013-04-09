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
		});
	}
);