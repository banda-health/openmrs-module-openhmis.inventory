package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemAttribute;
import org.openmrs.module.openhmis.inventory.api.model.ItemAttributeType;

public class IItemAttributeDataServiceTest extends
        IMetadataDataServiceTest<IItemAttributeDataService, ItemAttribute> {
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
		ItemAttributeType itemAttributeType = itemAttributeTypeDataService.getById(0);
		Item item = itemDataService.getById(0);

		ItemAttribute itemAttribute = new ItemAttribute();
		itemAttribute.setOwner(item);
		if (valid) {
			itemAttribute.setValue("100");
		}

		itemAttribute.setAttributeType(itemAttributeType);
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
		itemAttribute.setValue("200");
	}

	@Override
	public void getByNameFragment_shouldThrowIllegalArgumentExceptionIfTheNameIsNull() throws Exception {
		throw new IllegalArgumentException();
	}

	@Override
	public void getByNameFragment_shouldThrowIllegalArgumentExceptionIfTheNameIsEmpty() throws Exception {
		throw new IllegalArgumentException();
	}

	@Override
	public void getByNameFragment_shouldThrowIllegalArgumentExceptionIfTheNameIsLongerThan255Characters() throws Exception {
		throw new IllegalArgumentException();
	}

	@Override
	public void getByNameFragment_shouldReturnAnEmptyListIfNoMetadataAreFound() throws Exception {}

	@Override
	public void getByNameFragment_shouldNotReturnRetiredMetadataUnlessSpecified() throws Exception {}

	@Override
	public void getByNameFragment_shouldReturnMetadataThatStartWithTheSpecifiedName() throws Exception {}

	@Override
	public void getByNameFragment_shouldReturnAllSpecifiedMetadataRecordsIfPagingIsNull() throws Exception {}

	@Override
	public void getByNameFragment_shouldReturnAllSpecifiedMetadataRecordsIfPagingPageOrSizeIsLessThanOne() throws Exception {}

	@Override
	public void getByNameFragment_shouldSetThePagingTotalRecordsToTheTotalNumberOfMetadataRecords() throws Exception {}

	@Override
	public void getByNameFragment_shouldNotGetTheTotalPagingRecordCountIfItIsMoreThanZero() throws Exception {}

	@Override
	public void getByNameFragment_shouldReturnPagedMetadataRecordsIfPagingIsSpecified() throws Exception {}
}
