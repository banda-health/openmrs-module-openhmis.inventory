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
		openhmis.url.backboneBase + 'js/lib/underscore',
		openhmis.url.backboneBase + 'js/model/generic',
		openhmis.url.backboneBase + 'js/lib/i18n',
        openhmis.url.backboneBase + 'js/model/openhmis',
		openhmis.url.inventoryBase + 'js/model/department',
        openhmis.url.backboneBase + 'js/model/concept'
	],
	function(_, openhmis, __) {
		openhmis.ItemCode = openhmis.GenericModel.extend({
			meta: {
				name: openhmis.getMessage('openhmis.inventory.item.code.name'),
				namePlural: openhmis.getMessage('openhmis.inventory.item.code.namePlural'),
				openmrsType: 'metadata',
				restUrl: openhmis.url.inventoryModelBase + 'itemCode',
				confirmDelete: openhmis.getMessage('openhmis.inventory.item.delete.confirm.itemCode')
			},
			schema: {
				code: {
					type: 'Text',
					validators: [
						{ type: 'required', message: openhmis.getMessage('openhmis.inventory.item.required.itemCode') }
					]
				}
			},
			toString: function() { return this.get('code'); }
		});

		openhmis.ItemPrice = openhmis.GenericModel.extend({
			meta: {
				name: openhmis.getMessage('openhmis.inventory.item.price.name'),
				namePlural: openhmis.getMessage('openhmis.inventory.item.price.namePlural'),
				openmrsType: 'metadata',
				restUrl: openhmis.url.inventoryModelBase + 'itemPrice',
				confirmDelete: openhmis.getMessage('openhmis.inventory.item.delete.confirm.itemPrice')
			},
			schema: {
				name: { type: "Text" },
				price: {
					type: 'BasicNumber',
					validators: [
						{ type: 'required', message: openhmis.getMessage('openhmis.inventory.item.required.price') }
					]
				}
			},

		    set: function(key, value, options) {
				if (typeof key === "string") {
					switch (key) {
						case "price":
							value = parseFloat(value);
							break;
					}
				}
				return openhmis.GenericModel.prototype.set.call(this, key, value, options);
			},

			format: function(price) {
				if (price === undefined) {
					return 0;
				}
				if (price instanceof openhmis.ItemPrice) {
					return price.toString();
				}
				return price.toFixed(2);
			},

			toString: function() {
				var name = this.get("name") ? " (" + this.get("name") + ")" : "";
				return this.format(this.get('price')) + name;
			},

			toJSON: function() {
				var attributes = openhmis.GenericModel.prototype.toJSON.call(this);
				if (this.get('uuid') != null && this.get('uuid') != undefined) {
					attributes[this.idAttribute] = this.attributes[this.idAttribute];
				}
				return attributes;
			}
		});

        openhmis.ItemAttributeType = openhmis.AttributeTypeBase.extend({
            meta: {
                restUrl: openhmis.url.inventoryModelBase + 'itemAttributeType',
                confirmDelete: openhmis.getMessage('openhmis.inventory.item.delete.confirm.AttributeType')
            }
        });

        openhmis.ItemAttribute = openhmis.AttributeBase.extend({
            attributeTypeClass: openhmis.ItemAttributeType,
            attributeTypeEditor: 'ItemAttributeTypeSelect'
        });

        openhmis.Item = openhmis.CustomizableBase.extend({
            attributeClass: openhmis.ItemAttribute,

            meta: {
				name: openhmis.getMessage('openhmis.inventory.item.name'),
				namePlural: openhmis.getMessage('openhmis.inventory.item.namePlural'),
				openmrsType: 'metadata',
				restUrl: openhmis.url.inventoryModelBase + 'item',
				confirmDelete: openhmis.getMessage('openhmis.inventory.item.delete.confirm.Item')
			},
			schema: {
				name: { type: 'Text' },
				department: {
					type: 'DepartmentSelect',
					options: new openhmis.GenericCollection(null, {
						model: openhmis.Department,
						url: openhmis.url.inventoryModelBase + 'department'
					}),
					objRef: true
				},
				hasExpiration: { type: "TrueFalseCheckbox" },
				defaultExpirationPeriod: { type: 'DefaultExpirationPeriodStepper' },
				concept: { type: 'ConceptInput'},
				hasPhysicalInventory: { type: "TrueFalseCheckbox" },
				minimumQuantity: { type: "BasicNumber" },
				buyingPrice: { type: "BasicNumber" },
				codes: { type: 'List', itemType: 'NestedModel', model: openhmis.ItemCode },
				prices: { type: 'List', itemType: 'NestedModel', model: openhmis.ItemPrice, subResource: true},
				defaultPrice: { type: 'ItemPriceSelect', options: [] }
			},

			initialize: function(attributes, options) {
				openhmis.CustomizableBase.prototype.initialize.call(this, attributes, options);

				this.on("change:defaultPrice", function(model, defaultPrice, options) {
					this._getDefaultPriceFromPricesIfAvailable(defaultPrice.id || defaultPrice);
				});
				this.setPriceOptions();
			},

			_getDefaultPriceFromPricesIfAvailable: function(id) {
				var prices = this.get("prices");
				for (var price in prices) {
					if (prices[price].id !== undefined) {
						if (prices[price].id === id) {
							this.attributes["defaultPrice"] = new openhmis.ItemPrice(prices[price]);
							break;
						}
					}
					else if (prices[price].price && prices[price].price.toString() === id) {
						this.attributes["defaultPrice"] = new openhmis.ItemPrice(prices[price]);
						break;
					}
				}
			},

			fetch: function(options) {
				options = options || {};
				var success = options.success;
				options.success = function(model, resp) {
					// Load price options
					model.setPriceOptions();
					if (success) {
						success(model, resp);
					}
				}
				return openhmis.GenericModel.prototype.fetch.call(this, options);
			},

			getCodesList: function(list) {
				var codes, schema;
				if (list !== undefined) {
					codes = list;
					schema = { model: openhmis.ItemCode }
				} else {
					codes = this.get("codes");
					schema = this.schema.codes
				}
				return openhmis.GenericCollection.prototype.toString.call(codes, schema);
			},

			setPriceOptions: function(prices) {
				prices = prices ? prices : this.get('prices');
				if (prices instanceof openhmis.GenericCollection) {
					prices = prices.models;
				}
				this.schema.defaultPrice.options = _.map(prices, function(price) {
					if (!(price instanceof openhmis.ItemPrice)) {
						price = new openhmis.ItemPrice(price);
					}
					return {
						val: price.id || price.price || price.get("price"),
						label: price.toString()
					}
				});
			},

			validate: function(attrs, options) {
				if (!attrs.name) {
					return { name: __(openhmis.getMessage('openhmis.inventory.nameRequiredError')) }
				}
				if (!attrs.department) {
					return { department: __(openhmis.getMessage('openhmis.inventory.item.required.departmentError')) }
				}
				if (!attrs.prices || attrs.prices.length < 1) {
					return { prices: __(openhmis.getMessage('openhmis.inventory.item.required.priceError')) }
				}
				if (!attrs.defaultPrice) {
					return { defaultPrice: openhmis.getMessage('openhmis.inventory.item.required.default.PriceError')}
				}
				if (attrs.defaultExpirationPeriod && attrs.defaultExpirationPeriod <= 0) {
					return { defaultExpirationPeriod: openhmis.getMessage('openhmis.inventory.item.required.default.ExpirationPeriodError')}
				}
				if (attrs.minimumQuantity && attrs.minimumQuantity !== parseInt(attrs.minimumQuantity)) {
					return { minimumQuantity: openhmis.getMessage('openhmis.inventory.item.required.minimumQuantity')}
				}

				return null;
			},

			parse: function(resp) {
                openhmis.CustomizableBase.prototype.parse.call(this, resp);

                if (resp) {
					if (resp.department && _.isObject(resp.department)) {
						resp.department = new openhmis.Department(resp.department);
					}
					if(resp.codes){
						resp.codes = new openhmis.GenericCollection(resp.codes, { model: openhmis.ItemCode}).models;
					}
					if (resp.prices) {
						resp.prices = new openhmis.GenericCollection(resp.prices, { model: openhmis.ItemPrice }).models;
					}
					if (resp.defaultPrice) {
						resp.defaultPrice = new openhmis.ItemPrice(resp.defaultPrice);
					}
					if (resp.concept && _.isObject(resp.concept)) {
						resp.concept = new openhmis.Concept(resp.concept);
					}
				}
				return resp;
			},

			toJSON: function(options) {
				if (this.attributes.codes !== undefined) {
					// Can't set these, so need to remove them from JSON
					for (var code in this.attributes.codes) {
						delete this.attributes.codes[code].resourceVersion;
					}
					for (var price in this.attributes.prices) {
						delete this.attributes.prices[price].resourceVersion;
					}
				}

				return openhmis.GenericModel.prototype.toJSON.call(this, options);
			},

			codeUndefined: function(code) {
				return code == null || code == undefined;
			},

			toString: function() {
				if (this.get("codes") && this.get("codes").length > 0) {
					var code = this.get("codes")[0].code;
					if (this.codeUndefined(code)) {
						code = this.get("codes")[0].get("code");
					}
					if (this.codeUndefined(code)) {
						return this.get("name")
					}
					return this.get("name") + ' - ' + code;
				}
				if (this.get("name")) {
					return this.get("name");
				}
				return openhmis.GenericModel.prototype.toString.call(this);
			}
		});

		return openhmis;
	}
)
