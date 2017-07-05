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
package org.openmrs.module.openhmis.inventory.api.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.api.WellKnownOperationTypes;

public class IStockOperationTypeTest extends BaseOperationTypeTest {
	protected IStockOperationType testType;
	protected User testUser;

	@Before
	public void before() throws Exception {
		super.before();

		testType = WellKnownOperationTypes.getAdjustment();
		testUser = Context.getUserService().getUser(5506);
	}

	/**
	 * @verifies return true when type has no role or user defined
	 * @see IStockOperationType#userCanProcess(org.openmrs.User)
	 */
	@Test
	public void userCanProcess_shouldReturnTrueWhenTypeHasNoRoleOrUserDefined() throws Exception {
		testType.setRole(null);
		testType.setUser(null);

		boolean result = testType.userCanProcess(testUser);
		Assert.assertTrue(result);
	}

	/**
	 * @verifies return false when type has different role than user
	 * @see IStockOperationType#userCanProcess(org.openmrs.User)
	 */
	@Test
	public void userCanProcess_shouldReturnFalseWhenTypeHasDifferentRoleThanUser() throws Exception {
		Role role = Context.getUserService().getRole("Other");
		Assert.assertFalse(testUser.hasRole("Other"));

		testType.setRole(role);
		testType.setUser(null);

		boolean result = testType.userCanProcess(testUser);
		Assert.assertFalse(result);
	}

	/**
	 * @verifies return false when type has different user than user
	 * @see IStockOperationType#userCanProcess(org.openmrs.User)
	 */
	@Test
	public void userCanProcess_shouldReturnFalseWhenTypeHasDifferentUserThanUser() throws Exception {
		User user = Context.getUserService().getUser(1);
		Assert.assertFalse(user.equals(testUser));

		testType.setRole(null);
		testType.setUser(user);

		boolean result = testType.userCanProcess(testUser);
		Assert.assertFalse(result);
	}

	/**
	 * @verifies return true when type has user role and different user than user
	 * @see IStockOperationType#userCanProcess(org.openmrs.User)
	 */
	@Test
	public void userCanProcess_shouldReturnTrueWhenTypeHasUserRoleAndDifferentUserThanUser() throws Exception {
		Role role = Context.getUserService().getRole("Parent");
		Assert.assertTrue(testUser.hasRole("Parent"));

		User user = Context.getUserService().getUser(1);
		Assert.assertFalse(user.equals(testUser));

		testType.setRole(role);
		testType.setUser(user);

		boolean result = testType.userCanProcess(testUser);
		Assert.assertTrue(result);
	}

	/**
	 * @verifies return true when type has different role and same user as user
	 * @see IStockOperationType#userCanProcess(org.openmrs.User)
	 */
	@Test
	public void userCanProcess_shouldReturnTrueWhenTypeHasDifferentRoleAndSameUserAsUser() throws Exception {
		Role role = Context.getUserService().getRole("Other");
		Assert.assertFalse(testUser.hasRole("Other"));

		testType.setRole(role);
		testType.setUser(testUser);

		boolean result = testType.userCanProcess(testUser);
		Assert.assertTrue(result);
	}

	/**
	 * @verifies throw IllegalArgumentException if user is null
	 * @see IStockOperationType#userCanProcess(org.openmrs.User)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void userCanProcess_shouldThrowIllegalArgumentExceptionIfUserIsNull() throws Exception {
		testType.userCanProcess(null);
	}

	/**
	 * @verifies return true when type has same role or parent role as user
	 * @see IStockOperationType#userCanProcess(org.openmrs.User)
	 */
	@Test
	public void userCanProcess_shouldReturnTrueWhenTypeHasSameRoleOrParentRoleAsUser() throws Exception {
		Role role = Context.getUserService().getRole("Parent");
		Assert.assertTrue(testUser.hasRole("Parent"));

		testType.setRole(role);
		testType.setUser(null);

		boolean result = testType.userCanProcess(testUser);
		Assert.assertTrue(result);
	}

	/**
	 * @verifies return true when type has same user as user
	 * @see IStockOperationType#userCanProcess(org.openmrs.User)
	 */
	@Test
	public void userCanProcess_shouldReturnTrueWhenTypeHasSameUserAsUser() throws Exception {
		testType.setRole(null);
		testType.setUser(testUser);

		boolean result = testType.userCanProcess(testUser);
		Assert.assertTrue(result);
	}

	/**
	 * @verifies return true when type has different user and user is sys dev
	 * @see IStockOperationType#userCanProcess(org.openmrs.User)
	 */
	@Test
	public void userCanProcess_shouldReturnTrueWhenTypeHasDifferentUserAndUserIsSysDev() throws Exception {
		testType.setRole(null);
		testType.setUser(testUser);

		// User 1 is a sys dev user
		User adminUser = Context.getUserService().getUser(1);

		boolean result = testType.userCanProcess(adminUser);
		Assert.assertTrue(result);
	}

	/**
	 * @verifies return true when type has different role and user is sys dev
	 * @see IStockOperationType#userCanProcess(org.openmrs.User)
	 */
	@Test
	public void userCanProcess_shouldReturnTrueWhenTypeHasDifferentRoleAndUserIsSysDev() throws Exception {
		Role role = Context.getUserService().getRole("Other");

		testType.setRole(role);
		testType.setUser(null);

		// User 1 is a sys dev user
		User adminUser = Context.getUserService().getUser(1);

		boolean result = testType.userCanProcess(adminUser);
		Assert.assertTrue(result);
	}

	/**
	 * @verifies return true when type has different role and user and user is sys dev
	 * @see IStockOperationType#userCanProcess(org.openmrs.User)
	 */
	@Test
	public void userCanProcess_shouldReturnTrueWhenTypeHasDifferentRoleAndUserAndUserIsSysDev() throws Exception {
		Role role = Context.getUserService().getRole("Other");

		testType.setRole(role);
		testType.setUser(testUser);

		// User 1 is a sys dev user
		User adminUser = Context.getUserService().getUser(1);

		boolean result = testType.userCanProcess(adminUser);
		Assert.assertTrue(result);
	}
}
