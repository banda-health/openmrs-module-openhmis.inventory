/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.openmrs.util.LocationUtility;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for inventory resources.
 */
@Controller
@RequestMapping("/rest/" + ModuleRestConstants.MODULE_REST_ROOT)
public class InventoryResourceController extends MainResourceController {
	@Override
	public String getNamespace() {
		return ModuleRestConstants.MODULE_REST_ROOT;
	}

	//icchange kmri location restriction
	@RequestMapping(value = "location", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getLocations(ModelMap model) throws IOException {
		List<Location> locationlist = new ArrayList<Location>();

		Location locationTemp = LocationUtility.getUserDefaultLocation();
		locationlist.add(locationTemp);

		SimpleObject results = new SimpleObject();
		List<SimpleObject> locationObjectList = new ArrayList<SimpleObject>();

		//jackson doesn't work for location class so we convert it manually
		for (int i = 0; i < locationlist.size(); i++) {
			SimpleObject locationObject = new SimpleObject();
			locationObject.add("display", locationlist.get(i).getName());
			locationObject.add("uuid", locationlist.get(i).getUuid());
			SimpleObject linkObjectSelf = new SimpleObject();
			linkObjectSelf.add("rel", "self");
			linkObjectSelf.add("uri", "/openmrs/ws/rest/v2/inventory/location/" + locationTemp.getUuid());
			SimpleObject linkObjectFull = new SimpleObject();
			linkObjectFull.add("rel", "full");
			linkObjectFull.add("uri", "/openmrs/ws/rest/v2/inventory/location/" + locationTemp.getUuid() + "?v=full");
			List<SimpleObject> linklist = new ArrayList<SimpleObject>();
			linklist.add(linkObjectSelf);
			linklist.add(linkObjectFull);
			locationObject.put("links", linklist);
			locationObjectList.add(locationObject);
		}
		results.add("results", locationObjectList);
		return results;
	}

	//icchange kmri location restriction
	@RequestMapping(value = "location/{locationUuid}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getLocationsByUudi(@PathVariable String locationUuid, ModelMap model) throws IOException {
		Location locationTemp = Context.getLocationService().getLocationByUuid(locationUuid);

		//jackson doesn't work for location class so we convert it manually
		SimpleObject locationObject = new SimpleObject();
		locationObject.add("display", locationTemp.getName());
		locationObject.add("name", locationTemp.getName());
		locationObject.add("description", locationTemp.getDescription());
		locationObject.add("uuid", locationTemp.getUuid());
		SimpleObject linkObjectSelf = new SimpleObject();
		linkObjectSelf.add("rel", "self");
		linkObjectSelf.add("uri", "/openmrs/ws/rest/v2/inventory/location/" + locationTemp.getUuid());
		SimpleObject linkObjectFull = new SimpleObject();
		linkObjectFull.add("rel", "full");
		linkObjectFull.add("uri", "/openmrs/ws/rest/v2/inventory/location/" + locationTemp.getUuid() + "?v=full");
		List<SimpleObject> linklist = new ArrayList<SimpleObject>();
		linklist.add(linkObjectSelf);
		linklist.add(linkObjectFull);
		locationObject.put("links", linklist);
		return locationObject;
	}
}
