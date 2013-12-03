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
		openhmis.url.inventoryBase + 'js/model/department',
        openhmis.url.inventoryBase + 'js/model/category'
	],
	function(_, openhmis, __) {
		openhmis.ItemCode = openhmis.GenericModel.extend({
			meta: {
				name: "Item Code",
				namePlural: "Item Codes",
				openmrsType: 'metadata',
				restUrl: openhmis.url.inventoryModelBase + 'itemCode'
			},
			schema: {
				code: { type: 'Text' }
			},
			toString: function() { return this.get('code'); }
		});
		
		openhmis.ItemPrice = openhmis.GenericModel.extend({
			meta: {
				name: "Item Price",
				namePlural: "Item Prices",
				openmrsType: 'metadata',
				restUrl: openhmis.url.inventoryModelBase + 'itemPrice'
			},
			schema: {
				name: { type: "Text" },
				price: { type: 'BasicNumber' }
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
				if (price === undefined)
					return 0;
				if (price instanceof openhmis.ItemPrice)
					return price.toString();
				return price.toFixed(2);
			},
			
			toString: function() {
				var name = this.get("name") ? " (" + this.get("name") + ")" : "";
				return this.format(this.get('price')) + name;
			}
		});
		
		openhmis.Item = openhmis.GenericModel.extend({
			meta: {
				name: "Item",
				namePlural: "Items",
				openmrsType: 'metadata',
				restUrl: openhmis.url.inventoryModelBase + 'item'
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
                category: {
                    type: 'CategorySelect',
                    options: new openhmis.GenericCollection(null, {
                        model: openhmis.Category,
                        url: openhmis.url.inventoryModelBase + 'category'
                    }),
                    objRef: true
                },
				codes: { type: 'List', itemType: 'NestedModel', model: openhmis.ItemCode },
				prices: { type: 'List', itemType: 'NestedModel', model: openhmis.ItemPrice },
				defaultPrice: { type: 'ItemPriceSelect', options: [] }
			},
			
			initialize: function(attributes, options) {
				openhmis.GenericModel.prototype.initialize.call(this, attributes, options);
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
					if (success) success(model, resp);
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
				if (prices instanceof openhmis.GenericCollection) prices = prices.models;
				this.schema.defaultPrice.options = _.map(prices, function(price) {
					if (!(price instanceof openhmis.ItemPrice)) price = new openhmis.ItemPrice(price);
					return {
						val: price.id || price.price || price.get("price"),
						label: price.toString()
					}
				});
			},
			
			validate: function(attrs, options) {
				if (!attrs.name) return { name: __("A name is required") }
				if (!attrs.department) return { department: __("An item needs to be associated with a department") }
				if (!attrs.prices || attrs.prices.length < 1) return { prices: __("An item should have at least one price.") }
				if (!attrs.defaultPrice) return { defaultPrice: "Please specify a default price."}
				return null;
			},
			
			parse: function(resp) {
				if (resp) {
					if (resp.department && _.isObject(resp.department))
						resp.department = new openhmis.Department(resp.department);
					if (resp.prices) resp.prices = new openhmis.GenericCollection(resp.prices, { model: openhmis.ItemPrice }).models;
					if (resp.defaultPrice) resp.defaultPrice = new openhmis.ItemPrice(resp.defaultPrice);
				}
				return resp;
			},
			
			toJSON: function() {
				if (this.attributes.codes !== undefined) {
					// Can't set these, so need to remove them from JSON
					for (var code in this.attributes.codes)
						delete this.attributes.codes[code].resourceVersion;
					for (var price in this.attributes.prices)
						delete this.attributes.prices[price].resourceVersion;
				}
				var json = openhmis.GenericModel.prototype.toJSON.call(this);
				if (json.defaultPrice instanceof openhmis.ItemPrice)
					json.defaultPrice = json.defaultPrice.get("price").toString();
				return json;
			},
			
			toString: function() {
				if (this.get("codes").length > 0)
					return this.get("codes")[0].code + ' - ' + this.get("name");
				return this.get("name");
			}
		});
		return openhmis;
	}
)