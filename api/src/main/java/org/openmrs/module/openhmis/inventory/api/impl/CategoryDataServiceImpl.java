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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.ICategoryDataService;
import org.openmrs.module.openhmis.inventory.api.model.Category;
import org.openmrs.module.openhmis.inventory.api.security.BasicMetadataAuthorizationPrivileges;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CategoryDataServiceImpl
		extends BaseMetadataDataServiceImpl<Category>
		implements ICategoryDataService {
	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return new BasicMetadataAuthorizationPrivileges();
	}

	@Override
	protected void validate(Category category) throws APIException {
		// Ensure that the category is not a child of itself
		if (hasCycle(category, category.getCategories())) {
			throw new APIException("Cycle detected.  A category cannot be a child of itself.");
		}
	}

	@Override
	protected Collection<? extends OpenmrsObject> getRelatedObjects(Category entity) {
		return new ArrayList<Category>(entity.getCategories());
	}

	/**
	 * Checks to see if the specified parent category is a descendant of itself
	 */
	private boolean hasCycle(Category parent, Set<Category> children) {
		// If there are no children then there are no cycles
		if (children == null || children.size() == 0) {
			return false;
		}

		// Check for each descendant
		for (Category child : children) {
			// Check direct children
			if (child.getId() != null && child.getId().equals(parent.getId())) {
				return true;
			}

			// Check children of direct children
			if (hasCycle(parent, child.getCategories())) {
				return true;
			}
		}

		// Because a category can only be the child of a single parent, we don't need to check for cycles for each child

		// We're done, no cycles
		return false;
	}

	/**
	 * Overrides the default get all logic to return only the root categories (ie, categories with no parent).
	 * @param pagingInfo The {@link PagingInfo} object to load with the record count.
	 * @return The root categories.
	 */
	@Override
	public List<Category> getAll(boolean includeRetired, PagingInfo pagingInfo) {
		IMetadataAuthorizationPrivileges privileges = getPrivileges();
		if (privileges != null && !StringUtils.isEmpty(privileges.getGetPrivilege())) {
			Context.requirePrivilege(privileges.getGetPrivilege());
		}

		Criteria criteria = repository.createCriteria(Category.class);
		criteria.add(Restrictions.isNull("parentCategory"));
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}

		loadPagingTotal(pagingInfo, criteria);
		return repository.select(getEntityClass(), createPagingCriteria(pagingInfo, criteria));
	}
}
