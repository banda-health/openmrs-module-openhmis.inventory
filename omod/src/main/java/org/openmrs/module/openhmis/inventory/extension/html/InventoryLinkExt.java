package org.openmrs.module.openhmis.inventory.extension.html;

import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.openmrs.module.web.extension.LinkExt;

public class InventoryLinkExt extends LinkExt{

    @Override
    public String getLabel() {
        if (true) {
            return "openhmis.inventory.page";
        }
        return null;
    }

    @Override
    public String getUrl() {
        return ModuleWebConstants.INVENTORY_START_PAGE;
    }

    @Override
    public String getRequiredPrivilege() {
        return PrivilegeConstants.MANAGE_ITEMS;
    }

    public String getPortletUrl() {
        return null;
    }

}
