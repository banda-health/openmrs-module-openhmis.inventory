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
package org.openmrs.module.openhmis.inventory.api.model;

import org.apache.commons.lang.StringUtils;
import org.openmrs.BaseOpenmrsMetadata;

import java.util.ArrayList;
import java.util.List;

public class Category extends BaseOpenmrsMetadata {
	public static final long serialVersionUID = 0L;

	private Integer categoryId;
	private Category parentCategory;
	private List<Category> categories;

	public Category() {
		super();
	}

	public Category(String name) {
		super();

		super.setName(name);
	}

	@Override
	public Integer getId() {
		return this.categoryId;
	}

	@Override
	public void setId(Integer id) {
		this.categoryId = id;
	}

	public Category getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(Category parentCategory) {
		this.parentCategory = parentCategory;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public Category addCategory(String name) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("The item category name must be defined.");
		}

		Category category = new Category(name);
		addCategory(category);

		return category;
	}

	public void addCategory(Category category) {
		if (category != null) {
			if (categories == null) {
				categories = new ArrayList<Category>();
			}

			categories.add(category);
		}
	}

	public void removeCode(Category category) {
		if (category != null) {
			if (categories == null) {
				return;
			}

			categories.remove(category);
		}
	}
}
