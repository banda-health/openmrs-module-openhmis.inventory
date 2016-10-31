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
        openhmis.url.inventoryBase + 'js/model/item',
    ],
    function(openhmis, __) {
        openhmis.ItemConceptSuggestion = openhmis.GenericModel.extend({
            meta: {
                name: __(openhmis.getMessage('openhmis.inventory.item.concept.suggestion.name')),
                namePlural: __(openhmis.getMessage('openhmis.inventory.item.concept.suggestion.namePlural')),
                openmrsType: 'metadata',
                restUrl: openhmis.url.inventoryModelBase + 'itemConceptSuggestion'
            },

            schema: {
                item: { type: 'NestedModel', model: openhmis.Item, objRef: true },
                conceptName: {type: 'Text'},
                conceptUuid: {type: 'Text'},
                conceptAccepted: {type: 'Checkbox'}
            },
            
            parse: function(resp) {
				if (resp) {
					if (resp.item && _.isObject(resp.item)) {
						resp.item = new openhmis.Item(resp.item);
					}
				}
				return resp;
			},
            
        });

        return openhmis;
    }
);