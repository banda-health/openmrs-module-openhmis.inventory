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

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openmrs.OpenmrsObject;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.model.Department;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Transactional
public class ItemDataServiceImpl
		extends BaseMetadataDataServiceImpl<Item>
		implements IItemDataService, IMetadataAuthorizationPrivileges {
	@Override
	protected void validate(Item entity) throws APIException {
		return;
	}

	@Override
	protected Collection<? extends OpenmrsObject> getRelatedObjects(Item entity) {
		ArrayList<OpenmrsObject> results = new ArrayList<OpenmrsObject>();

		results.addAll(entity.getCodes());
		results.addAll(entity.getPrices());
		results.addAll(entity.getAttributes());

		return results;
	}

	@Override
	@Authorized( { PrivilegeConstants.VIEW_ITEMS } )
	@Transactional(readOnly = true)
	public Item getItemByCode(String itemCode) throws APIException {
		if (StringUtils.isEmpty(itemCode)) {
			throw new IllegalArgumentException("The item code must be defined.");
		}
		if (itemCode.length() > 255) {
			throw new IllegalArgumentException("The item code must be less than 256 characters.");
		}

		Criteria criteria = repository.createCriteria(getEntityClass());
		criteria.createAlias("codes", "c")
				.add(Restrictions.ilike("c.code", itemCode));

		return repository.selectSingle(getEntityClass(), criteria);
	}

	@Override
	public List<Item> getItemsByDepartment(Department department, boolean includeRetired) throws APIException {
		return getItemsByDepartment(department, includeRetired, null);
	}

	@Override
	public List<Item> getItemsByDepartment(final Department department, final boolean includeRetired, PagingInfo pagingInfo) throws APIException {
		if (department == null) {
			throw new NullPointerException("The department must be defined");
		}

		return executeCriteria(Item.class, pagingInfo, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq("department", department));
				if (!includeRetired) {
					criteria.add(Restrictions.eq("retired", false));
				}
			}
		});
	}

	@Override
	@Authorized( { PrivilegeConstants.VIEW_ITEMS } )
	@Transactional(readOnly = true)
	public List<Item> findItems(Department department, String name, boolean includeRetired) throws APIException {
		return findItems(department, name, includeRetired, null);
	}
	
	@Override
	@Authorized( { PrivilegeConstants.VIEW_ITEMS } )
	@Transactional(readOnly = true)
	public List<Item> findItems(final Department department, final String name, final boolean includeRetired, PagingInfo pagingInfo) throws APIException {
		if (department == null) {
			throw new NullPointerException("The department must be defined");
		}
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("The item code must be defined.");
		}
		if (name.length() > 255) {
			throw new IllegalArgumentException("The item code must be less than 256 characters.");
		}

		return executeCriteria(Item.class, pagingInfo, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq("department", department))
						.add(Restrictions.ilike("name", name, MatchMode.START));

				if (!includeRetired) {
					criteria.add(Restrictions.eq("retired", false));
				}
			}
		});
	}

	@Override
	@Authorized( { PrivilegeConstants.VIEW_ITEMS } )
	public List<Item> findItems(ItemSearch itemSearch) {
		return findItems(itemSearch, null);
	}

	@Override
	@Authorized( { PrivilegeConstants.VIEW_ITEMS } )
	public List<Item> findItems(final ItemSearch itemSearch, PagingInfo pagingInfo) {
		if (itemSearch == null) {
			throw new NullPointerException("The item search must be defined.");
		} else if (itemSearch.getTemplate() == null) {
			throw new NullPointerException("The item search template must be defined.");
		}

		return executeCriteria(Item.class, pagingInfo, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				itemSearch.updateCriteria(criteria);
			}
		});
	}

	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return this;
	}

	@Override
	public String getSavePrivilege() {
		return PrivilegeConstants.MANAGE_ITEMS;
	}

	@Override
	public String getPurgePrivilege() {
		return PrivilegeConstants.PURGE_ITEMS;
	}

	@Override
	public String getGetPrivilege() {
		return PrivilegeConstants.VIEW_ITEMS;
	}

	@Override
	public String getRetirePrivilege() {
		return PrivilegeConstants.MANAGE_ITEMS;
	}
}
