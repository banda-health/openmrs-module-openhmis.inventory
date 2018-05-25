package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemAttribute;
import org.openmrs.module.openhmis.inventory.api.model.ItemAttributeType;

public class IItemAttributeDataServiceTest extends IObjectDataServiceTest<IItemAttributeDataService, ItemAttribute> {
	public static final String ITEM_ATTRIBUTE_DATASET = TestConstants.BASE_DATASET_DIR + "ItemAttributeTest.xml";

	private IItemDataService itemDataService;
	private IItemAttributeTypeDataService itemAttributeTypeDataService;

	@Override
	public void before() throws Exception {
		super.before();

		this.itemDataService = Context.getService(IItemDataService.class);
		this.itemAttributeTypeDataService = Context.getService(IItemAttributeTypeDataService.class);

		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(IItemAttributeTypeDataServiceTest.DATASET);
		executeDataSet(ITEM_ATTRIBUTE_DATASET);
	}

	@Override
	public ItemAttribute createEntity(boolean valid) {
		ItemAttribute itemAttribute = new ItemAttribute();

		Item item = itemDataService.getById(0);
		ItemAttributeType itemAttributeType = itemAttributeTypeDataService.getById(0);

		if (valid) {
			itemAttribute.setOwner(item);
		}

		itemAttribute.setAttributeType(itemAttributeType);
		itemAttribute.setId(itemAttributeType.getId());
		itemAttribute.setValue("100");

		return itemAttribute;
	}

	@Override
	protected int getTestEntityCount() {
		return 6;
	}

	@Override
	protected void updateEntityFields(ItemAttribute itemAttribute) {
		itemAttribute.setOwner(itemDataService.getById(1));
		itemAttribute.setAttributeType(itemAttributeTypeDataService.getById(1));
		itemAttribute.setId(itemAttribute.getAttributeType().getId());
		itemAttribute.setValue("200");
	}
}
