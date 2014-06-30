package org.openmrs.module.openhmis.inventory.extension.html;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.openmrs.module.web.extension.LinkExt;

import java.util.Map;

public class InventoryLinkExt extends LinkExt {

    private boolean hasInventoryRights = false;

    @Override
    public void initialize(Map<String, String> parameterMap) {
        super.initialize(parameterMap);
        User currentUser = Context.getAuthenticatedUser();
        if (currentUser != null) {
            boolean hasViewItemsPrivilege = currentUser.hasPrivilege(PrivilegeConstants.VIEW_ITEMS);
            boolean hasManageItemsPrivilege = currentUser.hasPrivilege(PrivilegeConstants.MANAGE_ITEMS);
            boolean hasPurgeItemsPrivilege = currentUser.hasPrivilege(PrivilegeConstants.PURGE_ITEMS);
            if (hasViewItemsPrivilege || hasManageItemsPrivilege || hasPurgeItemsPrivilege) {
                hasInventoryRights = true;
            }
        }
    }

    @Override
    public String getLabel() {
        if (hasInventoryRights) {
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
