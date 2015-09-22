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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.IDepartmentDataService;
import org.openmrs.module.openhmis.inventory.api.model.Department;
import org.openmrs.module.openhmis.inventory.api.security.BasicMetadataAuthorizationPrivileges;
import org.springframework.transaction.annotation.Transactional;

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
	public List<Department> getAll(boolean includeVoided) {
		return super.getAll(includeVoided);
	}

	/**
	 * Gets a list of all departments sorted by name
	 * @return sorted departments list
	 */
	@Override
	public List<Department> getAllDepartmentsSorted(boolean includeVoided) {
		List<Department> departments = getAll(includeVoided);
		Collections.sort(departments, new Comparator<Department>() {

			public int compare(Department d1, Department d2) {
				return d1.getName().toLowerCase()
				        .compareTo(d2.getName().toLowerCase());
			}
		});

		return departments;
	}
}
