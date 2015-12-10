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
        openhmis.url.inventoryBase + 'js/model/stockroom',
        openhmis.url.inventoryBase + 'js/model/institution',
        openhmis.url.inventoryBase + 'js/model/department'
    ],
    function(openhmis, __) {
        openhmis.OperationAttributeType = openhmis.AttributeTypeBase.extend({
			meta: {
				restUrl: openhmis.url.inventoryModelBase + 'stockOperationAttributeType',
				confirmDelete: openhmis.getMessage('openhmis.inventory.operations.delete.confirm.attributeType')
			}
		});

        openhmis.OperationType = openhmis.CustomizableInstanceTypeBase.extend({
		    meta: {
				name: __(openhmis.getMessage('openhmis.inventory.operations.type.name')),
				namePlural: __(openhmis.getMessage('openhmis.inventory.operations.type.namePlural')),
				restUrl: openhmis.url.inventoryModelBase + 'stockOperationType'
		    },

		    attributeTypeClass: openhmis.OperationAttributeType,

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
			    if (!attrs.name) return { name: __(openhmis.getMessage('openhmis.inventory.nameRequiredError')) };
			    return null;
		    },

		    toString: function() {
			    if (this.get("name")) {
				    return this.get("name");
			    }

			    return openhmis.GenericModel.prototype.toString.call(this);
		    }
	    });

        openhmis.OperationAttribute = openhmis.AttributeBase.extend({
            attributeTypeClass: openhmis.OperationAttributeType
        });

        openhmis.TransactionBase = openhmis.GenericModel.extend({
		    initialize: function(attributes, options) {
			    openhmis.GenericModel.prototype.initialize.call(this, attributes, options);

                this.schema.operation = { type: 'NestedModel', model: openhmis.Operation, objRef: true };
                this.schema.item = { type: 'NestedModel', model: openhmis.Item, objRef: true };
			    this.schema.quantity = { type: 'BasicNumber' };
                this.schema.cancelReason = {type: 'Text'},
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
			    name: __(openhmis.getMessage('openhmis.inventory.operations.reservation.name')),
			    namePlural: __(openhmis.getMessage('openhmis.inventory.operations.reservation.namePlural')),
			    openmrsType: 'metadata',
			    restUrl: openhmis.url.inventoryModelBase + 'reservationTransaction'
		    },

			schema: {
				available: { type: 'checkbox' }
			}
	    });

	    openhmis.OperationTransaction = openhmis.TransactionBase.extend({
		    meta: {
			    name: __(openhmis.getMessage('openhmis.inventory.operations.transaction.name')),
			    namePlural: __(openhmis.getMessage('openhmis.inventory.operations.transaction.name')),
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

        openhmis.NewOperation = openhmis.GenericModel.extend({
            meta: {
                name: __(openhmis.getMessage('openhmis.inventory.operations.name')),
                namePlural: __(openhmis.getMessage('openhmis.inventory.operations.namePlural')),
                openmrsType: 'metadata',
                restUrl: openhmis.url.inventoryModelBase + 'stockOperation'
            },

            schema: {},

            OperationStatus: {
                NEW:        "NEW",
                PENDING:	"PENDING",
                CANCELLED:	"CANCELLED",
                COMPLETED:	"COMPLETED",
                ROLLBACK: "ROLLBACK"
            },

            initialize: function(attrs, options) {
                openhmis.GenericModel.prototype.initialize.call(this, attrs, options);

                this.schema.operationNumber = { type: 'Text' };
                this.schema.status = {
                    type: 'Text',
                    readonly: 'readonly',
                    hidden: true
                };
                this.schema.instanceType = {
                    type: 'OperationTypeSelect',
                        title: 'Operation Type',
                        options: new openhmis.GenericCollection(null, {
                            model: openhmis.OperationType,
                            url: openhmis.url.inventoryModelBase + 'stockOperationType',
                            queryString: "v=full"
                    }),
                    objRef: true
                };
                this.schema.operationDate = {
                    type: 'OperationDateEditor',
                    format: openhmis.dateTimeFormatLocale
                };
                this.schema.items = {
                    type: 'List',
                    itemType: 'NestedModel',
                    model: openhmis.OperationItem,
                    hidden: true
                };
                this.schema.source = {
                    type: 'StockroomSelect',
                    options: new openhmis.GenericCollection(null, {
                        model: openhmis.Stockroom,
                        url: openhmis.url.inventoryModelBase + 'stockroom'
                    }),
                    objRef: true
                };
                this.schema.destination = {
                    type: 'StockroomSelect',
                    options: new openhmis.GenericCollection(null, {
                        model: openhmis.Stockroom,
                        url: openhmis.url.inventoryModelBase + 'stockroom'
                    }),
                    objRef: true
                };
                this.schema.patient = {
                    type: 'Object',
                    objRef: true,
                    hidden: true
                };
                this.schema.institution = {
                    type: 'InstitutionSelect',
                    options: new openhmis.GenericCollection(null, {
                        model: openhmis.Institution,
                        url: openhmis.url.inventoryModelBase + 'institution'
                    }),
                    objRef: true
                };
                this.schema.department = {
                    type: 'OptionalDepartmentSelect',
                    options: new openhmis.GenericCollection(null, {
                        model: openhmis.Department,
                        url: openhmis.url.inventoryModelBase + 'department'
                    }),
                    objRef: true
                };
                this.schema.attributes = {
                    hidden: true
                };

                if (!this.get("status")) {
                    this.set("status", this.OperationStatus.NEW);
                }
            },

            parse: function(resp) {
                if (resp) {
                    if (resp.instanceType && _.isObject(resp.instanceType)) {
                        resp.instanceType = new openhmis.OperationType(resp.instanceType);
                    }

                    if (resp.source) {
                        resp.source = new openhmis.Stockroom(resp.source);
                    }
                    if (resp.destination) {
                        resp.destination = new openhmis.Stockroom(resp.destination);
                    }
                    if (resp.institution) {
                        resp.institution = new openhmis.Institution(resp.institution);
                    }
                    if (resp.patient) {
                        resp.patient = new openhmis.Patient(resp.patient);
                    }
                    if (resp.department) {
                        resp.department = new openhmis.Department(resp.department);
                    }
                    if (resp.creator) {
                        resp.creator = new openhmis.User(resp.creator.person);
                    }
                    if (resp.attributes) {
                        resp.attributes = new openhmis.GenericCollection(resp.attributes,
                            { model: openhmis.OperationAttribute }).models;
                    }
                }

                return resp;
            },

            validate: function(goAhead) {
                // By default, backbone validates every time we try try to alter the model.  We don't want to be bothered
                // with this until we care.
                if (goAhead !== true) {
                    return null;
                }

                var errors = [];
                var operationNumber = this.get("operationNumber");
                if (operationNumber === undefined || operationNumber === '') {
                    errors.push({
                        selector: ".field-operationNumber",
                        message: openhmis.getMessage('openhmis.inventory.operations.error.number')
                    });
                }

                if (this.get("instanceType") === undefined) {
                    errors.push({
                        selector: ".field-instanceType",
                        message: openhmis.getMessage('openhmis.inventory.operations.error.type')
                    });
                } else {
                    var operationType = this.get("instanceType");
                    if (operationType.get("hasSource") &&
                        (this.get("source") === undefined || this.get("source").id === "")) {
                        errors.push({
                            selector: ".field-source",
                            message: openhmis.getMessage('openhmis.inventory.operations.error.prefix')+ " " + operationType.get("name") + " " + openhmis.getMessage('openhmis.inventory.operations.required.sourceStockroom')
                        });
                    }
                    if (operationType.get("hasDestination") &&
                        (this.get("destination") === undefined || this.get("destination").id === "")) {
                        errors.push({
                            selector: ".field-destination",
                            message: openhmis.getMessage('openhmis.inventory.operations.error.prefix')+ " " + operationType.get("name") + " " + openhmis.getMessage('openhmis.inventory.operations.required.destinationStockroom')
                        });
                    }
                    if ((operationType.get("hasSource") && operationType.get("hasDestination")) &&
                        (this.get("source").id === this.get("destination").id)) {
                        errors.push({
                            selector: ".field-destination",
                            message: openhmis.getMessage('openhmis.inventory.operations.error.destinationStockroom')
                        });
                    }
                    if (operationType.get("hasRecipient")) {
                        var institution = this.get('institution');
                        var department = this.get('department');
                        var patient = this.get('patient');

                        // Either an institution, department, or patient must be defined
                        if ((!institution || institution.id === "") &&
                            (!department || department.id === "") &&
                            (!patient || patient.id === "")) {
                            errors.push({
                                selector: ".field-institution",
                                message: openhmis.getMessage('openhmis.inventory.operations.error.prefix') + operationType.get("name") + " " + openhmis.getMessage('openhmis.inventory.operations.required.variables')
                            });
                        } else {
                            var defined = 0;
                            defined += institution && institution.id !== "" ? 1 : 0;
                            defined += department && department.id !== "" ? 1 : 0;
                            defined += patient && patient.id !== "" ? 1 : 0;

                            if (defined > 1) {
                                errors.push({
                                    selector: ".field-institution",
                                    message: openhmis.getMessage('openhmis.inventory.operations.error.prefix') + operationType.get("name") + " " + openhmis.getMessage('openhmis.inventory.operations.required.variables')
                                });
                            }
                        }
                    }
                }

                // TODO: Should the operation type user/role check happen here?

                var items = this.get("items");
                if (items === undefined || items.length === 0) {
                    errors.push({
                        selector: ".item-stock",
                        message: openhmis.getMessage('openhmis.inventory.operations.error.itemQuantity'),
                        selectParent: true
                    });
                } else  {
                     var itemError = false;
                     var expiryDateError = false;
                     items.each(function(item) {
                     	if (item.get("quantity") === 0) {
                     		itemError = true;
                     	}
                     	var itemAttribute = item.get("item");
                     	if (itemAttribute != null && itemAttribute.get("hasExpiration") === true && item.get("expiration") === "") {
                     		expiryDateError = true;
                     	}
                     });
                     if (itemError) {
                     	errors.push({
                             selector: "th.field-quantity",
                             message: openhmis.getMessage('openhmis.inventory.operations.error.itemError')
                         });
                     }
                     if (expiryDateError) {
                    	 errors.push({
                    		 selector: "th.field-expiration",
                    		 message: openhmis.getMessage('openhmis.inventory.operations.error.expiryDate')
                    	 });
                     }
                }

                if (errors.length === 0) {
                    return null;
                } else {
                    return errors;
                }

            },

            toString: function() {
                if (this.get("operationNumber")) {
                    return this.get("operationNumber");
                } else {
                    return "Operation";
                }
            }
        });

        openhmis.Operation = openhmis.NewOperation.extend({
            schema: {
                operationNumber: 'Text',
                dateCreated: {
                    type: 'Text',
                    editorAttrs: { disabled: true },
                    format: openhmis.dateTimeFormatLocale
                }
            },

            initialize: function(attrs, options) {
                openhmis.NewOperation.prototype.initialize.call(this, attrs, options);

                // Show the status column
                this.schema.status.hidden = false;
            }
        });

        openhmis.OperationItem = openhmis.ItemStockDetailBase.extend({
            meta: {
                name: __(openhmis.getMessage('openhmis.inventory.operations.time.name')),
                namePlural: __(openhmis.getMessage('openhmis.inventory.operations.time.namePlural')),
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

        return openhmis;
    }
);
