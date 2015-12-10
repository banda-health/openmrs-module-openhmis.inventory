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
            openhmis.url.backboneBase + 'js/lib/jquery',
            openhmis.url.backboneBase + 'js/lib/backbone',
            openhmis.url.backboneBase + 'js/lib/underscore',
            openhmis.url.inventoryBase + 'js/model/item',
            openhmis.url.inventoryBase + 'js/model/department',
            openhmis.url.inventoryBase + 'js/view/search',
            openhmis.url.inventoryBase + 'js/model/operation',
            openhmis.url.inventoryBase + 'js/model/institution',
            openhmis.url.backboneBase + 'js/lib/backbone-forms',
            openhmis.url.backboneBase + 'js/lib/labelOver',
            openhmis.url.backboneBase + 'js/view/editors',
            openhmis.url.backboneBase + 'js/model/concept'
    ],
    function ($, Backbone, _, openhmis) {
        var editors = Backbone.Form.editors;

        editors.DepartmentSelect = editors.GenericModelSelect.extend({
            modelType: openhmis.Department,
            displayAttr: "name"
        });

        editors.OptionalDepartmentSelect = editors.GenericModelSelect.extend({
            modelType: openhmis.Department,
            displayAttr: "name",
            allowNull: true
        });

        editors.InstitutionSelect = editors.GenericModelSelect.extend({
            modelType: openhmis.Institution,
            displayAttr: "name",
            allowNull: true
        });

        editors.ItemPriceSelect = editors.GenericModelSelect.extend({
            modelType: openhmis.ItemPrice,
            displayAttr: "price"
        });

        editors.StockroomSelect = editors.GenericModelSelect.extend({
            modelType: openhmis.Stockroom,
            displayAttr: "name",
            allowNull: true
        });

        editors.OperationTypeSelect = editors.GenericModelSelect.extend({
            modelType: openhmis.OperationType,
            displayAttr: "name",

            // Hacky support for updating the instance type based on the current user authorization
            renderOptions: function(options) {
                // The "options" here is the model collection that has been async loaded and thus renders after the main
                //  form (NewOperationView) has finished executing it's render method. This is the only way I've been able
                //  to get the instance type editor to be processed after it has been loaded.
                editors.GenericModelSelect.prototype.renderOptions.call(this, options);

                if (this.model.schema.instanceType.view) {
                    this.model.schema.instanceType.view.checkInstanceType();
                }
            }
        });

        editors.OperationSelect = editors.GenericModelSelect.extend({
            modelType: openhmis.Operation,
            displayAttr: "operationNumber"
        });

        editors.ItemAttributeTypeSelect = editors.GenericModelSelect.extend({
            modelType: openhmis.ItemAttributeType,
            displayAttr: "name"
        });

        editors.DefaultExpirationPeriodStepper = editors.Base.extend({
            tagName: "span",
            className: "editor",
            tmplFile: openhmis.url.inventoryBase + 'template/editors.html',
            tmplSelector: '#defaultExpirationPeriodStepper-editor',

            initialize: function (options) {
                this.events = _.extend({}, this.events, {
                    'keypress': 'onKeyPress'
                });
                _.bindAll(this);
                editors.Base.prototype.initialize.call(this, options);
                this.template = this.getTemplate();
            },

            events: {
                'change #defaultExpirationPeriod': 'update'
            },

            onKeyPress: function (event) {
                //this is for firefox as arrows are detected as keypress events
                if ($('input[name=defaultExpirationPeriod]').is(':focus')) {
                    var view = this;
                    if (event.keyCode === 38 /*arrow up*/) {
                        if (this.value == null) {
                            this.value = 0;
                        }
                        var defaultExpirationPeriod = this.value;
                        this.$('#defaultExpirationPeriod').val(defaultExpirationPeriod + 1)
                        this.update();
                    }
                    if (event.keyCode === 40 && this.value != null /*arrow down*/) {
                        var defaultExpirationPeriod = this.value;
                        if (defaultExpirationPeriod > 0) {
                            this.$('#defaultExpirationPeriod').val(defaultExpirationPeriod - 1)
                            this.update();
                        }
                    }
                }
            },

            getValue: function () {
                return this.value;
            },

            update: function () {
                var tmp = this.$('#defaultExpirationPeriod').val();
                if (tmp != null && tmp != '') {
                    this.value = parseInt(tmp);
                } else {
                    this.value = null;
                }
            },

            render: function () {
                this.$el.html(this.template({
                    defaultExpirationPeriod: this.value
                }));
//                this.$('input[type=number]').stepper({
//                    allowArrows: false,
//                    limit: [0, null],
//                    onStep: this.update
//                });
//                this.$('#outer-span-stepper').removeClass("ui-widget-content-spinner")
//                    .removeClass("ui-spinner-input-spinner")
//                    .addClass("ui-spinner-input-spinner-border")
//                    .addClass("ui-widget-content-spinner-border");
                return this;
            }
        });

        editors.ConceptInput = editors.Base.extend({
            tagName: "span",
            className: "editor",
            tmplFile: openhmis.url.inventoryBase + 'template/editors.html',
            tmplSelector: '#conceptInput',

            initialize: function (options) {
                _.bindAll(this);
                editors.Base.prototype.initialize.call(this, options);
                this.cache = {};
                this.template = this.getTemplate();
            },

            events: {
                'blur .concept-display': 'handleBlur'
            },

            handleBlur: function () {
                this.handleSpinnerHide();
                if ($('.concept-display').val() == '') {
                    $('.concept').val('');
                }
            },

            getValue: function () {
                return this.value;
            },

            doConceptSearch: function (request, response) {
                var term = request.term;
                var query = "?q=" + encodeURIComponent(term);
                this.doStockSearch(request, response, openhmis.Concept, query);
            },

            doStockSearch: function (request, response, model, query) {
                this.handleSpinnerShow();
                var term = request.term;
                if (query in this.cache) {
                    response(this.cache[query]);
                    this.handleSpinnerHide();
                    return;
                }
                var resultCollection = new openhmis.GenericCollection([], { model: model });
                var view = this;
                var fetchQuery = query ? query : "?q=" + encodeURIComponent(term);
                resultCollection.fetch({
                    url: "/openmrs/ws/rest/v1/concept" + fetchQuery,
                    success: function (collection, resp) {
                        view.handleSpinnerHide();
                        var data = collection.map(function (model) {
                            return {
                                val: model.id,
                                display: model.get('display')
                            }
                        });
                        view.cache[query] = data;
                        response(data);
                    }
                });
            },

            selectConcept: function (event, ui) {
                var uuid = ui.item.val;
                var name = ui.item.display;
                this.$('.concept-display').val(name);
                this.$('.concept').val(uuid);
                event.preventDefault();
            },

            render: function () {
                var self = this;
                this.$el.html(this.template({
                    concept: this.model.attributes.concept,
                    item_id: self.model.cid
                }));
                this.$('.concept-display').autocomplete({
                    minLength: 2,
                    source: this.doConceptSearch,
                    select: this.selectConcept
                })
                    // Tricky stuff here to get the autocomplete list to render with our custom data
                    .data("autocomplete")._renderItem = function (ul, concept) {
                    return $("<li></li>").data("concept.autocomplete", concept)
                        .append("<a>" + concept.display + "</a>").appendTo(ul);
                };
                this.handleSpinnerHide();
                return this;
            },

            handleSpinnerHide: function () {
                this.$('#conceptDisplay').removeClass('spinner-float-style');
                this.$('.spinner').hide();
            },

            handleSpinnerShow: function () {
                this.$('#conceptDisplay').addClass('spinner-float-style');
                this.$('.spinner').show();
            },

            renderInput: function () {
                $('#conceptBox').append('<input id="conceptInput" type="text" placeholder="Enter concept name or id"><input type="hidden" class="concept-uuid" name="concept"/>');
            }

        });

        editors.ItemListSelect = editors.ListSelect.extend({
            modalWidth: 750,
            initListView: function () {
                var options = this.schema.editorOptions || {};
                options.model = this.schema.options;
                options.searchView = openhmis.DepartmentAndNameSearchView;
                this.listView = new openhmis.GenericSearchableListView(options);
            }
        });

        editors.ItemStockAutocomplete = editors.Base.extend({
            tagName: "span",
            className: "editor",
            tmplFile: openhmis.url.inventoryBase + 'template/editors.html',
            tmplSelector: '#itemstock-autocomplete-editor',

            departmentCollection: function() {
                var collection = new openhmis.GenericCollection([], { model: openhmis.Department });
                collection.fetch();
                return collection;
            }(),

            initialize: function(options) {
                _.bindAll(this);

                editors.Base.prototype.initialize.call(this, options);

                this.template = this.getTemplate();
                this.stockroomSelector = options.schema.stockroomSelector;
                this.parentView = options.schema.parentView;

                this.cache = {};
                this.departmentCollection.on("reset", this.render);
            },

            events: {
                'change select.department': 'modified',
                'change input.itemStock-name' : 'modified',
                'focus select': 'handleFocus',
                'focus .itemStock-name': 'handleFocus',
                'blur select': 'handleBlur',
                'keypress .itemStock-name': 'onItemNameKeyPress'
            },

            getUuid: function() {
                return this.$('.item-uuid').val();
            },

            getValue: function() {
                return this.value;
            },

            handleFocus: function(event) {
                if (this.hasFocus) return;
                this.trigger("focus", this);
            },

            handleBlur: function(event) {
                if (!this.hasFocus) return;
                var self = this;
                setTimeout(function() {
                    // Check if another input from this editor has come into focus
                    if (self.$('select:focus')[0] || self.$('.itemStock-name:focus')[0] || self.$('label:focus')[0]) {
                        return;
                    }

                    if (self.value) {
                        self.$('.itemStock-name').val(self.value.get("name"));
                        self.$('.department').val(self.value.get("department").id);
                        self.$('label.over-apply').hide();
                    } else {
                        self.render();
                    }

                    self.trigger("blur", self);
                }, 0);
            },

            onItemNameKeyPress: function(event) {
                if (event.keyCode === 13 && this.itemUpdating !== undefined) {
                    event.stopPropagation();
                }
            },

            modified: function(event) {
                // TODO: Some logic to handle messing with the form after
                //       successful validation
            },

            doSearch: function(request, response) {
                // Query the item stock by name
                var query = "?q=" + encodeURIComponent(request.term);

                // Add the optional item department query filter
                var department_uuid = this.$('.department').val();
                if (department_uuid) {
                    query += "&department_uuid=" + encodeURIComponent(department_uuid);
                }
                var cacheSection = "item"
                // We only want to return items that have physical stock
                query += "&has_physical_inventory=true";

                if (cacheSection + query in this.cache) {
                    response(this.cache[cacheSection + query]);
                    return;
                };
                this.search(request, response, openhmis.Item, query, cacheSection,
                    function(model) {
                        return {
                            val: model.id,
                            label: model.get('name'),
                            department_uuid: model.get('department').id
                        }
                    }
                );
            },

            search: function(request, response, model, query, cacheSection, mapFn) {
                cacheSection = cacheSection ? cacheSection : "";

                // Check for cached queries
                if (cacheSection + query in this.cache) {
                    response(this.cache[query]);
                    return;
                }

                var resultCollection = new openhmis.GenericCollection([], { model: model });
                var fetchQuery = query ? query : "?q=" + encodeURIComponent(request.term);

                var view = this;
                resultCollection.fetch({
                    url: resultCollection.url + fetchQuery,
                    success: function(collection, resp) {
                        var data = collection.map(mapFn);

                        view.cache[cacheSection + query] = data;
                        response(data);
                    },
                    error: openhmis.error,
                    statusCode: {
                        401: function(data) {
                            alert("Auth Failure!");
                        }
                    }
                });
            },

            selectItem: function(event, ui) {
                this.itemUpdating = true;

                var uuid = ui.item.val;
                var name = ui.item.label;
                var departmentUuid = ui.item.department_uuid;

                this.$('.itemStock-name').val(name);
                this.$('.itemStock-uuid').val(uuid);
                this.$('.department').val(departmentUuid);

                this.value = new openhmis.Item({ uuid: uuid });

                var view = this;
                this.value.fetch({ success: function(model, resp) {
                    view.trigger('change', view);
                    delete view.itemUpdating;
                }});
            },

            departmentKeyDown: function(event) {
                if (event.keyCode === 8) {
                    $(event.target).val('');
                }
            },

            render: function() {
                var item, itemStock, department;

                itemStock = undefined;
                department = this.value ? this.value.get("department") : undefined;
                item = this.value ? this.value : undefined;

                this.$el.html(this.template({
                    departments: this.departmentCollection,
                    itemStock: itemStock,
                    department: department,
                    item: item,
                    cid: this.model.cid
                }));

                this.$('select').keydown(this.departmentKeyDown);
                this.$('label').labelOver('over-apply');

                var self = this;
                this.$('.itemStock-name')
                    .autocomplete({
                        minLength: 2,
                        source: this.doSearch,
                        select: this.selectItem
                    })
                    .data("autocomplete")._renderItem = function(ul, itemStock) {
                        // Tricky stuff here to get the autocomplete list to render with our custom data
                        return $("<li></li>").data("item.autocomplete", itemStock)
                            .append("<a>" + itemStock.label + "</a>").appendTo(ul);
                    };

                return this;
            }
        });

        editors.ItemStockEntryExpiration = editors.Base.extend({
            tagName: "span",
            className: "expiration-editor",
            tmplFile: openhmis.url.inventoryBase + 'template/editors.html',
            tmplSelector: '#itemstock-expiration-editor',

            events: {
                'change select.expiration': 'modified',
                'change .itemstock-expiration' : 'modified'
            },

            initialize: function(options) {
                _.bindAll(this);

                editors.Base.prototype.initialize.call(this, options);

                this.template = this.getTemplate();
                this.stockroomSelector = options.schema.stockroomSelector;

                this.cache = {};
                this.parentView = options.schema.parentView;
            },

            render: function() {
                var operationType = this.getOperationType();
                if (!operationType) {
                    return;
                }

                var defaultExp;
                var entryRequired = operationType.get('hasSource') !== true;
                if (entryRequired ) {
                    // Set the default expiration date to the current date plus the number of days defined in the item's
                    //  default expiration period. If the item does not have a default, no value should be used so that
                    //  user is required to enter something
                    var itemExpPeriod = this.options.item ? this.options.item.get('defaultExpirationPeriod') : undefined;
                    if (itemExpPeriod) {
                        defaultExp = new Date();
                        defaultExp.setDate(defaultExp.getDate() + itemExpPeriod);
                    }
                }

                this.$el.html(this.template({
                    visible: this.options.visible ? this.options.visible : false,
                    entryRequired: entryRequired,
                    defaultExpirationDate: defaultExp,
                    expirations: this.options.options,
                    cid: this.model.cid
                }));

                this.$('label').labelOver('over-apply');

                if (entryRequired) {
                    var entryEl = this.$('#' + this.model.cid + '_expirationEntry');

                    // Turn the expiration text input into a date picker
                    entryEl.datepicker({
                        defaultDate: defaultExp,
                        minDate:0
                    });

                    // Set the text to the default value, if one has been calculated
                    if (defaultExp) {
                        entryEl.val(openhmis.dateFormatLocale(defaultExp));
                    }
                }

                return this;
            },

            modified: function() {
                this.commit();
            },

            getValue: function() {
                var operationType = this.getOperationType();
                if (!operationType) {
                    return undefined;
                }

                var selected = undefined;
                if (operationType.get("hasSource") === true) {
                    selected = this.$('option:selected').val();
                } else {
                    selected = this.$('#' + this.model.cid + '_expirationEntry').val();
                }

                // Try to parse the date into a real Date object
                if (selected) {
                    try {
                        var date = new Date(selected);
                        if (!isNaN(date.getTime())) {
                            selected = openhmis.dateFormat(date);
                            //selected = date;
                        }
                    } catch(err) {
                        //selected = undefined;
                    }
                }

                // If the value could not be parsed as a date ("Auto" or "None") then the value will be parsed by the
                //  NewOperationView.save method
                return selected;
            },

            getOperationType: function() {
                // The parent view tracks the currently selected operation type so we'll use that here
                var operationType = this.parentView.currentOperationType;
                if (!operationType) {
                    alert(openhmis.getMessage('openhmis.inventory.operations.type.load.error'));
                }

                return operationType;
            }
        });

        editors.ItemStockEntryBatch = editors.Base.extend({
            tagName: "span",
            className: "batch-editor",
            tmplFile: openhmis.url.inventoryBase + 'template/editors.html',
            tmplSelector: '#itemstock-batch-editor'
        });

        editors.OperationDateEditor = editors.Base.extend({
            tagName: "span",
            className: "operationDate-editor",
            tmplFile: openhmis.url.inventoryBase + 'template/editors.html',
            tmplSelector: '#operation-date-editor',

            currentDateEl: null,
            currentDateValueEl: null,
            changeOperationDialogEl: null,
            operationDateInputEl: null,
            dateOperationsEl: null,
            operationOrderEl: null,
            updateOperationEl: null,

            dateOperations: null,
            selectedOperation: null,

            events: {
                'click #changeOperationDate' : 'showChangeDialog'
            },

            initialize: function(options) {
                _.bindAll(this);

                editors.Base.prototype.initialize.call(this, options);

                this.template = this.getTemplate();
            },

            render: function() {
                if (this.model.operationDate == undefined) {
                    this.model.operationDate = new Date();
                }

                this.$el.html(this.template({
                    operationDateText: openhmis.dateTimeFormatLocale(this.model.operationDate),
                    operationDate: this.model.operationDate,
                    today: new Date().toDateString(),
                    cid: this.model.cid
                }));

                this.currentDateEl = this.$("#currentOperationDateLabel");
                this.currentDateValueEl = this.$('#currentOperationDateValue');
                this.changeOperationDialogEl = this.$('#changeOperationDateDialog');

                this.operationDateInputEl = this.$('#operationDateEntry');
                this.operationDateInputEl.datepicker({
                    maxDate: new Date()
                });

                this.operationOrderEl = this.$('#operationOrderSelect');
                this.dateOperationsEl = this.$('#dateOperations');

                this.operationDateInputEl.change(this.dateChanged);
                this.operationOrderEl.change(this.orderChanged);

                this.dateOperationsEl.hide();
                this.operationOrderEl
                    .find('option')
                    .remove();

                this.changeOperationDialogEl.dialog({
                    autoOpen: false,
                    modal: true,
                    width: 350,
                    height: 175,
                    draggable: false,
                    resizable: false,
                    buttons: [
                        {
                            text: "Cancel",
                            click: this.cancelChange
                        },
                        {
                            id: "updateOperationDate",
                            text: "Change Date",
                            click: this.commitChange
                        }
                    ]
                });

                return this;
            },

            showChangeDialog: function() {
                this.changeOperationDialogEl.dialog("open");

                // Not using 'this.$()' for the search here on purpose (for, um, reasons)
                this.updateOperationEl = $('#updateOperationDate');
                this.updateOperationEl.button("disable");
            },

            dateChanged: function() {
                this.dateOperationsEl.hide();
                this.operationOrderEl
                    .find('option')
                    .remove();

                var date = this.operationDateInputEl.val();
                if (date == undefined) {
                    return;
                } else {
                    date = new Date(date);
                }

                this.dateOperations = null;
                this.selectedOperation = null;

                // Get the operations that occurred on the selected date
                var search = new openhmis.GenericCollection([], {
                    model: openhmis.NewOperation
                });
                var self = this;
                search.fetch({
                    queryString: "operation_date=" + openhmis.dateFormat(date, false),
                    success: function(model, resp) {
                        self.dateOperations = model.models;

                        if (model.models && model.models.length > 0) {
                            // If there were any operations on the date then display the operation order select

                            // Add first empty option
                            self.operationOrderEl.append(
                                $('<option></option>').html("")
                            );

                            _.each(model.models, function (operation) {
                                self.operationOrderEl.append(
                                    $('<option></option>').val(operation.get('operationOrder'))
                                        .html("Before " + operation.get('operationNumber') +
                                        " (" + openhmis.timeFormatLocale(operation.get('operationDate')) + ")")
                                );
                            });

                            // Get the last operation order to use fo the 'after last' option
                            order = model.models[model.models.length - 1].get('operationOrder');
                            self.operationOrderEl.append(
                                $('<option></option>').val(order + 1).html("After Last Operation")
                            );

                            self.dateOperationsEl.show();
                            self.updateOperationEl.button("disable");
                        } else {
                            // There are no other operations on the selected date
                            self.dateOperationsEl.hide();
                            self.operationOrderEl
                                .find('option')
                                .remove();
                            self.updateOperationEl.button("enable");
                        }
                    },
                    error: function(model, resp) {
                        openhmis.error(model, resp);
                    }
                });
            },

            orderChanged: function() {
                var order = this.operationOrderEl.val();
                if (order == undefined || order == "") {
                    this.selectedOperation = null;
                    this.updateOperationEl.button("disable");
                } else {
                    var index = this.operationOrderEl[0].selectedIndex - 1;
                    if (index < this.dateOperations.length) {
                        this.selectedOperation = this.dateOperations[index];
                    } else {
                        // If the last option is select (After last operation) then use the last operation
                        this.selectedOperation = this.dateOperations[this.dateOperations.length - 1]
                    }

                    this.updateOperationEl.button("enable");
                }
            },

            cancelChange: function() {
                this.changeOperationDialogEl.dialog('close');
            },

            commitChange: function() {
                var date = this.operationDateInputEl.val();
                try {
                    date = new Date(date);
                } catch(err) {
                    date = null;
                }

                if (!date) {
                    return;
                }
                this.model.set('operationDate', date);

                var order = this.operationOrderEl.val();
                if (order) {
                    if (this.selectedOperation) {
                        // If an operation was selected for this operation to run before then use that operation date
                        date = this.selectedOperation.get('operationDate');
                        this.model.set('operationDate', this.selectedOperation.get('operationDate'));
                    }

                    this.model.set('operationOrder', order);

                    this.currentDateEl.text(openhmis.dateTimeFormatLocale(date));
                } else {
                    this.currentDateEl.text(openhmis.dateFormatLocale(date));
                }

                this.currentDateValueEl.val(date);

                this.changeOperationDialogEl.dialog('close');
            },

            getValue: function() {
                return openhmis.dateFormatWithSeconds(this.currentDateValueEl.val());
            }
        });

        return editors;
    }
);
