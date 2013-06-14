define(
    [
        openhmis.url.backboneBase + 'js/openhmis',
        openhmis.url.backboneBase + 'js/lib/i18n',
        openhmis.url.backboneBase + 'js/model/generic',
	    openhmis.url.backboneBase + 'js/model/user',
	    openhmis.url.inventoryBase + 'js/model/stockRoom',
	    openhmis.url.inventoryBase + 'js/model/transactionType'
    ],
    function(openhmis, __) {
        openhmis.Transaction = openhmis.GenericModel.extend({
            meta: {
                name: __("Transaction"),
                namePlural: __("Transactions"),
                openmrsType: 'metadata',
                restUrl: openhmis.url.inventoryModelBase + 'stockRoomTransaction'
            },

            schema: {
                name: 'Text',
                transactionNumber: 'Text',
                status: 'Text',
				createdBy: {
					type: 'UserSelect',
					options: new openhmis.GenericCollection(null, {
						model: openhmis.User,
						url: 'v1/user'
					}),
					objRef: true
				},
	            dateCreated: {
		            type: 'DateTime',
		            format: openhmis.dateTimeFormatLocale
	            },
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

	        parse: function(resp) {
		        if (resp) {
			        if (resp.transactionType && _.isObject(resp.transactionType)) {
				        resp.transactionType = new openhmis.TransactionType(resp.transactionType);
			        }

			        /*if (resp.dateCreated) {
				        resp.dateCreated = new Date(resp.dateCreated).toLocaleString();
			        }*/
		        }

		        return resp;
	        },

            toString: function() {
                return this.get('transactionNumber');
            }
        });

        return openhmis;
    }
);
