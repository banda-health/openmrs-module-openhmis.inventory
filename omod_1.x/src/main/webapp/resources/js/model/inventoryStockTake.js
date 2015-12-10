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
        openhmis.url.backboneBase + 'js/model/concept',
        openhmis.url.inventoryBase + 'js/model/stockroom',
    ],
    function(openhmis, __) {
        openhmis.InventoryStockTake = openhmis.GenericModel.extend({
            meta: {
                name: __(openhmis.getMessage('openhmis.inventory.stocktake.name')),
                namePlural: __(openhmis.getMessage('openhmis.inventory.stocktake.namePlural')),
                openmrsType: 'metadata',
                restUrl: openhmis.url.inventoryModelBase + 'inventoryStockTake'
            },

            schema: {
                operationNumber: {type: "text"},
                stockroom: { type: "Object", objRef: true },
                itemStockSummaryList: { type: "List", itemType: "NestedModel", model: openhmis.ItemStockSummary},
            },

        });

        openhmis.ItemStockSummary = openhmis.GenericModel.extend({
            meta: {
            	name: __(openhmis.getMessage('openhmis.inventory.stocksummary.name')),
                namePlural: __(openhmis.getMessage('openhmis.inventory.stocksummary.namePlural')),
                openmrsType: 'metadata',
                restUrl: openhmis.url.inventoryModelBase + 'inventoryStockTakeSummary'
            },

            schema: {
                actualQuantity: { type: "BasicNumber" },
                quantity: { type: "BasicNumber" },
                expiration: { type: "Date", format: openhmis.dateFormat },
                item: { type: "Object", objRef: true },
            },

            parse: function(resp) {
				if (resp) {
					if (resp.item && _.isObject(resp.item)) {
						resp.item = new openhmis.Item(resp.item);
					}

					if (resp.expiration) {
						var date = new Date(resp.expiration);
						resp.expiration = openhmis.dateFormat(date);
					}

				}
				return resp;
			},
        });

        return openhmis;
    }
);