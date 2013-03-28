define(
	[
		openhmis.url.backboneBase + 'js/view/generic'
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