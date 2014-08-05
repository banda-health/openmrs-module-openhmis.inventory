define(
	[
        openhmis.url.backboneBase + 'js/lib/jquery',
        openhmis.url.backboneBase + 'js/view/generic',
        openhmis.url.backboneBase + 'js/view/openhmis',
		openhmis.url.inventoryBase + 'js/model/operation',
		openhmis.url.inventoryBase + 'js/model/stockroom',
        openhmis.url.inventoryBase + 'js/view/editors',
		'link!' + openhmis.url.inventoryBase + 'css/style.css'
	],
	function($, openhmis) {
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

            events: {
                'click .completeOp': 'completeOperation',
                'click .cancelOp': 'cancelOperation',
                'click .cancel': 'cancel'
            },

            initialize: function(options) {
                openhmis.GenericAddEditView.prototype.initialize.call(this, options);

                this.itemsView = new openhmis.GenericListView({
                    model: new openhmis.GenericCollection([], {
                        model: openhmis.OperationItem
                    }),
                    showRetiredOption: false,
                    showRetired: true,
                    listTitle: "Operation Items",
                    listFields: ['item', 'quantity', 'batchOperation', 'expiration'],
                    itemView: openhmis.OperationItemListItemView
                });

                this.itemsView.on("fetch", this.fetch);
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

                // Post the status change and then reload the model
                this.updateStatus("CANCELLED");
            },

            prepareModelForm: function(model, options) {
              // We don't want to use the backbone form as we're just displaying the operation information here
                return undefined;
            },

            fetch: function(options) {
                options.queryString = openhmis.addQueryStringParameter(options.queryString, "operation_uuid=" + this.model.id);
            },

            render: function() {
                openhmis.GenericAddEditView.prototype.render.call(this);

                if (this.model.id) {
                    // Fetch and render the operation items list
                    this.itemsView.fetch(undefined, undefined);

                    var itemsEl = $("#operation-items");
                    itemsEl.append(this.itemsView.el);
                    itemsEl.show();
                }
            },

            updateStatus: function(status) {
                var self = this;

                // Post the status change using the raw ajax request. This just sends up the changed property, status.
                $.ajax({
                    type: 'POST',
                    url: this.model.url(),
                    data: '{"status":"' + status + '"}',
                    success: function(data) {
                        // Fetch the updated model
                        self.model.fetch({
                            success: function() {
                                // Once the fetch is complete, sync the changes back to the list and close this edit view
                                self.model.trigger("sync");

                                self.cancel();
                            }
                        });
                    },
                    error: function(model, resp) { openhmis.error(resp); },
                    contentType: "application/json",
                    dataType: 'json'
                });
            }
        });

        openhmis.NewOperationView = openhmis.GenericAddEditView.extend({
            /*tmplFile: openhmis.url.inventoryBase + 'template/operation.html',
            tmplSelector: '#new-operation',*/

            currentOperationType: undefined,

            initialize: function(options) {
                openhmis.GenericAddEditView.prototype.initialize.call(this, options);

                if (options.element) {
                    this.setElement(options.element);
                }

                if (options.addLink) {
                    options.addLink.click(this.showForm);
                }

                this.events = _.extend({}, this.events, {
                    'change select[name="instanceType"]': 'updateOperationType'
                });

                this.itemStockView = new openhmis.OperationItemStockView({
                    model: new openhmis.GenericCollection([], {
                        model: openhmis.ItemStockEntry
                    }),
                    itemView: openhmis.OperationItemStockItemView,
                    stockroomSelector: 'select[name="source"]',
                    view: this,
                    operation: this.model
                });

                var self = this;
                var operationTypes = new openhmis.GenericCollection([], { model: openhmis.OperationType });
                operationTypes.fetch({
                    success: function(collection, resp) {
                        self.currentOperationType = collection.models[0];
                    },
                    silent: true
                })
            },

            render: function() {
                openhmis.GenericAddEditView.prototype.render.call(this);

                if (!this.model.id) {
                    // Render the empty operation item stock list
                    this.itemStockView.render();

                    this.itemStockView.setupNewItem();
                }

                // Add the item-stock class to the item stock table so it is easy to locate
                this.itemStockView.$("table").addClass("item-stock");
            },

            updateOperationType: function(event) {
                var source = $('select[name="source"]');
                var dest = $('select[name="destination"]');

                this.currentOperationType = this.findOperationType($(event.target).val());
                if (this.currentOperationType != undefined) {
                    source.prop('disabled', !this.currentOperationType.get('hasSource'));
                    dest.prop('disabled', !this.currentOperationType.get('hasDestination'));
                }
            },

            showForm: function() {
                // Render new operation form
                this.beginAdd();
                $(".addLink").hide();

                // Insert the item stock list after the form but before the buttons
                $("#newOperation").find(".bbf-form").append(this.itemStockView.el);

                // Display the form as a dialog
                this.$el.show();
                //this.$el.dialog();
            },

            findOperationType: function(uuid) {
                var type = undefined;
                var models = this.modelForm.fields.instanceType.schema.options.models;
                for (var i in models) {
                    var model = models[i];
                    if (model.get('uuid') == uuid) {
                        type = model;
                        break;
                    }
                }

                return type;
            }
        });

        openhmis.OperationItemStockView = openhmis.GenericListEntryView.extend({
            schema: {
                item: { type: "ItemStockAutocomplete" },
                expiration: { type: "ItemStockEntryExpiration" },
                /*batchOperation: { type: "" },*/
                quantity: { type: "CustomNumber" }
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
                this.schema.expiration.parentView = options.view;
                //this.schema.batchOperation.parentView = options.view;

                var operation = options.operation ? options.operation : new openhmis.Operation();
                this.setOperation(operation);

                openhmis.GenericListEntryView.prototype.initialize.call(this, options);

                this.itemView = openhmis.OperationItemStockItemView;
            },

            setOperation: function(operation) {
                this.operation = operation;
                this.model = operation.get("items") ?
                    operation.get("items") :
                    new openhmis.GenericCollection(null, { model: openhmis.ItemStockEntry });
                this.options.itemActions = ["remove", "inlineEdit"];
            },

            onItemRemoved: function(view) {
                this.setupNewItem();

                openhmis.GenericListEntryView.prototype.onItemRemoved.call(this, view);
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

                        if (errors == null) {
                            this.setupNewItem();
                        }
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
            },

            render: function() {
                openhmis.GenericListEntryView.prototype.render.call(this, { options: { listTitle: "" }});

                return this;
            },

            _addItemFromInputLine: function(inputLineView) {
                // Prevent multiple change events causing duplicate views
                if (this.model.getByCid(inputLineView.model.cid)) return;
                inputLineView.off("change", this._addItemFromInputLine);
                this.model.add(inputLineView.model, { silent: true });
                this._deselectAll();
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
                    this.form.on('item:change', this.updateItem);
                }
            },

            updateItem: function(form, itemEditor) {
                var item = itemEditor.getValue();
                this.refreshItemFields(item, form);
                if (form.fields.quantity.getValue() === 0) {
                    form.fields.quantity.setValue(1);
                }
                this.update();

                form.fields.quantity.editor.focus(true);
            },

            refreshItemFields: function(item, form) {
                // Get the selected item details
                var details = item.get("details");

                // Build the expiration and batch lists for this item
                var expirations = [];
                var batches = [];
                details.each(function(detail) {
                    var exp = detail.get("expiration");
                    if (exp && exp != "") {
                        expirations.push(exp);
                    }

                    var batch = detail.get("batchOperation");
                    if (batch) {
                        batches.push(batch);
                    }
                });

                // Load the lists into the fields
                if (form) {
                    form.fields.expiration.editor.options.options = expirations;
                    form.fields.expiration.editor.render();

                    form.fields.batchOperation.editor.options.options = batches;
                    form.fields.batchOperation.editor.render();
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
                    this.trigger("change", this);
                    this.commitForm(event);

                    // Prevent enter press from interfering with HTML form controls
                    event.preventDefault();
                }
            },

            commitForm: function(event) {
                var errors = openhmis.GenericListItemView.prototype.commitForm.call(this, event);
                if (errors === undefined && event && event.keyCode === 13) {
                    this.trigger("focusNext", this);
                }
            },

            onModelChange: function(model) {
                if (model.hasChanged() && model.isValid()) {
                    this.trigger("change", this);
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
                    this.$('.item-name').focus();
                }
            },

            _removeModel: function() {
                if (this.model.collection) {
                    this.model.collection.remove(this.model, { silent: true });
                }
            },

            render: function() {
                openhmis.GenericListItemView.prototype.render.call(this);

                this.$('td.field-quantity')
                    .add(this.$('td.field-price'))
                    .add(this.$('td.field-total'))
                    .addClass("numeric");

                this.$('input[type=number]').stepper({
                    allowArrows: false,
                    onStep: this.stepCallback
                });

                return this;
            },

            stepCallback: function(val, up) {
                this.update();
            }

        });

		return openhmis;
	}
);
