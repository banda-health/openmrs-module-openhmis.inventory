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
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
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
	public String getLocations(ModelMap model) throws IOException {
		List<Location> locationlist = new ArrayList<Location>();

		String loc = Context.getAuthenticatedUser().getUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION);
		Location ltemp = Context.getLocationService().getLocation(Integer.parseInt(loc));

		locationlist.add(ltemp);

		//jackson doesn't work for location class so we convert it manually
		String output = "{\"results\": [";
		for (int i = 0; i < locationlist.size(); i++) {
			if (i != 0) {
				output += ",";
			}
			output += "{\"display\":\"" + locationlist.get(i).getName()
			        + "\",\"uuid\":\"" + locationlist.get(i).getUuid() + "\"";
			output +=
			        ",\"links\":["
			                + "{\"rel\":\"self\"," + "\"uri\":\"/openmrs/ws/rest/v2/inventory/location/" + ltemp.getUuid()
			                + "\"" + "}"
			                + ",{\"rel\":\"full\"," + "\"uri\":\"/openmrs/ws/rest/v2/inventory/location/" + ltemp.getUuid()
			                + "?v=full\"" + "}"
			                + "]";
			output += "}";
		}
		output += "]}";

		return output;
	}

	//icchange kmri location restriction
	@RequestMapping(value = "location/{locationuuid}", method = RequestMethod.GET)
	@ResponseBody
	public String getLocationsByUudi(@PathVariable String locationuuid, ModelMap model) throws IOException {
		Location ltemp = Context.getLocationService().getLocationByUuid(locationuuid);

		//jackson doesn't work for location class so we convert it manually
		String output = "{";
		output += "\"display\":\"" + ltemp.getName() + "\""
		        + ",\"name\":\"" + ltemp.getName() + "\""
		        + ",\"description\":\"" + ltemp.getDescription() + "\""
		        + ",\"uuid\":\"" + ltemp.getUuid() + "\"";
		output +=
		        ",\"links\":["
		                + "{\"rel\":\"self\"," + "\"uri\":\"/openmrs/ws/rest/v2/inventory/location/" + ltemp.getUuid()
		                + "\"" + "}"
		                + ",{\"rel\":\"full\"," + "\"uri\":\"/openmrs/ws/rest/v2/inventory/location/" + ltemp.getUuid()
		                + "?v=full\"" + "}"
		                + "]";
		output += "}";

		return output;
	}
}
