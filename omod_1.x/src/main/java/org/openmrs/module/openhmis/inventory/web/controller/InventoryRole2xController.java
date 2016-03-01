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
 *
 */

package org.openmrs.module.openhmis.inventory.web.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.module.openhmis.commons.model.RoleCreationViewModel;
import org.openmrs.module.openhmis.commons.web.controller.HeaderController;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.openmrs.util.RoleConstants;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dubdabasoduba on 21/01/2016.
 */
@Controller
@RequestMapping(ModuleWebConstants.ROLE_CREATION_2X_ROOT)
public class InventoryRole2xController {
	private static final Log LOG = LogFactory.getLog(InventoryRole2xController.class);

	private UserService userService;

	@Autowired
	public InventoryRole2xController(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public void render(ModelMap model, HttpServletRequest request) throws IOException {
		List<Role> roles = userService.getAllRoles();
		model.addAttribute("roles", roles);
		HeaderController.render(model, request);
	}

	@RequestMapping(method = RequestMethod.POST)
	public void submit(HttpServletRequest request, RoleCreationViewModel viewModel, Errors errors, ModelMap model)
	        throws IOException {
		HttpSession session = request.getSession();
		String action = request.getParameter("action");

		if (action.equals("add")) {
			addPrivileges(viewModel.getAddToRole());

			session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "openhmis.inventory.roleCreation.page.feedback.add");
		} else if (action.equals("remove")) {
			removePrivileges(viewModel.getRemoveFromRole());

			session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "openhmis.inventory.roleCreation.page.feedback.remove");
		} else if (action.equals("new") && validateNewRole(viewModel, errors)) {
			createRole(viewModel, session);
		}

		render(model, request);
	}

	private void addPrivileges(String roleUuid) {
		Role role = userService.getRoleByUuid(roleUuid);
		if (role == null) {
			throw new APIException("The role '" + roleUuid + "' could not be found.");
		}

		for (Privilege priv : PrivilegeConstants.getDefaultPrivileges()) {
			if (!role.hasPrivilege(priv.getName())) {
				role.addPrivilege(priv);
			}
		}

		userService.saveRole(role);
	}

	private void removePrivileges(String roleUuid) {
		Role role = userService.getRoleByUuid(roleUuid);

		if (role == null) {
			throw new APIException("The role '" + roleUuid + "' could not be found.");
		}

		for (Privilege priv : PrivilegeConstants.getDefaultPrivileges()) {
			if (role.hasPrivilege(priv.getName())) {
				role.removePrivilege(priv);
			}
		}

		userService.saveRole(role);
	}

	private void createRole(RoleCreationViewModel viewModel, HttpSession session) {
		Role newRole = new Role();
		newRole.setRole(viewModel.getNewRoleName());
		newRole.setDescription("Users who create and manage inventory data.");
		newRole.setPrivileges(PrivilegeConstants.getDefaultPrivileges());

		// Get the provider role and add it to a set that will be passed as the inherited roles
		Role providerRole = userService.getRole(RoleConstants.PROVIDER);
		Set<Role> inheritedRoles = new HashSet<Role>();
		inheritedRoles.add(providerRole);
		newRole.setInheritedRoles(inheritedRoles);

		userService.saveRole(newRole);

		session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "openhmis.inventory.roleCreation.page.feedback.new");
	}

	private boolean validateNewRole(RoleCreationViewModel viewModel, Errors errors) {
		if (StringUtils.isEmpty(viewModel.getNewRoleName())) {
			errors.rejectValue("role", "openhmis.inventory.roleCreation.page.feedback.error.blankRole");
		} else if (checkForDuplicateRole(viewModel.getNewRoleName())) {
			errors.rejectValue("role", "openhmis.inventory.roleCreation.page.feedback.error.existingRole");
		}

		return !errors.hasErrors();
	}

	private Boolean checkForDuplicateRole(String role) {
		for (Role name : userService.getAllRoles()) {
			if (name.getRole().equals(role)) {
				return true;
			}
		}
		return false;
	}
}
