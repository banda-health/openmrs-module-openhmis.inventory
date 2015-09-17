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
        openhmis.url.backboneBase + 'js/view/generic',
        openhmis.url.backboneBase + 'js/view/openhmis',
        openhmis.url.backboneBase + 'js/view/editors',
        openhmis.url.backboneBase + 'js/lib/backbone-forms',
        openhmis.url.backboneBase + 'js/model/concept'
    ],
    function(openhmis) {
        openhmis.ItemAddEditView = openhmis.GenericAddEditView.extend({
            initialize: function(options) {
                _.bindAll(this);
                this.events = _.extend({}, this.events, {
                    'click [data-action="remove-concept"]' : 'onRemove',
                    'change [name="hasExpiration"]' : 'showDefaultExpirationPeriodField'
                });
                openhmis.GenericAddEditView.prototype.initialize.call(this, options);

                this.itemStockView = new openhmis.GenericListView({
                    model: new openhmis.GenericCollection([], {
                        model: openhmis.ItemStock
                    }),
                    showRetiredOption: false,
                    showRetired: true,
                    listTitle: openhmis.getMessage('openhmis.inventory.item.stock.name'),
                    listFields: ['stockroom', 'quantity']
                });

                this.itemStockView.on("fetch", this.fetch);
            },

            fetch: function(options) {
                options.queryString = openhmis.addQueryStringParameter(options.queryString, "item_uuid=" + this.model.id);
            },

            prepareModelForm: function(model, options) {
                var modelForm = openhmis.GenericAddEditView.prototype.prepareModelForm.call(this, model, options);
                modelForm.on('prices:change', this.updatePriceOptions);
                return modelForm;
            },

            render: function() {
                openhmis.GenericAddEditView.prototype.render.call(this);

                if (this.model.id) {
                    var el = this.$(".submit");

                    // Find or create the attributes element
                    this.$attributes = this.$("#itemAttributes");
                    if (!this.$attributes.length) {
                        // Find the element that the attributes should be added after
                        el.before("<form id='itemAttributes' class='bbf-form' />");
                        this.$attributes = this.$("#itemAttributes");
                    }

                    // Load and display the item attributes
                    var self = this;
                    openhmis.renderAttributesFragment(this.$attributes, "Item", null, {
                        success: function() {
                            openhmis.displayAttributes(self.$attributes, self.model.get('attributes'));
                        }
                    });

                    this.itemStockView.fetch(null);
                    el.before(this.itemStockView.el);

                    // Set the itemstock element id so we can style it
                    this.$(this.itemStockView.el).attr("id", "itemStock");
                }
            },

            updatePriceOptions: function() {
                this.model.setPriceOptions(this.modelForm.fields['prices'].getValue());
                this.modelForm.fields['defaultPrice'].editor.schema.options = this.model.schema.defaultPrice.options;
                this.modelForm.fields['defaultPrice'].editor.render();
            },
            
            onRemove: function() {
                $('#conceptLink').hide();
                $('#conceptMessage').hide();
                $('#conceptBox').show();
                this.modelForm.fields.concept.editor.value = '';
            },
            
            edit: function(model) {
            	this.model = model;
				var self = this;
				this.model.fetch({
					success: function(model, resp) {
						self.render();
						$('.addLink').hide();
						$(self.titleEl).show();
						self.modelForm = self.prepareModelForm(self.model);
						$(self.formEl).prepend(self.modelForm.el);
						self.showDefaultExpirationPeriodField();
						
						if (model.attributes.concept != null) {
							var concept = model.attributes.concept;
		                     $('#conceptBox').hide();
		                     $('#conceptLink').append('<button type="button" data-action="remove-concept" class="bbf-remove" title="Remove">×</button>' +
	                    				'<a href="/openmrs/module/openhmis/backboneforms/concept.form?conceptUuid=' + 
	                    				concept.attributes.uuid +'" target="_blank">' + concept.attributes.display +'</a>');
		                }
		                
						$(self.formEl).show();
						$(self.retireVoidPurgeEl).show();
						$(self.formEl).find('input')[0].focus();
					},
					error: openhmis.error
				});
               
            },
            
            showDefaultExpirationPeriodField: function() {
                if (this.modelForm.fields['hasExpiration'].getValue() === true) {
                    $('.field-defaultExpirationPeriod').show();
                    $('#defaultExpirationPeriod').show();
                    $('#defaultExpirationPeriodText').hide();
                } else if (this.modelForm.fields['defaultExpirationPeriod'].getValue() != null && this.modelForm.fields['defaultExpirationPeriod'].getValue() != '') {
                    $('#defaultExpirationPeriodText').contents().remove();
                    $('#defaultExpirationPeriodText').append(this.modelForm.fields['defaultExpirationPeriod'].getValue());
                    $('#defaultExpirationPeriod').hide();
                    $('#defaultExpirationPeriodText').show();
                } else {
                    $('.field-defaultExpirationPeriod').hide();
                	
                }
            },
            
            beginAdd: function() {
                openhmis.GenericAddEditView.prototype.beginAdd.call(this);
                this.showDefaultExpirationPeriodField();
            },
            
            save: function(event) {
                if(this.modelForm.fields.concept.editor.value && _.isObject(this.modelForm.fields.concept.editor.value)) {
                    this.modelForm.fields.concept.editor.value = this.modelForm.fields.concept.editor.value.uuid
                } else {
                    this.modelForm.fields.concept.editor.value = this.$('#concept').val();
                }

                // Load the attributes and set in the model
                var attributes = openhmis.loadAttributes(this, this.$attributes, openhmis.ItemAttribute);
                if (attributes) {
                    this.model.set("attributes", attributes);
                } else if (attributes === false) {
                    // The loadAttributes returns false if there was an error so halt the save if we got that
                    return false;
                }

                openhmis.GenericAddEditView.prototype.save.call(this, event);
            }
            
        });

        return openhmis;
    }
);
