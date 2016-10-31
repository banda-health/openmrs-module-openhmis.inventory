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
 * Copyright (C) OpenHMIS.  All Rights Reserved.'
 */
package org.openmrs.module.openhmis.inventory.extension.html;

import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.openmrs.module.openhmis.inventory.web.PrivilegeWebConstants;
import org.openmrs.module.web.extension.LinkExt;

/**
 * Link extension class to add an inventory link to OpenMRS.
 */
public class InventoryLinkExt extends LinkExt {

	@Override
	public String getLabel() {
		return "openhmis.inventory.page";
	}

	@Override
	public String getUrl() {
		return ModuleWebConstants.INVENTORY_PAGE;
	}

	@Override
	public String getRequiredPrivilege() {
		return PrivilegeWebConstants.INVENTORY_PAGE_PRIVILEGES;
	}

	public String getPortletUrl() {
		return null;
	}

}
