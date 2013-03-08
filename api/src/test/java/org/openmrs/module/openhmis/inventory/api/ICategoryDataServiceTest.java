package org.openmrs.module.openhmis.inventory.api;

import org.junit.Assert;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.model.Category;

import java.util.Iterator;
import java.util.List;

public class ICategoryDataServiceTest extends IMetadataDataServiceTest<ICategoryDataService, Category> {
	public static final String CATEGORY_DATASET = TestConstants.BASE_DATASET_DIR + "CategoryTest.xml";

	@Override
	public void before() throws Exception{
		super.before();

		executeDataSet(CATEGORY_DATASET);
	}

	@Override
	protected Category createEntity(boolean valid) {
		Category category = new Category();
		if (valid) {
			category.setName("new category");
		}

		category.setDescription("new category description");

		category.addCategory("new sub category 1");
		category.addCategory("new sub category 2");

		return category;
	}

	@Override
	protected int getTestEntityCount() {
		return 6;
	}

	@Override
	protected void updateEntityFields(Category category) {
		category.setDescription(category.getDescription() + " Updated");
		category.setName(category.getName() + " Updated");

		List<Category> categories = category.getCategories();
		if (categories.size() > 0) {
			// Update an existing code
			Iterator<Category> iterator = categories.iterator();
			Category child = iterator.next();
			child.setName(child.getName() + " Updated");

			if (categories.size() > 1) {
				// Delete an existing code
				child = iterator.next();

				category.removeCategory(child);
			}
		}

		// Add a new code
		category.addCategory("new sub category");
	}

	@Override
	protected void assertEntity(Category expected, Category actual) {
		super.assertEntity(expected, actual);

		if (expected.getParentCategory() == null) {
			Assert.assertNull(actual.getParentCategory());
		} else {
			Assert.assertEquals(expected.getParentCategory().getId(), actual.getParentCategory().getId());
		}

		Assert.assertEquals(expected.getCategories().size(), actual.getCategories().size());
		Category[] expectedChildren = new Category[expected.getCategories().size()];
		expected.getCategories().toArray(expectedChildren);
		Category[] actualChildren = new Category[actual.getCategories().size()];
		actual.getCategories().toArray(actualChildren);
		for (int i = 0; i < expected.getCategories().size(); i++) {
			Assert.assertEquals(expectedChildren[i].getId(), actualChildren[i].getId());
			Assert.assertEquals(expectedChildren[i].getName(), actualChildren[i].getName());
		}
	}
}
