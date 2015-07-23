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
		openhmis.url.backboneBase + 'js/lib/backbone-forms',
		openhmis.url.backboneBase + 'js/model/location',
		openhmis.url.inventoryBase + 'js/model/stockroom',
		openhmis.url.inventoryBase + 'js/model/operation',
        openhmis.url.inventoryBase + 'js/view/stockroom',
		openhmis.url.backboneBase + 'js/view/generic',

		openhmis.url.backboneBase + 'js/view/list',
		openhmis.url.backboneBase + 'js/view/editors',
		openhmis.url.inventoryBase + 'js/view/editors',
		openhmis.url.backboneBase + 'js/view/search',
		openhmis.url.inventoryBase + 'js/view/search'
	],
	function($, openhmis, Backbone) {
		$(function() {

			// Display current stockrooms into list
            openhmis.startAddEditScreen(openhmis.Stockroom, {
                listView: openhmis.GenericSearchableListView,
                searchView: openhmis.LocationAndNameSearchView,
                addEditViewType: openhmis.StockroomDetailView,
                listFields: ['name', 'location']
			});
		});
	}
);
