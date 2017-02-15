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

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.model.Department;

import java.util.List;

public class IDepartmentDataServiceTest extends IMetadataDataServiceTest<IDepartmentDataService, Department> {
	public static final String DEPARTMENT_DATASET = TestConstants.BASE_DATASET_DIR + "DepartmentTest.xml";

	public LocationService locationService;

	@Override
	public void before() throws Exception {
		super.before();

		locationService = Context.getLocationService();

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

	@Test
	public void getDepartmentsByLocation_TestGettingDepartmentsViaLocations() {
		Location location1 = new Location();
		location1.setName("locationTest1");
		locationService.saveLocation(location1);

		Location location2 = new Location();
		location2.setName("locationTest2");
		locationService.saveLocation(location2);

		Location location3 = new Location();
		location3.setName("locationTest3");
		locationService.saveLocation(location3);

		Department department1 = new Department();
		department1.setName("depatmenttest1");
		department1.setLocation(location1);
		service.save(department1);

		Department department2 = new Department();
		department2.setName("depatmenttest2");
		department2.setLocation(location1);
		service.save(department2);

		Department department3 = new Department();
		department3.setName("depatmenttest3");
		department3.setLocation(location2);
		service.save(department3);

		Context.flushSession();

		List<Department> departmentList = service.getDepartmentsByLocation(location1, false);
		Assert.assertEquals(2, departmentList.size());
		Assert.assertTrue(departmentList.get(0).getName().equals("depatmenttest1"));
		Assert.assertTrue(departmentList.get(1).getName().equals("depatmenttest2"));

		departmentList = service.getDepartmentsByLocation(location2, false);
		Assert.assertEquals(1, departmentList.size());
		Assert.assertTrue(departmentList.get(0).getName().equals("depatmenttest3"));

		departmentList = service.getDepartmentsByLocation(location3, false);
		Assert.assertEquals(0, departmentList.size());

	}

}
