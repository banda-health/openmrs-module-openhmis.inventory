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
		openhmis.url.backboneBase + 'js/model/role',
        openhmis.url.backboneBase + 'js/model/patient',
		openhmis.url.backboneBase + 'js/model/openhmis',
	    openhmis.url.inventoryBase + 'js/model/stockroom'
    ],
    function(openhmis, __) {
		openhmis.OperationAttributeType = openhmis.AttributeTypeBase.extend({
			meta: {
				restUrl: openhmis.url.inventoryModelBase + 'stockOperationAttributeType'
			}
		});

	    openhmis.OperationType = openhmis.CustomizableInstanceTypeBase.extend({
		    meta: {
				name: __("Operation Type"),
				namePlural: __("Operation Types"),
				restUrl: openhmis.url.inventoryModelBase + 'stockOperationType'
		    },

		    attributeType: openhmis.OperationAttributeType,

			schema: {
			    name: { type: 'Text' },
			    description: { type: 'TextArea' },
			    hasSource: {
				    type: 'TrueFalseCheckbox',
				    editorAttrs: { disabled: true }
			    },
			    hasDestination: {
				    type: 'TrueFalseCheckbox',
				    editorAttrs: { disabled: true }
			    },
			    hasRecipient: {
				    type: 'TrueFalseCheckbox',
				    editorAttrs: { disabled: true }
			    },
			    availableWhenReserved: {
				    type: 'TrueFalseCheckbox',
			    editorAttrs: { disabled: true }
			    },
			    user: {
				    type: 'UserSelect',
				    options: new openhmis.GenericCollection(null, {
					    model: openhmis.User,
					    url: 'v1/user'
				    }),
				    objRef: true
			    },
			    role: {
				    type: 'RoleSelect',
				    options: new openhmis.GenericCollection(null, {
					    model: openhmis.Role,
					    url: 'v1/role'
				    }),
				    objRef: true
			    }
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

        openhmis.OperationItem = openhmis.ItemStockDetailBase.extend({
            meta: {
                name: __("Operation Item"),
                namePlural: __("Operation Items"),
                openmrsType: 'metadata',
                restUrl: openhmis.url.inventoryModelBase + 'stockOperationItem'
            },

            schema: {
                operation: {
                    type: 'NestedModel',
                    model: openhmis.Operation,
                    objRef: true
                }
            },



            toString: function() {
                return this.get('item.name');
            }
        });

	    openhmis.TransactionBase = openhmis.GenericModel.extend({
		    initialize: function(attributes, options) {
			    openhmis.GenericModel.prototype.initialize.call(this, attributes, options);

                this.schema.operation = { type: 'NestedModel', model: openhmis.Operation, objRef: true };
                this.schema.item = { type: 'NestedModel', model: openhmis.Item, objRef: true };
			    this.schema.quantity = { type: 'BasicNumber' };
			    this.schema.expiration = { type: 'Date', format: openhmis.dateFormatLocale };
			    this.schema.dateCreated = { type: 'Date', format: openhmis.dateTimeFormatLocale };
			    this.schema.batchOperation = { type: 'NestedModel', model: openhmis.Operation, objRef: true };
			    this.schema.calculatedExpiration = {type: 'checkbox'};
			    this.schema.calculatedBatch = {type: 'checkbox'};
		    },

		    parse: function(resp) {
			    if (resp) {
                    if (resp.operation && _.isObject(resp.operation)) {
                        resp.operation = new openhmis.Operation(resp.operation);
                    }

                    if (resp.item && _.isObject(resp.item)) {
                        resp.item = new openhmis.Item(resp.item);
                    }

                    if (resp.batchOperation && _.isObject(resp.batchOperation)) {
					    resp.batchOperation = new openhmis.Operation(resp.batchOperation);
				    }
			    }

			    return resp;
		    },

            toString: function() {
                var expiration = this.get("expiration");
                var exp = ": ";
                if (expiration) {
                    exp = " (" + openhmis.dateFormatLocale(expiration) + "): ";
                }

                return this.get("item").name + exp + this.get("quantity")
            }
	    });

	    openhmis.ReservedTransaction = openhmis.TransactionBase.extend({
		    meta: {
			    name: __("Reservation Transaction"),
			    namePlural: __("Reservation Transactions"),
			    openmrsType: 'metadata',
			    restUrl: openhmis.url.inventoryModelBase + 'reservationTransaction'
		    },

			schema: {
				available: { type: 'checkbox' }
			}
	    });

	    openhmis.OperationTransaction = openhmis.TransactionBase.extend({
		    meta: {
			    name: __("Operation Transaction"),
			    namePlural: __("Operation Transactions"),
			    openmrsType: 'metadata',
			    restUrl: openhmis.url.inventoryModelBase + 'stockOperationTransaction'
		    },

		    schema: {
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
		    },

		    parse: function(resp) {
			    openhmis.TransactionBase.prototype.parse.call(this, resp);

			    if (resp) {
				    if (resp.stockroom && _.isObject(resp.stockroom)) {
					    resp.stockroom = new openhmis.Stockroom(resp.stockroom);
				    }

				    if (resp.patient && _.isObject(resp.patient)) {
					    resp.patient = new openhmis.Patient(resp.patient);
				    }

				    if (resp.institution && _.isObject(resp.institution)) {
					    resp.institution = new openhmis.Institution(resp.institution);
				    }
			    }

			    return resp;
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
                operationNumber: 'Text',
                status: {
	                type: 'Text',
	                readonly: 'readonly'
                },
	            dateCreated: {
		            type: 'Text',
                    editorAttrs: { disabled: true },
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
                items: { type: 'List', itemType: 'NestedModel', model: openhmis.OperationItem },
	            //reserved: { type: 'List', itemType: 'NestedModel', model: openhmis.ReservedTransaction },
	            //transactions: { type: 'List', itemType: 'NestedModel', model: openhmis.OperationTransaction },
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
	            }
            },

	        OperationStatus: {
		        NEW:        "NEW",
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

            parse: function(resp) {
		        if (resp) {
			        if (resp.instanceType && _.isObject(resp.instanceType)) {
				        resp.instanceType = new openhmis.OperationType(resp.instanceType);
			        }

                    if (resp.reserved) {
                        resp.reserved = new openhmis.GenericCollection(resp.reserved, { model: openhmis.ReservedTransaction }).models;
                    }

                    if (resp.transactions) {
                        resp.transactions = new openhmis.GenericCollection(resp.transactions, { model: openhmis.OperationTransaction }).models;
                    }

                    if (resp.source) {
                        resp.source = new openhmis.Stockroom(resp.source);
                    }
                    if (resp.destination) {
                        resp.destination = new openhmis.Stockroom(resp.destination);
                    }
		        }

		        return resp;
	        },

            toString: function() {
	            if (this.get("operationNumber")) {
		            return this.get("operationNumber");
	            } else {
		            return "Operation";
	            }
            }
        });

        return openhmis;
    }
);
