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
		openhmis.url.backboneBase + 'js/lib/underscore',
		openhmis.url.backboneBase + 'js/lib/backbone',
		openhmis.url.backboneBase + 'js/lib/i18n',
		openhmis.url.backboneBase + 'js/view/search',
		'js!' + openhmis.url.inventoryBase + 'js/itemAutocomplete.js'
	],
	function($, _, Backbone, __, openhmis) {
		openhmis.DepartmentAndNameSearchView = openhmis.BaseSearchView.extend(
		/** @lends DepartmentAndNameSearchView.prototype */
		{
			tmplFile: openhmis.url.inventoryBase + 'template/search.html',
			tmplSelector: '#department-name-search',

			/**
			 * @class DepartmentAndNameSearchView
			 * @extends BaseSearchView
			 * @classdesc A search view that supports searching by department
			 *     and name.
			 * @constructor DepartmentAndNameSearchView
			 * @param {map} options View options.  See options for
			 *     {@link BaseSearchView}.
			 *
			 */
			initialize: function(options) {
				this.events['change #department_uuid'] = 'onFormSubmit';
				openhmis.BaseSearchView.prototype.initialize.call(this, options);
				var departmentCollection = new openhmis.GenericCollection([], { model: openhmis.Department });
				departmentCollection.on("reset", function(collection) {
					collection.unshift(new openhmis.Department({ name: __("Any") }));
				});
				this.form = new Backbone.Form({
					className: "inline",
					schema: {
						department_uuid: {
							title: __(openhmis.getMessage('openhmis.inventory.department.name')),
							type: "Select",
							options: departmentCollection
						},
						q: {
							title: __("%s Identifier or Name", this.model.meta.name),
							type: "Text",
							editorClass: "search"
						}
					},
					data: {}
				});
			},

			/** Collect user input */
			commitForm: function() {
				var filters = this.form.getValue();
				if (!filters.department_uuid && !filters.q)
					this.searchFilter = undefined;
				else
					this.searchFilter = filters;
			},

			/**
			 * Get fetch options
			 *
			 * @param {map} options Fetch options from base view
			 * @returns {map} Map of fetch options
			 */
			getFetchOptions: function(options) {
				options = options ? options : {}
				if (this.searchFilter) {
					for (var filter in this.searchFilter)
						options.queryString = openhmis.addQueryStringParameter(
							options.queryString, filter + "=" + encodeURIComponent(this.searchFilter[filter]));
				}
				return options;
			},

			/** Focus the search form */
			focus: function() { this.$("#q").focus(); },

			/**
			 * Render the view
			 *
			 * @returns {View} The rendered view
			 */
			render: function() {
				this.$el.html(this.template({ __: __ }));
				this.$("div.box").append(this.form.render().el);
				if (this.searchFilter)
					this.form.setValue(this.searchFilter);
				this.$("form").addClass("inline");
				this.$("form ul").append('<button id="submit">'+__("Search")+'</button>');
				return this;
			}
		});

		openhmis.StockroomStockTakeSearchView = openhmis.BaseSearchView.extend({
			tmplFile: openhmis.url.inventoryBase + 'template/search.html',
			tmplSelector: '#stockroom-search',

			initialize: function(options) {
				this.events['change #stockroom_uuid'] = 'onFormSubmit';
				openhmis.BaseSearchView.prototype.initialize.call(this, options);
				var stockroomCollection = new openhmis.GenericCollection([], { model: openhmis.Stockroom });
				stockroomCollection.on("reset", function(collection) {
                    collection.unshift(new openhmis.Stockroom({ name: __("Any") }));
                });
				this.form = new Backbone.Form({
					className: "inline",
					schema: {
						stockroom_uuid: {
							title: __("Stockroom"),
							type: "Select",
							options: stockroomCollection
						}
					},
					data: {}
				});
			},

			/** Collect user input */
			commitForm: function() {
				var filters = this.form.getValue();
				if (!filters.stockroom_uuid)
					this.searchFilter = undefined;
				else
					this.searchFilter = filters;
			},

			/**
			 * Get fetch options
			 *
			 * @param {map} options Fetch options from base view
			 * @returns {map} Map of fetch options
			 */
			getFetchOptions: function(options) {
				options = options ? options : {}
				if (this.searchFilter) {
					for (var filter in this.searchFilter)
						options.queryString = openhmis.addQueryStringParameter(
							options.queryString, filter + "=" + encodeURIComponent(this.searchFilter[filter]));
				}
				return options;
			},

			onFormSubmit: function(event) {
				if (openhmis.StockTakeChangeCounter != 0) {
					// if there are stock adjustments to the current stockroom
					if (!confirm(openhmis.getMessage('openhmis.inventory.search.confirm.stockroomChange'))) {
						event.currentTarget.selectedIndex = this.currentStockroomIndex;
						return;
					}
					openhmis.BaseSearchView.prototype.onFormSubmit.call(this, event);
					this.trigger('resetItemStockAdjustments', this);
				} else {
					//otherwise just perform search as usual
					openhmis.BaseSearchView.prototype.onFormSubmit.call(this, event);
				}
				this.currentStockroomIndex = event.currentTarget.selectedIndex;
			},

			/**
			 * Render the view
			 *
			 * @returns {View} The rendered view
			 */
			render: function() {
				this.$el.html(this.template({ __: __ }));
				this.$("div.box").append(this.form.render().el);
				if (this.searchFilter)
					this.form.setValue(this.searchFilter);
				this.$("form").addClass("inline");
				this.$("form ul").append('<button id="submit">'+__("Search")+'</button>');
				return this;
			},

			focus: function() {this.$("#stockroom_uuid").focus();}
		});

		openhmis.LocationAndNameSearchView = openhmis.BaseSearchView.extend({
			tmplFile: openhmis.url.inventoryBase + 'template/search.html',
			tmplSelector: '#location-name-search',

			initialize: function(options) {
				this.events['change #location_uuid'] = 'onFormSubmit';
				openhmis.BaseSearchView.prototype.initialize.call(this, options);
				var locationCollection = new openhmis.GenericCollection([], {
                    model: openhmis.Location,
                    limit: openhmis.rest.maxResults
                });
				locationCollection.on("reset", function(collection) {
					collection.unshift(new openhmis.Location({ name: __("Any") }));
				});
				this.form = new Backbone.Form({
					className: "inline",
					schema: {
						location_uuid: {
							title: __(openhmis.getMessage('openhmis.inventory.location.name')),
							type: "Select",
							options: locationCollection
						},
						q: {
							title: __("%s Name", this.model.meta.name),
							type: "Text",
							editorClass: "search"
						}
					},
					data: {}
				});
			},

			/** Collect user input */
			commitForm: function() {
				var filters = this.form.getValue();
				if (!filters.location_uuid && !filters.q)
					this.searchFilter = undefined;
				else
					this.searchFilter = filters;
			},

			/**
			 * Get fetch options
			 *
			 * @param {map} options Fetch options from base view
			 * @returns {map} Map of fetch options
			 */
			getFetchOptions: function(options) {
				options = options ? options : {}
				if (this.searchFilter) {
					for (var filter in this.searchFilter)
						options.queryString = openhmis.addQueryStringParameter(
							options.queryString, filter + "=" + encodeURIComponent(this.searchFilter[filter]));
				}
				return options;
			},

			/** Focus the search form */
			focus: function() { this.$("#q").focus(); },

			/**
			 * Render the view
			 *
			 * @returns {View} The rendered view
			 */
			render: function() {
				this.$el.html(this.template({ __: __ }));
				this.$("div.box").append(this.form.render().el);
				if (this.searchFilter)
					this.form.setValue(this.searchFilter);
				this.$("form").addClass("inline");
				this.$("form ul").append('<button id="submit">'+__("Search")+'</button>');
				return this;
			}
		});

		openhmis.ByNameSearchView = openhmis.BaseSearchView.extend({
			tmplFile: openhmis.url.inventoryBase + 'template/search.html',
			tmplSelector: '#by-name-search',

			initialize: function(options) {
				openhmis.BaseSearchView.prototype.initialize.call(this, options);
				this.form = new Backbone.Form({
					className: "inline",
					schema: {
						q: {
							title: __("%s Name", this.model.meta.name),
							type: "Text",
							editorClass: "search"
						}
					},
					data: {}
				});
			},

			getFetchOptions: function(options) {
				options = options ? options : {}
				if (this.searchFilter) {
					for (var filter in this.searchFilter)
						options.queryString = openhmis.addQueryStringParameter(
							options.queryString, filter + "=" + encodeURIComponent(this.searchFilter[filter]));
				}
				return options;
			},

			focus: function() { this.$("#q").focus(); },

			commitForm: function() {
				var filters = this.form.getValue();
				this.searchFilter = filters;
			},

			render: function() {
				this.$el.html(this.template({ __: __ }));
				this.$("div.box").append(this.form.render().el);
				if (this.searchFilter)
					this.form.setValue(this.searchFilter);
				this.$("form").addClass("inline");
				this.$("form ul").append('<button id="submit">'+__("Search")+'</button>');
				return this;
			}
		});

        openhmis.OperationSearchByStatus = openhmis.BaseSearchView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/search.html',
            tmplSelector: '#operation-search',

            STATUSES: ["Any", "Pending", "Completed", "Cancelled", "Rollback"],

            initialize: function(options) {
                this.events['change #operation_status'] = 'onFormSubmit';
                this.events['change #operationType_uuid'] = 'onFormSubmit';
                this.events['change #stockroom_uuid'] = 'onFormSubmit';
                this.events['change #item-uuid'] = 'onFormSubmit';

                this.item_uuid = "";

                openhmis.BaseSearchView.prototype.initialize.call(this, options);
                var operationTypeCollection = new openhmis.GenericCollection([], { model: openhmis.OperationType });
                operationTypeCollection.on("reset", function(collection) {
                    collection.unshift(new openhmis.OperationType({ name: __("Any") }));
                });

                var stockroomCollection = new openhmis.GenericCollection([], { model: openhmis.Stockroom });
                stockroomCollection.on("reset", function(collection) {
                    collection.unshift(new openhmis.Stockroom({ name: __("Any") }));
                });

                this.form = new Backbone.Form({
                    className: "inline",
                    schema: {
                        operation_status: {
                            title: __("Status"),
                            type: "Select",
                            options: this.STATUSES
                        },
                        operationType_uuid: {
                            title: __(openhmis.getMessage('openhmis.inventory.operations.type.name')),
                            type: "Select",
                            options: operationTypeCollection
                        },
                        stockroom_uuid: {
                            title: __(openhmis.getMessage('openhmis.inventory.stockroom.name')),
                            type: "Select",
                            options: stockroomCollection
                        },
                        operation_item: {
                            title: __(openhmis.getMessage('openhmis.inventory.item.name')),
                            type: "Text",
                            editorClass: "search"
                        }
                    }
                });

                if (options.operation_status) {
                    this.searchFilter = { operation_status: options.operation_status};
                }
            },

            getFetchOptions: function(options) {
                options = options ? options : {};
                if (this.searchFilter) {
                    for (var filter in this.searchFilter) {
                        if (this.searchFilter[filter] != "Any" && this.searchFilter[filter] !="") {
                            if (filter == "operation_item") {
                                options.queryString = openhmis.addQueryStringParameter(options.queryString, "operationItem_uuid" + "=" + $("#item-uuid").val());
                                this.item_uuid = $("#item-uuid").val();
                            } else {
                                options.queryString = openhmis.addQueryStringParameter(options.queryString, filter + "=" +
                                encodeURIComponent(this.searchFilter[filter]));
                            }
                        }
                    }
                }

                return options;
            },

            focus: function() {
                this.$("#q").focus();
                if (this.item_uuid != "") {
                	$('#item-uuid').val(this.item_uuid);
                }
            },

            commitForm: function() {
                var filters = this.form.getValue();

                if (!filters.operation_status) {
                    this.searchFilter = undefined;
                } else {
                    this.searchFilter = filters;
                }
            },

            render: function() {
                this.$el.html(this.template({ __: __ }));
                this.$("div.box").append(this.form.render().el);

                if (this.searchFilter) {
                   this.form.setValue(this.searchFilter);
                }

                this.$("form").addClass("inline");
                this.$("form ul").append('<button id="submit">'+__("Search")+'</button>');
                this.$("#operation_item").autocomplete({
                    minLength: 2,
                    source: doSearch,
                    select: selectItem
                })
                .data("autocomplete")._renderItem = function (ul, item) {
                return $("<li></li>").data("item.autocomplete", item)
                    .append("<a>" + item.label + "</a>").appendTo(ul);
                };
                return this;
            }
        });

        openhmis.SearchByOperationItemView = openhmis.BaseSearchView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/search.html',
            tmplSelector: '#operation-item-search',


            initialize: function(options) {
            	this.itemSelector = "#item-uuid";
                this.events['change #item-uuid'] = 'onFormSubmit';

                this.item_uuid = "";

                openhmis.BaseSearchView.prototype.initialize.call(this, options);
                this.form = new Backbone.Form({
                    className: "inline",
                    schema: {
                        operation_item: {
                            title: __(openhmis.getMessage('openhmis.inventory.item.name')),
                            type: "Text",
                            editorClass: "search"
                        }
                    }
                });
            },

            getFetchOptions: function(options) {
                options = options ? options : {};
                if (this.searchFilter) {
                    for (var filter in this.searchFilter) {
                        if (this.searchFilter[filter] !="") {
                            if (filter == "operation_item") {
                                options.queryString = openhmis.addQueryStringParameter(options.queryString, "operationItem_uuid" + "=" + $(this.itemSelector).val());
                                this.item_uuid = $(this.itemSelector).val();
                            } else {
                                options.queryString = openhmis.addQueryStringParameter(options.queryString, filter + "=" +
                                encodeURIComponent(this.searchFilter[filter]));
                            }
                        }
                    }
                }

                return options;
            },

            focus: function() {
                if (this.item_uuid != "") {
                	$(this.itemSelector).val(this.item_uuid);
                }
            },

            commitForm: function() {
                var filters = this.form.getValue();
                this.searchFilter = filters;
            },

            render: function() {
                this.$el.html(this.template({ __: __ }));
                this.$("div.box").append(this.form.render().el);

                if (this.searchFilter) {
                   this.form.setValue(this.searchFilter);
                }

                this.$("form").addClass("inline");
                this.$("form ul").append('<button id="submit">'+__("Search")+'</button>');
                this.$("#operation_item").autocomplete({
                    minLength: 2,
                    source: doSearch,
                    select: selectItem
                })
                .data("autocomplete")._renderItem = function (ul, item) {
                return $("<li></li>").data("item.autocomplete", item)
                    .append("<a>" + item.label + "</a>").appendTo(ul);
                };
                return this;
            }
        });

        openhmis.SearchByTransactionItemView = openhmis.BaseSearchView.extend({
            tmplFile: openhmis.url.inventoryBase + 'template/search.html',
            tmplSelector: '#transaction-item-search',


            initialize: function(options) {
            	this.itemSelector = "#transaction-item-uuid";
                this.events['change #transaction-item-uuid'] = 'onFormSubmit';

                this.item_uuid = "";

                openhmis.BaseSearchView.prototype.initialize.call(this, options);
                this.form = new Backbone.Form({
                    className: "inline",
                    schema: {
                        transaction_item: {
                            title: __(openhmis.getMessage('openhmis.inventory.item.name')),
                            type: "Text",
                            editorClass: "search"
                        }
                    }
                });
            },

            getFetchOptions: function(options) {
                options = options ? options : {};
                if (this.searchFilter) {
                    for (var filter in this.searchFilter) {
                        if (this.searchFilter[filter] !="") {
                            if (filter == "transaction_item") {
                                options.queryString = openhmis.addQueryStringParameter(options.queryString, "transactionItem_uuid" + "=" + $(this.itemSelector).val());
                                this.item_uuid = $(this.itemSelector).val();
                            } else {
                                options.queryString = openhmis.addQueryStringParameter(options.queryString, filter + "=" +
                                encodeURIComponent(this.searchFilter[filter]));
                            }
                        }
                    }
                }

                return options;
            },

            focus: function() {
                if (this.item_uuid != "") {
                	$(this.itemSelector).val(this.item_uuid);
                }
            },

            commitForm: function() {
                var filters = this.form.getValue();
                this.searchFilter = filters;
            },

            render: function() {
                this.$el.html(this.template({ __: __ }));
                this.$("div.box").append(this.form.render().el);

                if (this.searchFilter) {
                   this.form.setValue(this.searchFilter);
                }

                this.$("form").addClass("inline");
                this.$("form ul").append('<button id="submit">'+__("Search")+'</button>');
                this.$("#transaction_item").autocomplete({
                    minLength: 2,
                    source: doSearch,
                    select: selectTransactionItem
                })
                .data("autocomplete")._renderItem = function (ul, item) {
                return $("<li></li>").data("item.autocomplete", item)
                    .append("<a>" + item.label + "</a>").appendTo(ul);
                };
                return this;
            },

        });

        //needed because otherwise there would be same ids twice on the reports page
        function selectTransactionItem(event, ui) {
            var uuid = ui.item.val;
            var name = ui.item.label;
            $('#transaction-item').val(name);
            $('#transaction-item-uuid').val(uuid).trigger('change');
        };

        return openhmis;
    }
)
