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
beforeEach(function() {
	Backbone.sync = Backbone.__sync;
});

afterEach(function() {
	Backbone.sync = function() {};
});
describe("Item Editor", function() {
	it("requires Inventory editors", function() {
		Backbone.sync = function() {};
		require([openhmis.url.inventoryBase + 'js/view/editors']);
	});
	/**
	 * @requires server response
	 */
	it("should return the proper data after a search", function() {
		var response = jasmine.createSpy();

		runs(function() {
			var editor = new Backbone.Form.editors.Item();
	
			spyOn(jQuery, "ajax").andCallFake(function(options) {
				var resp = jQuery.parseJSON(openhmis.testData.JSON.itemCollection);
				options.success(resp);
			});
			
			var request = { term: "Test term" }
			editor.doStockSearch(request, response, openhmis.Item);
		});

		waitsFor(function() {
			return response.wasCalled;
		}, "search to complete", 1000);
		
		runs(function() {
			var data = response.mostRecentCall.args[0][0];
			expect(data.codes.length).toEqual(2);
			expect(data.department_uuid).toEqual("faf2f364-189c-4959-9428-4f917f52b8de");
			expect(data.label).toEqual("Ciprofloxacin");
			expect(data.val).toEqual("20aa2858-6642-4b7a-b456-b07621c26538");
		});
	});
});
