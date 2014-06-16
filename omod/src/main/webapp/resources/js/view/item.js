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
        openhmis.url.backboneBase + 'js/model/drug',
        openhmis.url.backboneBase + 'js/model/concept'
    ],
    function(openhmis) {
        openhmis.ItemAddEditView = openhmis.GenericAddEditView.extend({

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

            beginAdd: function(event) {
                openhmis.GenericAddEditView.prototype.beginAdd.call(this, event);
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
                return this;
            },

            edit: function(model) {
                openhmis.GenericAddEditView.prototype.edit.call(this, model);
                var self = this;

                return this;

            },

            doConceptSearch: function(request, response) {
                var term = request.term;
                var query = "?q=" + encodeURIComponent(term);
                var urlPrefix = "/openmrs/ws/rest/v1/concept";
                this.doSearch(request, response, openhmis.Concept, urlPrefix, query);
            },

            doSearch: function(request, response, model, urlPrefix, query) {
                var term = request.term;
//                if (query in this.cache) {
//                  response(this.cache[query]);
//                  return;
//                }
                var resultCollection = new openhmis.GenericCollection([], { model: model });
                var view = this;
                var fetchQuery = query ? query : "?q=" + encodeURIComponent(term);
                resultCollection.fetch({
                  url: urlPrefix + fetchQuery,
                  success: function(collection, resp) {
                    var data = collection.map(function(model) { return {
                      val: model.id,
                      display: model.get('display'),
                    }});
//                    view.cache[query] = data;
                    response(data);
                  }
                });
              },

              selectConcept: function(event, ui) {
                var uuid = ui.item.val;
                var name = ui.item.display;
                this.$('.concept-display').val(name);
                this.$('.concept').val(uuid);
                //this.value = new openhmis.Concept({ uuid: uuid });
              },

            save: function(event) {
                if(this.modelForm.fields.concept.editor.value && _.isObject(this.modelForm.fields.concept.editor.value)) {
                    this.modelForm.fields.concept.editor.value = this.modelForm.fields.concept.editor.value.uuid
                } else {
                    this.modelForm.fields.concept.editor.value = this.$('#concept').val();
                }
                if(this.modelForm.fields.drug.editor.value && _.isObject(this.modelForm.fields.drug.editor.value)) {
                    this.modelForm.fields.drug.editor.value = this.modelForm.fields.drug.editor.value.uuid
                } else {
                	this.modelForm.fields.drug.editor.value = this.$('#drug').val();
                }
                openhmis.GenericAddEditView.prototype.save.call(this, event);
            },


        });

        return openhmis;
    }
);