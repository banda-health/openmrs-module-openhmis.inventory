curl(
	{ baseUrl: openhmis.url.resources },
	[
		openhmis.url.backboneBase + 'js/lib/jquery',
		openhmis.url.backboneBase + 'js/openhmis',
		openhmis.url.backboneBase + 'js/lib/backbone-forms',
		openhmis.url.inventoryBase + 'js/model/item',
		openhmis.url.inventoryBase + 'js/model/department',
        openhmis.url.inventoryBase + 'js/model/category',
		openhmis.url.backboneBase + 'js/view/generic',
		openhmis.url.backboneBase + 'js/view/list',
		openhmis.url.backboneBase + 'js/view/editors',
		openhmis.url.inventoryBase + 'js/view/editors',
		openhmis.url.backboneBase + 'js/view/search',
		openhmis.url.inventoryBase + 'js/view/search',
		openhmis.url.inventoryBase + 'js/view/item'
	],
	function($, openhmis) {
		$(function() {
			openhmis.startAddEditScreen(openhmis.Item, {
				listView: openhmis.GenericSearchableListView,
				searchView: openhmis.DepartmentAndNameSearchView,
				addEditViewType: openhmis.ItemAddEditView,
				listFields: ['name', 'department', 'category', 'codes', 'defaultPrice']
			});
		});
	}
);