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
        openhmis.url.backboneBase + 'js/openhmis',
        openhmis.url.backboneBase + 'js/lib/backbone-forms',
        openhmis.url.backboneBase + 'js/model/generic',
        openhmis.url.backboneBase + 'js/view/generic',
        openhmis.url.backboneBase + 'js/view/list',
        openhmis.url.backboneBase + 'js/view/paginate',
        openhmis.url.backboneBase + 'js/view/editors',
        'link!' + openhmis.url.backboneBase + 'css/style.css',
        'link!/openmrs/scripts/jquery/dataTables/css/dataTables_jui.css'
    ],
    function($, _, Backbone, __, openhmis) {

        openhmis.startItemConceptSuggestionScreen = function(model, options) {
            var options = {};
            if (!options.listElement) {
                $("#content").append('<div id="existing-form"></div>');
                options.listElement = $("#existing-form");
            }

            var collection = new openhmis.GenericCollection([], {
                model: model
            });
            collection.fetch({
                success: function() {
                    $(".spinner").hide();
                }
            });

            var viewOptions = _.extend({
                model: collection,
            }, options);

            var listViewType = openhmis.ItemToConceptMappingListView;
            var listView = new listViewType(viewOptions);

            listView.setElement(options.listElement);
            listView.fetch();
        },

        openhmis.ItemToConceptMappingListView = Backbone.View.extend({

                    tmplFile: openhmis.url.inventoryBase + 'template/itemConceptSuggestion.html',
                    tmplSelector: '#item-concept-suggestion-list',

                    /** The default ListItemView to use to display each item */
                    itemView: openhmis.GenericListItemView,

                    /**
                     * A list of other FetchHelpers that may affect the fetch results
                     * for this view.  A FetchHelper must implement the
                     * <b>getFetchOptions<b> method which will return
                     *
                     * @type Array
                     */
                    fetchable: null,

                    initialize: function(options) {
                        var itemView = this.itemView; // bindAll can messes this up for extending classes
                        _.bindAll(this);
                        this.itemView = itemView;
                        this.options = {};

                        this.paginateView = new openhmis.PaginateView({ model: this.model, pageSize: 5 });
                        this.paginateView.on("fetch", this.fetch);
                        this.fetchable = [];
                        this.fetchable.push(this.paginateView);

                        // Load options
                        if (options !== undefined) {
                            this.itemView = options.itemView ? options.itemView : openhmis.GenericListItemView;
                            if (options.schema) this.schema = options.schema;

                            // Why is this inside options??
                            this.template = this.getTemplate();

                            this.options.listTitle = options.listTitle;

                            this.options.itemActions = options.itemActions || [];
                            var itemViewActions = this.itemView.prototype.actions;
                            if (itemViewActions) this.options.itemActions = this.options.itemActions.concat(itemViewActions);

                            this.options.includeFields = options.listFields;
                            this.options.excludeFields = options.listExcludeFields;
                            this.options.showPaging = options.showPaging !== undefined ? options.showPaging : true;
                            if (options.pageSize) this.paginateView.setPageSize(options.pageSize);
                            this.options.showRetiredOption = options.showRetiredOption !== undefined ? options.showRetiredOption : true;
                            this.options.hideIfEmpty = options.hideIfEmpty !== undefined ? options.hideIfEmpty : false;
                        }

                        this.model.on("reset", this.render);

                        this.showRetired = false;
                        this._determineFields();

                    },

                    focus: function() {
                        if (this.selectedItem) {
                            this.selectedItem.focus();
                        }
                    },

                    fetch: function(options, sender) {
                        options = options ? options : {};
                        for (var f in this.fetchable) {
                            if (this.fetchable[f] !== sender)
                                options = this.fetchable[f].getFetchOptions(options);
                        }

                        if(this.showRetired) {
                            options.queryString = openhmis.addQueryStringParameter(options.queryString, "includeAll=true");
                        }

                        this.trigger("fetch", options, this);
                        this.model.fetch(options);
                    },

                    render: function(extraContext) {
                        var self = this;
                        var length = this._visibleItemCount();
                        if (length === 0 && this.options.hideIfEmpty) {
                            this.$el.html("");
                            return this;
                        }
                        var schema = _.extend({}, this.model.model.prototype.schema, this.schema || {});
                        var pagingEnabled = this.options.showPaging && length > 0;
                        var context = {
                            list: this.model,
                            listLength: length,
                            fields: this.fields,
                            modelType: this.model.model.prototype,
                            modelMeta: this.model.model.prototype.meta,
                            modelSchema: schema,
                            showRetired: this.showRetired,
                            pagingEnabled: pagingEnabled,
                            options: this.options
                        }
                        if (extraContext !== undefined) {
                            if (extraContext.options) {
                                context.options = _.extend({}, context.options, extraContext.options);
                                delete extraContext.options;
                            }
                            context = _.extend(context, extraContext);
                        }
                        this.$el.html(this.template(context));
                        return this;
                    },

                    /**
                     * Reassigns alternating styles to item views.
                     *
                     * @private
                     */
                    _colorRows: function() {
                        var lineNumber = 0;
                        this.$el.find('tbody tr').each(function() {
                            $(this)
                            .removeClass("evenRow oddRow")
                            .addClass((lineNumber % 2 === 0) ? "evenRow" : "oddRow");
                            lineNumber++;
                        });
                    },

                    /**
                     * Determine the number of items in the collection that are
                     * actually visible according to UI settings
                     *
                     * @private
                     */
                    _visibleItemCount: function() {
                        if (this.showRetired)
                            return this.model.length;
                        return this.model.filter(function(item) { return !item.isRetired() }).length;
                    },

                    /**
                     * Determine the fields that should be shown as columns in the table
                     *
                     * @private
                     */
                    _determineFields: function() {
                        if (this.options.includeFields !== undefined)
                            this.fields = this.options.includeFields;
                        else
                            this.fields = _.keys(this.model.model.prototype.schema);
                        if (this.options.excludeFields !== undefined) {
                            var argv = _.clone(this.options.excludeFields);
                            argv.unshift(this.fields);
                            this.fields = _.without.apply(this, argv);
                        }
                    },
                });

        return openhmis;
    }
);