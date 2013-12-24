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
describe('Item', function() {
	var testItem = jQuery.parseJSON(openhmis.testData.JSON.item);
	var refItem = jQuery.parseJSON(openhmis.testData.JSON.item);
	
	it("should should provide a correct serializable object", function() {
		var item = new openhmis.Item(_.clone(testItem), { parse: true });
		var attrs = item.toJSON();
		// defaultPrice is special case; because of the way REST handles item
		// prices, we have to send the default price as the price value, not a
		// UUID.
		expect(attrs.defaultPrice).toEqual(refItem.defaultPrice.price.toString());
		delete attrs.defaultPrice;
		
		openhmis.test.modelToJson(attrs, refItem, expect, item.schema);
	});

	it("should set a list of price options in schema.defaultPrice", function() {
		var price1 = testItem.prices[0];
		var price2 = testItem.prices[1];

		var item = new openhmis.Item({ prices: [ price1, price2 ] }, { parse: true});
		var option1 = item.schema.defaultPrice.options[0];
		var option2 = item.schema.defaultPrice.options[1];
		expect(option1.val).toEqual(price1.uuid);
		expect(option1.label).toEqual("1000.00 (AIDS)");
		expect(option2.val).toEqual(price2.uuid);
		expect(option2.label).toEqual("4.40 (Default)");
	});
	it("should get the full price from the prices attribute when setting defaultPrice", function() {
		var item = new openhmis.Item(_.clone(testItem), { parse: true });
		expect(item.get("defaultPrice").name).toBeUndefined();
		// Set using a uuid string
		item.set("defaultPrice", testItem.prices[1].uuid);
		expect(item.get("defaultPrice").get("name")).toEqual(testItem.prices[1].name);
		// Set using a model containing only uuid
		var barePrice = new openhmis.ItemPrice(
			{ uuid: testItem.prices[0].uuid }
		);
		item.set("defaultPrice", barePrice);
		expect(item.get("defaultPrice").get("name")).toEqual(testItem.prices[0].name);
	});
});

describe('ItemCode', function() {
	it('should provide a string representation for itself', function() {
		var code = 'TestCode';
		var itemCode = new openhmis.ItemCode({
			code: code
		});
		expect(code.toString()).toEqual(code);
	});
});