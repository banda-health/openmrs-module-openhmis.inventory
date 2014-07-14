define(
	[
		openhmis.url.backboneBase + 'js/view/generic',
        openhmis.url.backboneBase + 'js/view/openhmis',
		openhmis.url.inventoryBase + 'js/model/operation',
		openhmis.url.inventoryBase + 'js/model/stockroom',
		'link!' + openhmis.url.inventoryBase + 'css/style.css'
	],
	function(openhmis) {
		openhmis.OperationTypeEditView = openhmis.CustomizableInstanceTypeAddEditView.extend({
			tmplFile: openhmis.url.inventoryBase + 'template/operation.html',
			tmplSelector: '#detail-template'
		});

        openhmis.MyOperationListView = openhmis.GenericSearchableListView.extend({
            fetch: function(options, sender) {
                options = options ? options : {};

                // The 'my' query is set to return the list of user operations on the operation resource
                options.queryString = openhmis.addQueryStringParameter(options.queryString, "q=my");

                openhmis.GenericListView.prototype.fetch.call(this, options, sender);
            }
        });

        openhmis.MyOperationEditView = openhmis.GenericAddEditView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/operation.html',
            tmplSelector: '#detail-template'
        });

		return openhmis;
	}
);