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
        openhmis.url.inventoryBase + 'js/model/itemConceptSuggestionList',
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

            var viewOptions = openhmis.fetchData(model, options);
            var listViewType = openhmis.ItemToConceptMappingListView;
            var listView = new listViewType(viewOptions);
            listView.setElement(options.listElement);
        },
        
        openhmis.fetchData = function(model, options) {
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
            
            return viewOptions;
        },
        
        openhmis.renderData = function(model, options) {
        	
        },

        openhmis.ItemToConceptMappingListView = openhmis.GenericListView.extend({

            tmplFile: openhmis.url.inventoryBase + 'template/itemConceptSuggestion.html',
            tmplSelector: '#item-concept-suggestion-list',

            itemView: openhmis.ItemConceptSuggestionListView,

            initialize: function(options) {
                var itemView = this.itemView; // bindAll can messes this up for extending classes
                _.bindAll(this);
                this.itemView = itemView;
                this.options = {};
                this.fetchable = [];

                // Load options
                if (options !== undefined) {
                    this.itemView = openhmis.ItemConceptSuggestionListView;
                    if (options.schema) {
                        this.schema = options.schema;
                    }

                    // Why is this inside options??
                    this.template = this.getTemplate();
                    this.options.listTitle = options.listTitle;
                    this.options.itemActions = options.itemActions || [];
                    var itemViewActions = this.itemView.prototype.actions;
                    if (itemViewActions) {
                    	this.options.itemActions = this.options.itemActions.concat(itemViewActions);
                    }

                    this.options.includeFields = options.listFields;
                    this.options.excludeFields = options.listExcludeFields;
                    this.options.showPaging = false;
                    this.options.showRetiredOption = false;
                    this.options.hideIfEmpty = options.hideIfEmpty !== undefined ? options.hideIfEmpty : false;
                }
                this.model.on("reset", this.render);
                this.showRetired = false;
                this._determineFields();
            },

            events: {
            	'click .cancel' : 'cancelView',
            	'click .submitNext' : function(event) {
            		this.save(true);
            	},
            	'click .submit' : function(event) {
            		this.save(false);
            	},
	            'click .selectAll' : 'selectAll'
            },
            
            cancelView: function() {
            	window.location.href = $('#returnUrl').val();
            },


            fetch: function(options, sender) {
                options = options ? options : {};
                for (var f in this.fetchable) {
                    if (this.fetchable[f] !== sender) {
                        options = this.fetchable[f].getFetchOptions(options);
                    }
                }
                this.trigger("fetch", options, this);
                this.model.fetch(options);
            },

            addOne: function(model, schema, lineNumber) {
                if ((this.$el.html() === "" && this.options.hideIfEmpty === true)
                    || this.$("p.empty").length === 1) {
                    this.render();
                    // Re-rendering the entire list means we don't have to
                    // continue adding this item
                    return null;
                }
                schema = schema ? _.extend({}, model.schema, schema) : _.extend({}, this.model.model.prototype.schema, this.schema || {});

                // Determine class name for alternating row styling
                var className = "evenRow";
                if (lineNumber && !isNaN(lineNumber)) {
                    className = lineNumber % 2 === 0 ? "evenRow" : "oddRow";
                } else {
                    var $rows = this.$('tbody.list tr');
                    if ($rows.length > 0) {
                        var lastRow = $rows[$rows.length - 1];
                        if ($(lastRow).hasClass("evenRow")) {
                            className = "oddRow";
                        }
                    }
                }

                var itemView = new this.itemView({
                    model: model,
                    fields: this.fields,
                    schema: schema,
                    className: className,
                    actions: this.options.itemActions
                });
                model.view = itemView;
                this.$('tbody.list').append(itemView.render().el);
                return itemView;
            },

            render: function(extraContext) {
                var self = this;
                var length = this._visibleItemCount();
                if (length === 0 && this.options.hideIfEmpty) {
                    this.$el.html("");
                    return this;
                }
                var schema = _.extend({}, this.model.model.prototype.schema, this.schema || {});
                var context = {
                    list: this.model,
                    listLength: length,
                    fields: this.fields,
                    modelType: this.model.model.prototype,
                    modelMeta: this.model.model.prototype.meta,
                    modelSchema: schema,
                    showRetired: this.showRetired,
                    pagingEnabled: false,
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
                this.$('tbody.list').append('<tr class="evenRow"><td></td><td></td><td><input id="selectAll" type="checkbox" name="selectAll" class="selectAll" value="Select All"/><b>&nbsp;Select all </b></td></tr>');
                var view = this;
                var lineNumber = 1;
                this.model.each(function(model) {
                    view.addOne(model, schema, lineNumber)
                    lineNumber++;
                });
                return this;
            },
            
            save: function(loadNextItems) {
            	var view = this;
            	var itemConceptSuggestionList = new openhmis.ItemConceptSuggestionList();
            	itemConceptSuggestionList.set("itemConceptSuggestions", this.model.models);
            	
            	itemConceptSuggestionList.save(null, {
					success: function(itemConceptSuggestionList, resp) {
						if (loadNextItems === false) {
							window.location.assign($('#returnUrl').val());
						} else {
							$("#existing-form").empty();
							$('.spinner').show();
							var viewOptions = openhmis.fetchData(view.model.model, null);
				            var listViewType = openhmis.ItemToConceptMappingListView;
				            var listView = new listViewType(viewOptions);
				            listView.setElement($("#existing-form"));
						}
					},
					error: function(itemConceptSuggestionList, resp) { 
						openhmis.error(resp); 
					}
				});
            },

	        selectAll: function() {
                if($('#selectAll').is(':checked')) {
                	$('.conceptAccepted').prop('checked',true)
                	this.model.each(function(listEntry) {
                		listEntry.set('conceptAccepted', true);
                	});
                } else {
                	$('.conceptAccepted').prop('checked',false)
                	this.model.each(function(listEntry) {
                		listEntry.set('conceptAccepted', false);
                	});
                    $('#selectAll').prop('checked',false);
                }
	        }

        });

        openhmis.ItemConceptSuggestionListView = openhmis.GenericListItemView.extend({
			tagName: "tr",
			tmplFile: openhmis.url.inventoryBase + 'template/itemConceptSuggestion.html',
			tmplSelector: '#list-item',
			
			initialize: function(options) {
	            _.bindAll(this);
	            openhmis.GenericListItemView.prototype.initialize.call(this, options);
	            this.cache = {};
	            this.template = this.getTemplate();
	        },
			
			events: {
	            'blur .concept-display': 'handleBlur',
	            'change .conceptAccepted' : 'toggleCheckbox'
	        },

	        handleBlur: function() {
	        	var conceptInputFieldId = this.model.cid +'_conceptDisplay';
	        	this.handleSpinnerHide();
	            if ($('#' + conceptInputFieldId).val() == '') {
	            	var conceptHiddenFieldId = this.model.cid + '_concept';
	            	this.$('#' + conceptHiddenFieldId).val('');
	            	this.model.attributes.conceptName = '';
	            	this.model.attributes.conceptUuid = '';
	            }
	        },
	        
	        toggleCheckbox: function() {
	        	var currentValue = this.model.attributes.conceptAccepted;
	        	this.model.attributes.conceptAccepted = !currentValue;
	        },
	        
	        getValue: function() {
	            return this.value;
	        },

	        doConceptSearch: function(request, response) {
	            var term = request.term;
	            var query = "?q=" + encodeURIComponent(term);
	            this.doStockSearch(request, response, openhmis.Concept, query);
	          },

	        doStockSearch: function(request, response, model, query) {
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
	                success: function(collection, resp) {
	                    view.handleSpinnerHide();
	                    var data = collection.map(function(model) { return {
	                        val: model.id,
	                        display: model.get('display'),
	                    }});
	                    view.cache[query] = data;
	                    response(data);
	                }
	            });
	        },

	        selectConcept: function(event, ui) {
	            var uuid = ui.item.val;
	            var name = ui.item.display;
	            this.$('.concept-display').val(name);
	            this.$('.concept').val(uuid);
	            this.model.attributes.conceptName = name;
	            this.model.attributes.conceptUuid = uuid;
	            event.preventDefault();
	        },
	        
	        render: function() {
	        	this.$el.html(this.template({
					model: this.model,
					actions: this.actions,
					fields: this.fields,
					GenericCollection: openhmis.GenericCollection
				}));
	        	if (this.model.attributes.conceptName) {
	        		this.$('.concept-display').val(this.model.attributes.conceptName);
	        	}
	        	this.$('.concept-display').autocomplete({
	                minLength: 2,
	                source: this.doConceptSearch,
	                select: this.selectConcept
	              })
	              // Tricky stuff here to get the autocomplete list to render with our custom data
	              .data("autocomplete")._renderItem = function(ul, concept) {
	                return $("<li></li>").data("concept.autocomplete", concept)
	                  .append("<a>" + concept.display + "</a>").appendTo(ul);
	            };
	            this.handleSpinnerHide();
	            return this;
	        },
	        
	        handleSpinnerHide: function () {
	            this.$('.concept-display').removeClass('spinner-float-style');
	            this.$('.spinner').hide();
	        },

	        handleSpinnerShow: function () {
	        	this.$('.concept-display').addClass('spinner-float-style');
	        	this.$('.spinner').show();
	        },
		});

        return openhmis;
    }
);
