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
