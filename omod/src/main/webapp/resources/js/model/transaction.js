define(
    [
        openhmis.url.backboneBase + 'js/openhmis',
        openhmis.url.backboneBase + 'js/lib/i18n',
        openhmis.url.backboneBase + 'js/model/generic'
    ],
    function(openhmis, __) {
        openhmis.Transaction = openhmis.GenericModel.extend({
            meta: {
                name: __("Transaction"),
                namePlural: __("Transactions"),
                openmrsType: 'metadata',
                restUrl: openhmis.url.inventoryModelBase + 'transaction'
            },

            schema: {
                name: 'Text',
                number: 'Text',
                status: 'Text',
				createdBy: {
					type: 'UserSelect',
					options: new openhmis.GenericCollection(null, {
						model: openhmis.User,
						url: 'v1/user'
					}),
					objRef: true
				},
	            createdOn: 'DateTime',
	            transactionType: {
		            type: 'TransactionTypeSelect',
		            options: new openhmis.GenericCollection(null, {
			            model: openhmis.TransactionType,
			            url: openhmis.url.inventoryModelBase + '/transactionType'
		            }),
		            objRef: true
	            },
	            from: {
		            type: 'StockRoomSelect',
		            options: new openhmis.GenericCollection(null, {
			            model: openhmis.StockRoom,
			            url: openhmis.url.inventoryModelBase + '/stockRoom'
		            }),
		            objRef: true
	            },
	            to: {
		            type: 'StockRoomSelect',
		            options: new openhmis.GenericCollection(null, {
			            model: openhmis.StockRoom,
			            url: openhmis.url.inventoryModelBase + '/stockRoom'
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
