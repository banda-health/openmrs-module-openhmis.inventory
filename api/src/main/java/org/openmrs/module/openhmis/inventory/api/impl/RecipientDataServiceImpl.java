package org.openmrs.module.openhmis.inventory.api.impl;

import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.IRecipientDataService;
import org.openmrs.module.openhmis.inventory.api.model.Recipient;
import org.openmrs.module.openhmis.inventory.api.security.BasicMetadataAuthorizationPrivileges;

public class RecipientDataServiceImpl
	extends BaseMetadataDataServiceImpl<Recipient>
	implements IRecipientDataService {

	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return new BasicMetadataAuthorizationPrivileges();
	}

	@Override
	protected void validate(Recipient object) throws APIException {

	}

}
