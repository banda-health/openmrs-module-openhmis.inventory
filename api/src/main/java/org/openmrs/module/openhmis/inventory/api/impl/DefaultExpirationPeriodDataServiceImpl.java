/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
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
import org.openmrs.module.openhmis.inventory.api.IDefaultExpirationPeriodDataService;
import org.openmrs.module.openhmis.inventory.api.model.DefaultExpirationPeriod;
import org.openmrs.module.openhmis.inventory.api.security.BasicMetadataAuthorizationPrivileges;

public class DefaultExpirationPeriodDataServiceImpl
             extends BaseMetadataDataServiceImpl<DefaultExpirationPeriod>
             implements IDefaultExpirationPeriodDataService {

    @Override
    protected IMetadataAuthorizationPrivileges getPrivileges() {
        return this;
    }

    @Override
    protected void validate(DefaultExpirationPeriod object) throws APIException {

    }

    @Override
    public DefaultExpirationPeriod save(DefaultExpirationPeriod defaultExpirationPeriod) {
        if (defaultExpirationPeriod.getTimePeriod() == null && defaultExpirationPeriod.getTimeValue() == null) {
            purge(defaultExpirationPeriod);
            return null;
        }
        return super.save(defaultExpirationPeriod);
    }

}
