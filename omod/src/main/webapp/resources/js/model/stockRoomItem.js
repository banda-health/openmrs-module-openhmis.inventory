/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
define(
    [
        openhmis.url.backboneBase + 'js/openhmis',
        openhmis.url.backboneBase + 'js/lib/i18n',
        openhmis.url.backboneBase + 'js/model/generic',
	    openhmis.url.inventoryBase + 'js/model/item',
	    openhmis.url.inventoryBase + 'js/model/stockRoom',
	    openhmis.url.inventoryBase + 'js/model/transaction'
    ],
    function(openhmis, __) {
        openhmis.StockRoomItem = openhmis.GenericModel.extend({
            meta: {
                name: __("Stock Room Item"),
                namePlural: __("Stock Room Items"),
                openmrsType: 'metadata',
                restUrl: openhmis.url.inventoryModelBase + 'stockRoomItem'
            },

            schema: {
                quantity: {
	                type: 'BasicNumber'
                },
	            expiration: {
		            type: 'Date',
		            format: openhmis.dateFormatLocale
	            },
	            item: {
		            type: 'ItemSelect',
		            options: new openhmis.GenericCollection(null, {
			            model: openhmis.Item,
			            url: openhmis.url.inventoryModelBase + '/item'
		            }),
		            objRef: true
	            },
	            stockRoom: {
		            type: 'StockRoomSelect',
		            options: new openhmis.GenericCollection(null, {
			            model: openhmis.StockRoom,
			            url: openhmis.url.inventoryModelBase + '/stockRoom'
		            }),
		            objRef: true
	            },
	            importTransaction: {
		            type: 'TransactionSelect',
		            options: new openhmis.GenericCollection(null, {
			            model: openhmis.Transaction,
			            url: openhmis.url.inventoryModelBase + '/stockRoomTransaction'
		            }),
		            objRef: true
	            }
            },

            validate: function(attrs, options) {
                if (!attrs.quantity) return { quantity: __("An item quantity is required.") };
	            if (!attrs.item || !attrs.item.id) return { item: __("Please choose an item") };

                return null;
            },

	        parse: function(resp) {
		        if (resp) {
			        if (resp.item && _.isObject(resp.item)) {
				        resp.item = new openhmis.Item(resp.item);
			        }
			        if (resp.stockRoom && _.isObject(resp.stockRoom)) {
				        resp.stockRoom = new openhmis.StockRoom(resp.stockRoom);
			        }
			        if (resp.importTransaction && _.isObject(resp.importTransaction)) {
				        resp.importTransaction = new openhmis.Transaction(resp.importTransaction);
			        }

			        /*if (resp.expiration) {
				        resp.expiration = new Date(resp.expiration).toLocaleDateString();
			        }*/
		        }

		        return resp;
	        },

            toString: function() {
                return this.get('name');
            }
        });

        return openhmis;
    }
);
