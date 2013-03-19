package org.openmrs.module.openhmis.inventory.api.impl;

import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseCustomizableMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransfer;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;

public class StockRoomTransferDataServiceImpl
		extends BaseCustomizableMetadataDataServiceImpl<StockRoomTransfer>
		implements IMetadataAuthorizationPrivileges {
	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return this;
	}

	@Override
	protected void validate(StockRoomTransfer object) throws APIException {
	}

	@Override
	public String getRetirePrivilege() {
		return PrivilegeConstants.MANAGE_TRANSFERS;
	}

	@Override
	public String getSavePrivilege() {
		return PrivilegeConstants.MANAGE_TRANSFERS;
	}

	@Override
	public String getPurgePrivilege() {
		return PrivilegeConstants.PURGE_TRANSFERS;
	}

	@Override
	public String getGetPrivilege() {
		return PrivilegeConstants.VIEW_TRANSFERS;
	}
}
