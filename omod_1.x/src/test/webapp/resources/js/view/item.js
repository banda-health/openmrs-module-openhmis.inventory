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
describe("ItemAddEditView", function() {
	var departmentsObj = $.parseJSON(openhmis.testData.JSON.departmentCollection);

	var listIsRendered = function(view) {
		var call = view.trigger.mostRecentCall;
		return call.args !== undefined && call.args[0] === "readyToAdd";
	}

	beforeEach(function() {
		//TODO: Maybe we can make a helper class to make this spying less ugly
		// Setup mocked AJAX responses
		spyOn(Backbone, "sync").andCallFake(function(method, model, options) {
			var modelUrl = typeof model.url === 'function' ? model.url() : model.url;
			if (modelUrl.indexOf(openhmis.Department.prototype.meta.restUrl) !== -1)
				options.success(departmentsObj);
			else
				options.success([]);
		});
	});

	it("requires ItemAddEditView and Inventory editors", function() {
		var required = {};
		required[openhmis.url.inventoryBase + "js/view/item"] = null;
		required[openhmis.url.inventoryBase + "js/view/editors"] = null;
		require(required);
	});
	
	it("should edit an item's department", function() {
		var secondDepartment = departmentsObj.results[1];		
		var view = new openhmis.ItemAddEditView({
			collection: new openhmis.GenericCollection([], { model: openhmis.Item }) });
		var item = new openhmis.Item($.parseJSON(openhmis.testData.JSON.item), { parse: true });
		// Need to wait for this so that the form can be submitted properly
		var baseEditor = Backbone.Form.editors.Base.prototype;
		spyOn(baseEditor, "trigger").andCallThrough();
		view.edit(item);
		waitsFor(function() { return listIsRendered(baseEditor); },
			"form lists to be rendered", 1000);
		runs(function() {
			// Change department on form
			view.$("#" + item.cid + "_department").val(secondDepartment.uuid);
			view.save();
			expect(item.get("department").id).toEqual(secondDepartment.uuid);
		});
	});
	
	it("should edit an item's default price", function() {
		var item = new openhmis.Item($.parseJSON(openhmis.testData.JSON.item), { parse: true });
		var secondPrice = item.get("prices")[1];
		var view = new openhmis.ItemAddEditView({
			collection: new openhmis.GenericCollection([], { model: openhmis.Item }) });
		// Need to wait for this so that the form can be submitted properly
		var baseEditor = Backbone.Form.editors.Base.prototype;
		spyOn(baseEditor, "trigger").andCallThrough();
		view.edit(item);
		waitsFor(function() { return listIsRendered(baseEditor); },
			"form lists to be rendered", 1000);
		runs(function() {
			// Change department on form
			view.$("#" + item.cid + "_defaultPrice").val(secondPrice.id);
			view.save();
			expect(item.get("defaultPrice").id).toEqual(secondPrice.id);
		});		
	});
});