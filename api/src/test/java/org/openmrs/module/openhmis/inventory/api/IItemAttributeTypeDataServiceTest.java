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
import org.openmrs.module.openhmis.inventory.api.model.ItemAttributeType;

public class IItemAttributeTypeDataServiceTest
        extends IMetadataDataServiceTest<IItemAttributeTypeDataService, ItemAttributeType> {
	public static final String DATASET = TestConstants.BASE_DATASET_DIR + "ItemAttributeTypeTest.xml";

	@Override
	public void before() throws Exception {
		super.before();

		executeDataSet(DATASET);
	}

	@Override
	protected int getTestEntityCount() {
		return 7;
	}

	@Override
	public ItemAttributeType createEntity(boolean valid) {
		ItemAttributeType itemAttributeType = new ItemAttributeType();

		if (valid) {
			itemAttributeType.setName("new department");
		}

		itemAttributeType.setDescription("new department description");

		return itemAttributeType;
	}

	@Override
	protected void updateEntityFields(ItemAttributeType itemAttributeType) {
		itemAttributeType.setName(itemAttributeType.getName() + " updated");
		itemAttributeType.setDescription(itemAttributeType.getDescription() + " updated");
	}

	@Override
	protected void assertEntity(ItemAttributeType expected, ItemAttributeType actual) {
		super.assertEntity(expected, actual);
	}
}
