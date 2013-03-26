/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.api.impl;

import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.IStockRoomTransactionTypeDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransactionType;
import org.openmrs.module.openhmis.inventory.api.security.TransactionAuthorizationPrivileges;

public class StockRoomTransactionTypeDataServiceImpl
	extends BaseMetadataDataServiceImpl<StockRoomTransactionType>
	implements IStockRoomTransactionTypeDataService {
	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return new TransactionAuthorizationPrivileges();
	}

	@Override
	protected void validate(StockRoomTransactionType object) throws APIException {
	}
}
