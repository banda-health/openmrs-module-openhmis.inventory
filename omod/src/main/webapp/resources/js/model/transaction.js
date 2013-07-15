define(
    [
        openhmis.url.backboneBase + 'js/openhmis',
        openhmis.url.backboneBase + 'js/lib/i18n',
        openhmis.url.backboneBase + 'js/model/generic',
	    openhmis.url.backboneBase + 'js/model/user',
	    openhmis.url.inventoryBase + 'js/model/stockRoom',
	    openhmis.url.inventoryBase + 'js/model/transactionType',
	    openhmis.url.inventoryBase + 'js/view/editors'
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
                status: {
	                type: 'Text',
	                readonly: 'readonly'
                },
	            dateCreated: {
		            type: 'DateTime',
		            readonly: 'readonly',
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

	        TransactionStatus: {
		        PENDING:	"PENDING",
		        CANCELLED:	"CANCELLED",
		        COMPLETED:	"COMPLETED"
	        },

	        initialize: function(attrs, options) {
		        openhmis.GenericModel.prototype.initialize.call(this, attrs, options);

		        if (!this.get("status")) {
			        this.set("status", this.TransactionStatus.PENDING);
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
