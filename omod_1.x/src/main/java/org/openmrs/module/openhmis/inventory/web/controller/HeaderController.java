package org.openmrs.module.openhmis.inventory.web.controller;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.context.Context;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Retrieves locations for the logged in user and sets the list in session.
 */
public class HeaderController {

	public static void render(ModelMap model, HttpServletRequest request) throws IOException {

		HttpSession session = request.getSession();
		Integer locationId = (Integer)session.getAttribute("emrContext.sessionLocationId");
		model.addAttribute("sessionLocationId", locationId);
		model.addAttribute("sessionLocationName", Context.getLocationService().getLocation(locationId).getName());

		LocationTag locationTag = Context.getLocationService().getLocationTagByName("Login Location");

		List<Location> loginLocations = Context.getLocationService().getLocationsByTag(locationTag);

		model.addAttribute("loginLocations", loginLocations);
		model.addAttribute("multipleLoginLocations", loginLocations.size() > 1);

	}
}
