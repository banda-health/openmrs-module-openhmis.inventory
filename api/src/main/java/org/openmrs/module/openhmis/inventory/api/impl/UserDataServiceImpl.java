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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.IUserDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.security.BasicObjectAuthorizationPrivileges;
import org.openmrs.util.LocationUtility;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.user.UserProperties;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by ICCHANGE on 9/Nov/2016.
 */
public class UserDataServiceImpl extends BaseMetadataDataServiceImpl<User>
        implements IUserDataService {

	@Override
	public List<User> getUsersByLocation(Location location) {
		if (location == null) {
			throw new NullPointerException("The location must be defined");
		}

		String queryString = "select u from User u where u.userProperties['defaultLocation'] = :value";
		Query criteria = getRepository().createQuery(queryString);
		criteria.setParameter("value", location.getId() + "");
		List<User> result = criteria.list();

		return result;
	}

	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return null;
	}

	@Override
	protected void validate(User user) {

	}
}
