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
curl(
	{ baseUrl: openhmis.url.resources },
	[
		openhmis.url.backboneBase + 'js/lib/jquery',
		openhmis.url.backboneBase + 'js/openhmis',
		openhmis.url.backboneBase + 'js/lib/backbone',
		openhmis.url.backboneBase + 'js/lib/backbone-forms',
		openhmis.url.inventoryBase + 'js/model/stockRoom',
        openhmis.url.inventoryBase + 'js/view/stockRoom',
		openhmis.url.backboneBase + 'js/view/generic'
	],
	function($, openhmis, Backbone) {
		/* FOR REFERENCE UNTIL NOT NEEDED
		var StockroomView = Backbone.View.extend({
			tmplFile: openhmis.url.inventoryBase + 'template/stockRoom.html',
			tmplSelector: '#stockroom-select',

			events: {
				'change #srSelect': 'selected'
			},

			initialize: function(options) {
				this.template = this.getTemplate(this.tmplFile, this.tmplSelector);
				this.collection = options.collection;
			},

			render: function() {
				var that = this;
				this.collection.fetch({
					success: function() {
						that.$el.html(that.template({
							model: that.collection
						}));
					}
				});
			},

			selected: function() {
				// render the selected stockroom
				console.log('SELECTED!')
			}
		});
		*/
		$(function() {
			/*var stockrooms = new openhmis.GenericCollection([], {
				model: openhmis.StockRoom
			});
			var stockroomView = new StockroomView({
				el: $('#srSelect'),
				collection: stockrooms
			});
			stockroomView.render();
			*/

			var stockRoomList = $("#stockRoomList");
            var stockRoomInfo = $("#stockRoomInfo");
            var stockRoomEdit = $("#stockRoomEdit");

			// Display current stock rooms into list
            openhmis.startAddEditScreen(openhmis.StockRoom, {
				listFields: ['name', 'description'],
                listElement: stockRoomList,
                addEditViewType: openhmis.StockRoomDetailView,
                addEditElement: stockRoomInfo
			});
		});
	}
);