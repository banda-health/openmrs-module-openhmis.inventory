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
package org.openmrs.module.openhmis.inventory.web.controller;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.openmrs.module.openhmis.backboneforms.web.controller.BackboneMessageRenderController;

/**
 * Controller for the message properties fragment.
 */
@Controller
@RequestMapping(ModuleWebConstants.MESSAGE_PROPERTIES_JS_URI)
public class InventoryMessageRenderController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView render(HttpServletRequest request) {
		// object to store keys from inventory and backboneforms
		Vector<String> keys = new Vector<String>();

		Locale locale = RequestContextUtils.getLocale(request);
		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);

		// store inventory message keys in the vector object
		keys.addAll(resourceBundle.keySet());

		// retrieve backboneforms messages
		BackboneMessageRenderController backboneController = new BackboneMessageRenderController();
		ModelAndView modelAndView = backboneController.render(request);

		// store backboneforms message keys in the vector object
		for (Map.Entry<String, Object> messageKeys : modelAndView.getModel().entrySet()) {
			Enumeration<String> messageKey = (Enumeration<String>)messageKeys.getValue();
			while (messageKey.hasMoreElements()) {
				String key = messageKey.nextElement();
				if (!keys.contains(key))
					keys.add(key);
			}
		}

		return new ModelAndView(ModuleWebConstants.MESSAGE_PAGE, "keys", keys.elements());
	}
}
