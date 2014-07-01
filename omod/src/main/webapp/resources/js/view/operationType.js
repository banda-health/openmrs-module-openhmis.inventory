define(
	[
		openhmis.url.backboneBase + 'js/view/generic',
		openhmis.url.inventoryBase + 'js/model/operation',
		openhmis.url.inventoryBase + 'js/model/stockroom',
		'link!' + openhmis.url.inventoryBase + 'css/style.css'
	],
	function(openhmis) {
		openhmis.OperationTypeDetailView = openhmis.GenericAddEditView.extend({
			tmplFile: openhmis.url.inventoryBase + 'template/operationType.html',
			tmplSelector: '#detail-template'
		});

		return openhmis;
	}
);