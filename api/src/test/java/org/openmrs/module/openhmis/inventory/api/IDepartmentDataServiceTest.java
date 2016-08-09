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

package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.model.Department;

public class IDepartmentDataServiceTest extends IMetadataDataServiceTest<IDepartmentDataService, Department> {
	public static final String DEPARTMENT_DATASET = TestConstants.BASE_DATASET_DIR + "DepartmentTest.xml";

	@Override
	public void before() throws Exception {
		super.before();

		executeDataSet(DEPARTMENT_DATASET);
	}

	@Override
	protected int getTestEntityCount() {
		return 4;
	}

	@Override
	public Department createEntity(boolean valid) {
		Department department = new Department();

		if (valid) {
			department.setName("new department");
		}

		department.setDescription("new department description");

		return department;
	}

	@Override
	protected void updateEntityFields(Department department) {
		department.setName(department.getName() + " updated");
		department.setDescription(department.getDescription() + " updated");
	}

	@Override
	protected void assertEntity(Department expected, Department actual) {
		super.assertEntity(expected, actual);
	}
}
