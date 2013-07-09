define(
	[
		openhmis.url.backboneBase + 'js/openhmis',
		openhmis.url.backboneBase + 'js/lib/i18n',
		openhmis.url.backboneBase + 'js/model/generic',
		openhmis.url.backboneBase + 'js/model/location'
	],
	function(openhmis, __) {
		openhmis.StockRoom = openhmis.GenericModel.extend({
			meta: {
				name: __("Stock Room"),
				namePlural: __("Stock Rooms"),
				openmrsType: 'metadata',
				restUrl: openhmis.url.inventoryModelBase + 'stockRoom'
			},
			
			schema: {
				name: { type: 'Text' },
				description: { type: 'Text' },
				location: {
					type: 'LocationSelect',
					options: new openhmis.GenericCollection(null, {
						model: openhmis.Location,
						url: 'v1/location'
					}),
					objRef: true
				}
			},
			
			validate: function(attrs, options) {
				if (!attrs.name) return { name: __("A name is required.") };
				return null;
			},
			
			toString: function() {
				return this.get('name');
			}
		});
		
		return openhmis;
	}
);
