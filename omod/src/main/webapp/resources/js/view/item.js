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
        openhmis.url.backboneBase + 'js/view/editors',
        openhmis.url.backboneBase + 'js/lib/backbone-forms',
    ],
    function(openhmis) {
        openhmis.ItemAddEditView = openhmis.GenericAddEditView.extend({

            initialize: function(options) {
                this.events = _.extend({}, this.events, {
                'blur input[name="name"]': 'updateDrugAndConcept',
            });
            openhmis.GenericAddEditView.prototype.initialize.call(this, options);
            _.bindAll(this);
            },

            prepareModelForm: function(model, options) {
                var modelForm = openhmis.GenericAddEditView.prototype.prepareModelForm.call(this, model, options);
                modelForm.on('prices:change', this.updatePriceOptions);
                return modelForm;
            },

            updatePriceOptions: function() {
                this.model.setPriceOptions(this.modelForm.fields['prices'].getValue());
                this.modelForm.fields['defaultPrice'].editor.schema.options = this.model.schema.defaultPrice.options;
                this.modelForm.fields['defaultPrice'].editor.render();
            },

            updateDrugAndConcept: function() {
                var modelName = this.model.attributes.name;
                var modelFormName = this.modelForm.fields['name'].getValue();
                if (!_.isEqual(modelName, modelFormName) && !_.isEmpty(modelFormName)) {
                    this.collections(modelFormName);
                }
            },

            collections: function(itemName) {
                var term = itemName;
                var conceptCollection = new openhmis.GenericCollection([], {
                    model: openhmis.Concept,
                    url: 'v1/concept?q=' + term
                });

                var drugCollection = new openhmis.GenericCollection([], {
                    model: openhmis.Drug,
                    url: 'v1/drug?q=' + term
                });

                var self = this;
                conceptCollection.fetch({
                    success: function(collection, resp) {
                    	$('#conceptLink').remove();
                        $('.conceptSelector').remove();
                        if (collection.length > 0) {
                            self.renderConceptCollection(collection)
                        } else {
                        	$('#conceptMessage').append('No concept linked to this Item');
                        }
                    },

                });

                drugCollection.fetch({
                    success: function(collection, resp) {
                        $('#drugLink').remove();
                        $('.drugSelector').remove();
                        if (collection.length > 0) {
                            self.renderDrugCollection(collection)
                        } else {
                        	$('#drugMessage').append('No drug linked to this Item');
                        }
                    },

                });
                this.modelForm.fields.concept.editor.value = null;
                this.modelForm.fields.drug.editor.value = null;
            },

            renderConceptCollection: function(collection) {
                var id = this.model.cid;
                var selector = '<select id="' + id + '_concept" class="conceptSelector" >';
                selector += '<option value=""><em>--Not defined--</em></option>';
                collection.each(function (entry) {
                  selector += '<option value="' + entry.id + '">' + entry.get("display")+'</option>';
                });
                selector += '</select><input type="hidden" class="concept-uuid" name="concept"/>';
                $('#conceptSelect').append(selector);
                $('#conceptMessage').hide();
                $('#conceptLink').hide();
                return this;
            },

            renderDrugCollection: function(collection) {
                var id = this.model.cid;
                var selector = '<select id="' + id + '_drug" class="drugSelector" >';
                selector += '<option value=""><em>--Not defined--</em></option>';
                collection.each(function (entry) {
                  selector += '<option value="' + entry.id + '">' + entry.get("display")+'</option>';
                });
                selector += '</select><input type="hidden" class="drug-uuid" name="drug"/>';
                $('#drugSelect').append(selector);
                $('#drugMessage').hide();
                $('#drugLink').hide();
                return this;
            },

            save: function(event) {
                if(this.modelForm.fields.concept.editor.value && _.isObject(this.modelForm.fields.concept.editor.value)) {
                    this.modelForm.fields.concept.editor.value = this.modelForm.fields.concept.editor.value.uuid
                }
                if(this.modelForm.fields.drug.editor.value && _.isObject(this.modelForm.fields.drug.editor.value)) {
                    this.modelForm.fields.drug.editor.value = this.modelForm.fields.drug.editor.value.uuid
                }
                openhmis.GenericAddEditView.prototype.save.call(this, event);
            },

        });

        return openhmis;
    }
);