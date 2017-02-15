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
 */
package org.openmrs.module.openhmis.inventory.api.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IDepartmentDataService;
import org.openmrs.module.openhmis.inventory.api.model.Department;
import org.openmrs.module.openhmis.inventory.api.security.BasicMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.util.HibernateCriteriaConstants;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Data service implementation class for {@link Department}s.
 */
@Transactional
public class DepartmentDataServiceImpl extends BaseMetadataDataServiceImpl<Department> implements IDepartmentDataService {
	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return new BasicMetadataAuthorizationPrivileges();
	}

	@Override
	protected void validate(Department entity) {
		return;
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	public List<Department> getDepartmentsByLocation(Location location, boolean includeRetired) {
		return getDepartmentsByLocation(location, includeRetired, null);
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	public List<Department> getDepartmentsByLocation(final Location location, final boolean includeRetired,
	        PagingInfo pagingInfo) {
		if (location == null) {
			throw new NullPointerException("The location must be defined");
		}

		return executeCriteria(Department.class, pagingInfo, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq(HibernateCriteriaConstants.LOCATION, location));
				if (!includeRetired) {
					criteria.add(Restrictions.eq(HibernateCriteriaConstants.RETIRED, false));
				}
			}
		}, getDefaultSort());
	}
}
