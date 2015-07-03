define(
	[
        openhmis.url.backboneBase + 'js/lib/jquery',
        openhmis.url.backboneBase + 'js/lib/underscore',
        openhmis.url.backboneBase + 'js/lib/backbone',
        openhmis.url.backboneBase + 'js/view/generic',
        openhmis.url.backboneBase + 'js/view/openhmis',
        openhmis.url.backboneBase + 'js/view/patient',
        openhmis.url.inventoryBase + 'js/model/operation',
        openhmis.url.inventoryBase + 'js/model/stockroom',
        openhmis.url.inventoryBase + 'js/view/stockroom',
        openhmis.url.inventoryBase + 'js/view/editors',
        'link!' + openhmis.url.inventoryBase + 'css/style.css'
    ],
    function($, _, Backbone, openhmis) {
        openhmis.OperationTypeEditView = openhmis.CustomizableInstanceTypeAddEditView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/operation.html',
            tmplSelector: '#detail-template'
        });

        openhmis.MyOperationListView = openhmis.GenericSearchableListView.extend({
            fetch: function(options, sender) {
                options = options ? options : {};

                // The 'my' query is set to return the list of user operations on the operation resource
                options.queryString = openhmis.addQueryStringParameter(options.queryString, "q=my");

                openhmis.GenericListView.prototype.fetch.call(this, options, sender);
            }
        });

        openhmis.OperationItemListItemView = openhmis.GenericListItemView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/operation.html',
            tmplSelector: '#operation-item-list-item'
        });

        openhmis.OperationDetailView = openhmis.GenericAddEditView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/operation.html',
            tmplSelector: '#view-operation-detail',
            titleSelector: '#operationTabs',
            selectedTab: null,
	        currentTx: null,
	        currentTxForm: null,


            events: {
                'click .completeOp': 'completeOperation',
                'click .cancelOp': 'cancelOperation',
                'click .rollbackOp': 'rollbackOperation',
                'click .cancel': 'cancel'
            },

            initialize: function(options) {
                openhmis.GenericAddEditView.prototype.initialize.call(this, options);

                this.itemsView = new openhmis.StockroomDetailList({
                    model: new openhmis.GenericCollection([], {
                        model: openhmis.OperationItem
                    }),
                    showRetiredOption: false,
                    showRetired: true,
                    listFields: ['item', 'quantity', 'batchOperation', 'expiration'],
                    itemView: openhmis.OperationItemListItemView
                });

                this.transactionsView = new openhmis.StockroomDetailList({
                    model: new openhmis.GenericCollection([], {
                        model: openhmis.OperationTransaction
                    }),
                    showRetiredOption: false,
                    showRetired: true,
                    listFields: ['stockroom', 'item', 'batchOperation',  'expiration', 'quantity'],
                    itemView: openhmis.OperationItemListItemView
                });

                this.itemsView.on("fetch", this.fetch);
                this.transactionsView.on("fetch", this.fetch);
            },

        	edit: function(model) {
        		$("#viewOperation").show();
        		$("#newOperationDialog").hide();
        		$("#createOperationLink").hide();
        		$(".addLink").hide();
        		openhmis.GenericAddEditView.prototype.edit.call(this, model);
        	},

        	cancel: function() {
        		openhmis.GenericAddEditView.prototype.cancel.call(this);
        		$("#newOperationDialog").hide();
        		$("#createOperationLink").show();
        		$(".addLink").hide();
        	},

            completeOperation: function() {
                // TODO: Ensure that the current user can complete the operation

                // Post the status change and then reload the model
                //  Using the normal save mechanism can result in issues as it sends the entire object hierarchy which
                //  can result in issues with the REST converters
                this.updateStatus("COMPLETED");
            },

            cancelOperation: function() {
                // TODO: Ensure that the current user can cancel the operation
                if ($('#showOperationCancelReasonField').val() !== 'true') {
                    // Post the status change and then reload the model
                    this.updateStatus("CANCELLED");
                } else {
                    this.model.cancelReason = prompt((openhmis.getMessage('openhmis.inventory.operations.cancel.reason')));
                    if (this.model.cancelReason == null || this.model.cancelReason == "") {
                        alert(openhmis.getMessage('openhmis.inventory.operations.error.cancel.reasonRequired'));
                    } else {
                // Post the status change and then reload the model
                this.updateStatus("CANCELLED");
                    }
                }

            },

            rollbackOperation: function() {
                this.updateStatus("ROLLBACK");
            },

            prepareModelForm: function(model, options) {
              // We don't want to use the backbone form as we're just displaying the operation information here
                return undefined;
            },

            fetch: function(options) {
                options.queryString = openhmis.addQueryStringParameter(options.queryString, "operation_uuid=" + this.model.id);
            },

            render: function() {
                if (this.model.id) {
                    // Create a list of all the operation type attributes and get the value from the operation attributes.
                    //  This is needed so we can view optional attributes that were not defined for the specific operation.
                    var opType = this.model.get('instanceType');

                    var attributeList = [];
                    for (i in opType.get('attributeTypes')) {
                        var type = opType.get('attributeTypes')[i];

                        var name = type.name;
                        var value = this.getAttributeValue(this.model, name);

                        attributeList.push({
                            name: name,
                            value: value,
                            id: type.uuid
                        });
                    }

                    // Make the list of attributes and values accessible from the template via the attributeList field
                    this.model.attributeList = attributeList;
                }

                openhmis.GenericAddEditView.prototype.render.call(this);

                var tabs = $("#operationTabs");
		        if (this.model.id) {
			        if (this.selectedTab) {
				        tabs.tabs({
					        active: this.selectedTab,
					        activate: this.activateTab
				        });
			        } else {
				        tabs.tabs({
					        activate: this.activateTab
				        });
			        }
			        tabs.show();
			        $('#operationTabList').show();

			        this.itemsView.fetch(null);
			        this.transactionsView.fetch(null);

			        var items = $("#operation-items");
			        items.append(this.itemsView.el);
			        var transactions = $("#operation-transactions");
			        transactions.append(this.transactionsView.el);
			    } else {
			        tabs.hide();
		        }
		        this.$el.addClass('footer-padding');

            },

            updateStatus: function(status) {
                this.showProcessingDialog();

                var self = this;

                // Post the status change using the raw ajax request. This just sends up the changed property, status.
                var data = '{"status":"' + status + '"';
                if (this.model.cancelReason) {
                    data += ', "cancelReason": "' + this.model.cancelReason + '"}';
                } else {
                    data += '}';
                }
                $.ajax({
                    type: 'POST',
                    url: this.model.url(),
                        data: data,
                    success: function(data) {
                        // Fetch the updated model
                        self.model.fetch({
                            success: function() {
                                // Once the fetch is complete, sync the changes back to the list and close this edit view
                                self.model.trigger("sync");

                                self.hideProcessingDialog();
                                self.cancel();
                            }
                        });
                    },
                    error: function(model, resp) {
                        self.hideProcessingDialog();

                        openhmis.error(model, resp);
                    },
                    contentType: "application/json",
                    dataType: 'json'
                });
            },

            getAttributeValue: function(model, name) {
                for (i in model.get('attributes')) {
                    var attribute = model.get('attributes')[i];
                    if (attribute.get('attributeType').name === name) {
                        return attribute.get('value');
                    }
                }

                return " ";
            },

            showProcessingDialog: function() {
                $('.cancel').prop('disabled', true);
                $('.submit').prop('disabled', true);

                $('#processingDialog').dialog({
                    dialogClass: "no-close",
                    title: "Processing Operation",
                    draggable: false,
                    resizable: false,
                    modal: true,
                    width: 350
                });
            },

            hideProcessingDialog: function() {
                $('.cancel').prop('disabled', false);
                $('.submit').prop('disabled', false);

                $('#processingDialog').dialog("close");
            }
        });

        openhmis.NewOperationView = openhmis.GenericAddEditView.extend({
            currentOperationType: undefined,

            initialize: function(options) {

                openhmis.GenericAddEditView.prototype.initialize.call(this, options);

                if (options.element) {
                    this.setElement(options.element);
                }

                this.events = _.extend({}, this.events, {
                    'change select[name="instanceType"]': 'instanceTypeChanged',
                    'change select[name="source"]': 'sourceChanged',
                });

                var self = this;
                this.operationTypes = new openhmis.GenericCollection([], { model: openhmis.OperationType });
                this.operationTypes.fetch({
                    success: function(collection, resp) {
                        self.currentOperationType = collection.models[0];

                        if (options.loaded) {
                            options.loaded();
                        }
                    },
                    queryString: "v=full",
                    silent: true
                });
            },

            render: function() {
                this.model.schema.instanceType.view = this;

                openhmis.GenericAddEditView.prototype.render.call(this);

                if (!this.model.id) {
                    // Render the empty operation item stock list
                    this.itemStockView.render();

                    this.itemStockView.setupNewItem();
                }
            },

            save: function(event) {
                this.showProcessingDialog();

                // Load the attributes and set in the model
                var attributes = openhmis.loadAttributes(this, this.$attributes, openhmis.OperationAttribute);
                if (attributes) {
                    this.model.set("attributes", attributes);
                } else if (attributes === false) {
                    // The loadAttributes returns false if there was an error so halt the save if we got that
                    this.hideProcessingDialog();

                    return false;
                }

                // Load the item stock from the sub-view
                if (this.itemStockView && this.itemStockView.model && this.itemStockView.model.models) {
                    // Ensure that the current item is saved
                    if (this.itemStockView.selectedItem) {
                        this.itemStockView.selectedItem.commitForm();
                    }

                    if (!this.model.get("items") || this.model.get("items").length === 0) {
                        this.model.set("items", new openhmis.GenericCollection(this.itemStockView.model.models))
                    }

                    // Check for a Auto/None expiration and set the item attributes accordingly
                    _.each(this.model.get("items").models, function (item) {
                        if (item.get("expiration") === "Auto") {
                            item.set("expiration", undefined);
                            item.set("calculatedExpiration", true)
                        } else if (item.get("expiration") === "None") {
                            item.set("expiration", undefined);
                            item.set("calculatedExpiration", false)
                        } else {
                            item.set("calculatedExpiration", false)
                        }
                    });
                }

                if (this.currentOperationType.get('hasRecipient')) {
                    this.model.set('patient', this.patientView.model);
                }

                // Save the model, hooking into the post-commit "event" to validate the model after it has been loaded
                var self = this;
                openhmis.GenericAddEditView.prototype.save.call(this, event, {
                    postCommit: function() {
                        // Set the instance type to the fully loaded model rather than the basic information loaded by the
                        //  select box.
                        self.model.set("instanceType", self.currentOperationType);

                        var errors = self.model.validate(true);
                        if (errors) {
                        	self.model.set("items", null) ;
                            openhmis.displayErrors(self, errors);
                            self.hideProcessingDialog();
                            return false;
                        }
                    },
                    success: function(model) {
                        self.hideProcessingDialog();
                        self.trigger("save", model);
                    },
                    error: function(model) {
                        self.hideProcessingDialog();
                    }
                });
            },

            cancel: function() {
                openhmis.GenericAddEditView.prototype.cancel.call(this);
                this.itemStockView = undefined;
                $("#createOperationLink").show();
                $(".addLink").hide();
            },

            showForm: function() {
                // Set up the item stock entry view if not already defined
                if (!this.itemStockView) {
                    // Reset the items
                    if (!this.model.get('items')) {
                        this.model.set('items', new openhmis.GenericCollection([], {
                            model: openhmis.ItemStockEntry
                        }));
                    }

                    this.itemStockView = new openhmis.OperationItemStockView({
                        model: this.model.get('items'),
                        id: "itemStock",
                        itemView: openhmis.OperationItemStockItemView,
                        listTitle: 'Operation Items',
                        listFields: ['item', 'quantity', 'expiration', 'batchOperation'],
                        stockroomSelector: 'select[name="source"]',
                        view: this,
                        operation: this.model
                    });
                }

                // Render new operation form
                this.beginAdd();
                if ($('.isOperationNumberAutoGenerated').val() === 'true') {
                    var operationNumberEl = $('input[name=operationNumber]');

                    operationNumberEl.val('WILL BE GENERATED');
                    operationNumberEl.prop('readonly', true);
                    operationNumberEl.addClass('readonly');
                }
                $(".addLink").hide();
                $(".cancel  ").hide();
                $("#createOperationLink").hide();


                // Insert the item stock list after the form but before the buttons
                $("#newOperation").find(".bbf-form").after(this.itemStockView.el);

                // If the patient search already exists remove it and reset the patient search view
                this.$patientSearch = this.$("#patientSearch");
                if (this.$patientSearch.length) {
                    this.$patientSearch.remove();
                }

                // Set up a new patient search selection handler (we don't want to keep the old view)
                this.patientView = new openhmis.PatientView();
                openhmis.doSelectionHandler = this.patientView.takeRawPatient;

                // Create the patient search element
                this.$("form").after("<div id='patientSearch' class='bbf-form'></div>");
                this.$patientSearch = this.$("#patientSearch");

                openhmis.renderPatientSearchFragment(this.$patientSearch);

                // Display the form
                this.$el.show();

                this.patientView.setElement($('#patient-view'));
                this.patientView.render();

                if (this.currentOperationType) {
                    this.updateOperationType(this.currentOperationType);
                }
            },

            beginAdd: function() {
            	$("#viewOperation").hide();
            	$("#createOperationLink").hide();
            	$(".addLink").hide();
            	openhmis.GenericAddEditView.prototype.beginAdd.call(this);
            },

            checkHideItemStock: function() {
                var selectedSource = $('select[name="source"]').val();
                if ($('select[name="source"]').is(':enabled') && (selectedSource == null || selectedSource == "")) {
                	$(".item-stock").hide();
                    if (!$('#itemStockMessage').length) {
                        $(".item-stock").after("<div id='itemStockMessage'><b>You must select a stockroom before selecting items</b></div>");
                    } else {
                    	$("#itemStockMessage").show();
                    }
                } else {
                	$(".item-stock").show();
                	if ($('#itemStockMessage').length) {
                		$("#itemStockMessage").hide();
                	}
                }
            },

            instanceTypeChanged: function(event) {
                var items = this.itemStockView.model.length;

                if (items > 0) {
                    // Confirm operation type change if there are any defined item stock
                    if (!confirm(openhmis.getMessage('openhmis.inventory.operations.confirm.operationTypeChange'))) {
                        // Set the value back to the previous value
                        this.$(event.target).val($.data(event.target, 'current'));

                        return false;
                    }
                }
                // Store the updated operation type as the current value for the select
                $.data(event.target, 'current', this.$(event.target).val());

                if (items > 0) {
                    this.clearItemStock();
                }

                this.updateOperationType($(event.target).val());
            },

            checkInstanceType: function() {
                // Disable operation types that cannot be used by the current user
                var self = this;
                var optionsEl = this.$('select[name="instanceType"] option');
                optionsEl.each(function() {
                    var type = self.findOperationType($(this).val());

                    if (!type.get('canProcess')) {
                        $(this).prop('disabled', true);
                    }
                });

                // Select the first non-disabled option
                var selected;
                for (var i in optionsEl) {
                    var optionEl = $(optionsEl[i]);

                    if (!optionEl.is(':disabled')) {
                        selected = optionEl;
                        break;
                    }
                }

                if (selected) {
                    selected.prop('selected', true);
                }
            },

            clearItemStock: function() {
                // Remove the item stock models
                this.itemStockView.model.remove(this.itemStockView.model.models);

                // Remove the item stock rows

            },

            updateOperationType: function(instanceType) {
                // If the instance type is a model just use it, otherwise expect that it is the uuid id for the instance type
                this.currentOperationType = instanceType instanceof openhmis.OperationType ?
                    instanceType :
                    this.findOperationType(instanceType);

                if (this.currentOperationType != undefined) {
                    var source = $('select[name="source"]');
                    var dest = $('select[name="destination"]');
                    var institution = $('select[name="institution"]');
                    var department = $('select[name="department"]');
                    var patientSearch = self.$("#find-patient");

                    source.prop('disabled', !this.currentOperationType.get('hasSource'));
                    if (source.is(":disabled")) {
                        source.val(0)
                    }
                    dest.prop('disabled', !this.currentOperationType.get('hasDestination'));
                    if (dest.is(":disabled")) {
                        dest.val(0);
                    }
                    institution.prop('disabled', !this.currentOperationType.get('hasRecipient'));
                    department.prop('disabled', !this.currentOperationType.get('hasRecipient'));
                    if (institution.is(":disabled")) {
                        institution.val(0);
                        department.val(0);
                        patientSearch.hide();
                    } else {
                        patientSearch.show();

                        // Update the patient view as the expected elements will no be found
                        this.patientView.setElement($('#patient-view'));
                        this.patientView.render();
                    }

                    if (this.currentOperationType.get('hasRecipient')) {
                        this.$("#find-patient").show();
                        this.$patientSearch.prop('disabled', false);
                    } else {
                        this.$patientSearch.prop('disabled', true);
                    }

                    this.checkHideItemStock();
                }

                // Find or create the attributes element
                this.$attributes = this.$("#operationAttributes");
                if (!this.$attributes.length) {
                    // Find the element that the attributes should be added after
                    this.$("form").after("<form id='operationAttributes' class='bbf-form' />");
                    this.$attributes = this.$("#operationAttributes");
                }

                // Load the patient search and operation type attributes
                openhmis.renderAttributesFragment(this.$attributes, "OperationType", "uuid=" + this.currentOperationType.id);

            },

            findOperationType: function(uuid) {
                var type = undefined;
                var models = this.operationTypes.models;
                for (var i in models) {
                    var model = models[i];
                    if (model.id == uuid) {
                        type = model;
                        break;
                    }
                }

                return type;
            },

            sourceChanged: function(event) {
                var items = this.itemStockView.model.length;
                if (items > 0) {
                    // Confirm operation type change if there are any defined item stock
                    if (!confirm(openhmis.getMessage('openhmis.inventory.operations.confirm.sourceStockroomChange'))) {
                        // Set the value back to the previous value
                        this.$(event.target).val($.data(event.target, 'current'));

                        return false;
                    }
                }
                // Store the updated operation type as the current value for the select
                $.data(event.target, 'current', this.$(event.target).val());

                this.checkHideItemStock();

                if (items > 0) {
                    this.clearItemStock();
                }
            },

            showProcessingDialog: function() {
                $('.cancel').prop('disabled', true);
                $('.submit').prop('disabled', true);

                $('#processingDialog').dialog({
                    dialogClass: "no-close",
                    title: "Processing Operation",
                    draggable: false,
                    resizable: false,
                    modal: true,
                    width: 350
                });
            },

            hideProcessingDialog: function() {
                $('.cancel').prop('disabled', false);
                $('.submit').prop('disabled', false);

                $('#processingDialog').dialog("close");
            }

        });

        openhmis.OperationItemStockView = openhmis.GenericListEntryView.extend({
            schema: {
                item: { type: "ItemStockAutocomplete" },
                quantity: { type: "CustomNumber" },
                expiration: { type: "ItemStockEntryExpiration" },
                batchOperation: { hidden: true }
            },

            initialize: function(options) {
                _.bindAll(this);

                this.events = _.extend({}, this.events, {
                    'keypress': 'onKeyPress'
                });

                // Make sure that the options exist
                options = options ? options : {};

                // Create the stockroom jquery selector in the item schema so that it is passed to the editor
                this.schema.item.stockroomSelector = options.stockroomSelector;
                this.schema.item.parentView = options.view;
                this.schema.expiration.parentView = options.view;

                this.parentView = options.view;

                var operation = options.operation ? options.operation : new openhmis.Operation();
                this.setOperation(operation);

                openhmis.GenericListEntryView.prototype.initialize.call(this, options);

                this.itemView = openhmis.OperationItemStockItemView;
            },

            render: function(extraContext) {
                openhmis.GenericListEntryView.prototype.render.call(this, extraContext);

                // Add the item-stock class to the item stock table so it is easy to locate
                this.$("table").addClass("item-stock");
            },

            setOperation: function(operation) {
                this.operation = operation;
                this.operation.set('items', operation.get("items") ?
                    operation.get("items") :
                    new openhmis.GenericCollection(null, { model: openhmis.ItemStockEntry })
                );
                this.model = this.operation.get('items');

                this.options.itemActions = ["remove", "inlineEdit"];
            },

            setupNewItem: function(view) {
                // Handle adding an item from the input line
                // TODO: Is this the best place to handle changes/setUnsaved()?

                if (view !== undefined) {
                    this.operation.setUnsaved();
                    view.on("change remove", this.operation.setUnsaved);
                }

                openhmis.GenericListEntryView.prototype.setupNewItem.call(this, view);
            },

            onKeyPress: function(event) {
                if (event.keyCode === 13 /* Enter */)  {
                    var itemStock = this.operation.get("items");
                    if (itemStock && itemStock.length > 0) {
                        var errors = null;
                        itemStock.each(function(item) {
                            errors = item.validate(item.attributes, '');

                            if (errors != null) {
                                return errors;
                            }
                        });
                    }
                }
            },

            validate: function(allowEmptyOperation) {
                var errors = this.operation.validate(true);
                var elMap = {
                    'operationNumber': [$('#newOperation'), this],
                    'items': [ $('#operationItems'), this ]
                };

                if (allowEmptyOperation === true
                    && errors
                    && errors.items !== undefined) {
                    delete errors.items;
                }

                if (errors && _.size(errors) > 0) {
                    for (var e in errors) {
                        openhmis.validationMessage(elMap[e][0], errors[e], elMap[e][1]);
                    }

                    return false;
                }

                return true;
            }
        });

        openhmis.OperationItemStockItemView = openhmis.GenericListItemView.extend({
            initialize: function(options) {
                this.events = _.extend({}, this.events, {
                    'keypress': 'onKeyPress'
                });

                openhmis.GenericListItemView.prototype.initialize.call(this, options);
                _.bindAll(this);

                if (this.form) {
                    this.form.on('quantity:change', this.update);
                    this.form.on('item:change', this.updateItem);
                }
            },

            updateItem: function(form, itemEditor) {
                // Get the selected item
                var item = itemEditor.getValue();
                this.refreshItemFields(item, form);

                // Set the quantity if not specified
                if (form.fields.quantity.getValue() === 0) {
                    form.fields.quantity.setValue(1);
                }

                // Set the focus on the quantity field
                form.fields.quantity.editor.focus(true);
            },

            refreshItemFields: function(item, form) {
                var itemHasExpiration = item.get("hasExpiration");

                // TODO: This logic is much too closely coupled to the underlying item editor
                var stockroomUuid = $(this.options.schema.item.stockroomSelector).val();
                if (stockroomUuid) {
                    var self = this;

                    // Get the selected item stock details and build the list of available expiration dates and batch
                    // operations
                    var search = new openhmis.GenericCollection([], {
                        model: openhmis.ItemStock
                    });
                    search.fetch({
                        queryString: "stockroom_uuid=" + stockroomUuid + "&item_uuid=" + item.id,
                        success: function(model, resp) {
                            var expirations = [];
                            var batches = [];
                            var stockroomQuantity = null;

                            if (model.models && model.models.length > 0) {
                                var hasExp = false;
                                var hasNone = false;

                                // Build the expiration and batch lists for this item
                                _.each(model.models[0].get("details"), function (detail) {
                                    var exp = detail.get("expiration");
                                    if (exp && exp != "") {
                                        hasExp = true;
                                        expirations.push(openhmis.dateFormatLocale(exp));
                                    } else {
                                        hasNone = true;
                                    }

                                    var batch = detail.get("batchOperation");
                                    if (batch) {
                                        batches.push(batch);
                                    }

                                    stockroomQuantity = model.models[0].get("quantity")
                                });

                                if ((hasExp || itemHasExpiration) && hasNone && stockroomQuantity != null && stockroomQuantity > 0) {
                                    // Add the 'None' expiration if there was at least one expiration or the item is
                                    //  expirable and there was an empty expiration
                                    itemHasExpiration = true;
                                    expirations.push("");
                                } else if (hasExp && !itemHasExpiration) {
                                    // If a non-expirable item has stock with expirations then always include the
                                    //  'None' expiration
                                    itemHasExpiration = true;
                                    expirations.push("");
                                }
                            }

                            self.updateEditors(form, item, itemHasExpiration, expirations, batches);
                            self.update();
                        },
                        error: function() {
                            self.updateEditors(form, item, itemHasExpiration, undefined, undefined);
                            this.update();
                        }
                    });
                } else {
                    this.updateEditors(form, item, itemHasExpiration, undefined, undefined);
                    this.update();
                }
            },

            updateEditors: function(form, item, hasExpiration, expirations, batches) {
                if (form) {
                    form.fields.expiration.editor.options.visible = hasExpiration;
                    form.fields.expiration.editor.options.options = expirations;
                    form.fields.expiration.editor.options.item = item;

                    form.fields.expiration.editor.render();
                }
            },

            update: function() {
                if (this.updateTimeout !== undefined) {
                    clearTimeout(this.updateTimeout);
                    this.form.model.set(this.form.getValue());
                }

                var view = this;
                var update = function() {
                    var quantity = view.form.getValue("quantity");
                };

                this.updateTimeout = setTimeout(update, 200);
                this.form.model.set(this.form.getValue());
            },

            onKeyPress: function(event) {
                //this is for firefox as arrows are detected as keypress events
                if ($('input[name=quantity]').is(':focus')) {
                    var view = this;
                    if(event.keyCode === 38 /*arrow up*/) {
                        var quantity = view.form.getValue("quantity");
                        view.form.setValue({quantity : quantity + 1});
                        this.update;
                    }
                    if(event.keyCode === 40 /*arrow down*/) {
                        var quantity = view.form.getValue("quantity");
                        view.form.setValue({quantity : quantity - 1});
                        this.update;
                    }
                }

                if (event.keyCode === 13 /* Enter */)  {
                    this.update();
                    this.commitForm(event);

                    // Prevent enter press from interfering with HTML form controls
                    event.preventDefault();
                }
            },

            commitForm: function(event) {
                if (!this.model.attributes || !this.model.attributes.item) {
                    return;
                }

                var errors = openhmis.GenericListItemView.prototype.commitForm.call(this, event);
                if (errors === undefined && event && event.keyCode === 13) {
                    this.trigger("focusNext", this);
                }
            },

            displayErrors: function(errorMap, event) {
                // If there is already another item in the collection and
                // this is not triggered by enter key, skip the error message
                if (event && event.type !== "keypress"
                    && this.model.collection && this.model.collection.length > 0) {
                    //  Nothing to do
                } else if (event && event.type === "keypress" && event.keyCode === 13
                    && this.model.collection && this.model.collection.length > 1) {

                    // If there is already an item in the collection and the event
                    // was triggered by the enter key, request that focus be moved
                    // to the next form item.

                    this.trigger("focusNext", this);
                } else {
                    openhmis.GenericListItemView.prototype.displayErrors.call(this, errorMap, event);
                }
            },

            focus: function(form) {
                openhmis.GenericListItemView.prototype.focus.call(this, form);

                if (!form) {
                    this.$('.itemStock-name').focus();
                }
            },

            render: function() {
                openhmis.GenericListItemView.prototype.render.call(this);

                this.$('td.field-quantity')
                    .add(this.$('td.field-price'))
                    .add(this.$('td.field-total'))
                    .addClass("numeric");

//                this.$('input[type=number]').stepper({
//                    allowArrows: false,
//                    onStep: this.stepCallback
//                });

                return this;
            },

            stepCallback: function(val, up) {
                this.update();
            }
        });

		return openhmis;
	}
);
