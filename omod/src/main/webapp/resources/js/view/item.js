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
			prepareModelForm: function(model, options) {
				var modelForm = openhmis.GenericAddEditView.prototype.prepareModelForm.call(this, model, options);
				modelForm.on('prices:change', this.updatePriceOptions);
				return modelForm;
			},

			updatePriceOptions: function() {
				this.model.setPriceOptions(this.modelForm.fields['prices'].getValue());
				this.modelForm.fields['defaultPrice'].editor.schema.options = this.model.schema.defaultPrice.options;
				this.modelForm.fields['defaultPrice'].editor.render();
			}
		});

		return openhmis;
	}
);