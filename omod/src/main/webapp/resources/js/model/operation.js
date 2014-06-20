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
	    openhmis.url.backboneBase + 'js/model/user',
	    openhmis.url.inventoryBase + 'js/model/stockroom',
	    openhmis.url.inventoryBase + 'js/view/editors'
    ],
    function(openhmis, __) {
	    openhmis.OperationType = openhmis.GenericModel.extend({
		    meta: {
			    name: __("Operation Type"),
			    namePlural: __("Operation Types"),
			    openmrsType: 'metadata',
			    restUrl: openhmis.url.inventoryModelBase + 'stockOperationType'
		    },

		    schema: {
			    name: { type: 'Text' }
		    },

			validate: function(attrs, options) {
			    if (!attrs.name) return { name: __("A name is required.") };
			    return null;
		    },

		    toString: function() {
			    if (this.get("name")) {
				    return this.get("name");
			    }

			    return openhmis.GenericModel.prototype.toString.call(this);
		    }
	    });

	    openhmis.ReservedTransaction = openhmis.GenericModel.extend({
		    meta: {
			    name: __("Reservation Transaction"),
			    namePlural: __("Reservation Transactions"),
			    openmrsType: 'metadata',
			    restUrl: openhmis.url.inventoryModelBase + 'reservationTransaction'
		    },

		    schema: {
			    item: { type: 'NestedModel', model: openhmis.Item, objRef: true },
			    quantity: { type: 'BasicNumber' },
			    expiration: {
				    type: 'Date',
				    format: openhmis.dateFormatLocale
			    },
			    batchOperation: { type: 'NestedModel', model: openhmis.Operation, objRef: true },
			    calculatedExpiration: {type: 'checkbox'},
			    calculatedBatch: {type: 'checkbox'},
			    available: { type: 'checkbox' }
		    }
	    });

	    openhmis.OperationTransaction = openhmis.GenericModel.extend({
		    meta: {
			    name: __("Operation Transaction"),
			    namePlural: __("Operation Transactions"),
			    openmrsType: 'metadata',
			    restUrl: openhmis.url.inventoryModelBase + 'stockOperationTransaction'
		    },

		    schema: {
			    item: { type: 'NestedModel', model: openhmis.Item, objRef: true },
			    quantity: { type: 'BasicNumber' },
			    expiration: {
				    type: 'Date',
				    format: openhmis.dateFormatLocale
			    },
			    batchOperation: { type: 'NestedModel', model: openhmis.Operation, objRef: true },
			    calculatedExpiration: {type: 'checkbox'},
			    calculatedBatch: {type: 'checkbox'},
			    stockroom: {
				    type: 'StockroomSelect',
				    options: new openhmis.GenericCollection(null, {
					    model: openhmis.Stockroom,
					    url: openhmis.url.inventoryModelBase + '/stockroom'
				    }),
				    objRef: true
			    },
			    patient: { type: 'Object', model: openhmis.Patient, objRef: true },
                institution: { type: 'Object', model: openhmis.Institution, objRef: true}
		    }
	    });

	    openhmis.Operation = openhmis.GenericModel.extend({
            meta: {
                name: __("Operation"),
                namePlural: __("Operations"),
                openmrsType: 'metadata',
	            restUrl: openhmis.url.inventoryModelBase + 'stockOperation'
            },

            schema: {
                name: 'Text',
                operationNumber: 'Text',
                status: {
	                type: 'Text',
	                readonly: 'readonly'
                },
	            dateCreated: {
		            type: 'DateTime',
		            readonly: 'readonly',
		            format: openhmis.dateTimeFormatLocale
	            },
	            instanceType: {
		            type: 'OperationTypeSelect',
		            title: 'Operation Type',
		            options: new openhmis.GenericCollection(null, {
			            model: openhmis.OperationType,
			            url: openhmis.url.inventoryModelBase + '/stockOperationType'
		            }),
		            objRef: true
	            },
	            reserved: { type: 'List', itemType: 'NestedModel', model: openhmis.ReservedTransaction },
	            transactions: { type: 'List', itemType: 'NestedModel', model: openhmis.OperationTransaction },
	            source: {
		            type: 'StockroomSelect',
		            options: new openhmis.GenericCollection(null, {
			            model: openhmis.Stockroom,
			            url: openhmis.url.inventoryModelBase + '/stockroom'
		            }),
		            objRef: true
	            },
	            destination: {
		            type: 'StockroomSelect',
		            options: new openhmis.GenericCollection(null, {
			            model: openhmis.Stockroom,
			            url: openhmis.url.inventoryModelBase + '/stockroom'
		            }),
		            objRef: true
	            },
	            patient: { type: 'Object', model: openhmis.Patient, objRef: true }
            },

	        OperationStatus: {
		        PENDING:	"PENDING",
		        CANCELLED:	"CANCELLED",
		        COMPLETED:	"COMPLETED"
	        },

	        initialize: function(attrs, options) {
		        openhmis.GenericModel.prototype.initialize.call(this, attrs, options);

		        if (!this.get("status")) {
			        this.set("status", this.OperationStatus.PENDING);
		        }
	        },

            validate: function(attrs, options) {
                if (!attrs.name) return { name: __("A name is required.") };
                return null;
            },

	        parse: function(resp) {
		        if (resp) {
			        if (resp.instanceType && _.isObject(resp.instanceType)) {
				        resp.instanceType = new openhmis.OperationType(resp.instanceType);
			        }
		        }

		        return resp;
	        },

            toString: function() {
                return this.get('operationNumber');
            }
        });

        return openhmis;
    }
);
