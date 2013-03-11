package org.openmrs.module.openhmis.inventory.api;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.model.Category;

import java.util.Iterator;
import java.util.Set;

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
		// This is the number of root categories
		return 3;
	}

	@Override
	protected void updateEntityFields(Category category) {
		category.setDescription(category.getDescription() + " Updated");
		category.setName(category.getName() + " Updated");

		Set<Category> categories = category.getCategories();
		if (categories.size() > 0) {
			// Update an existing category
			Iterator<Category> iterator = categories.iterator();
			Category child = iterator.next();
			child.setName(child.getName() + " Updated");

			if (categories.size() > 1) {
				// Delete an existing category
				child = iterator.next();

				category.removeCategory(child);
			}
		}

		// Add a new category
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

		if (expected.getCategories() == null) {
			Assert.assertNull(actual.getCategories());
		} else {
			Assert.assertEquals(expected.getCategories().size(), actual.getCategories().size());
			Category[] expectedChildren = new Category[expected.getCategories().size()];
			expected.getCategories().toArray(expectedChildren);
			Category[] actualChildren = new Category[actual.getCategories().size()];
			actual.getCategories().toArray(actualChildren);
			for (int i = 0; i < expected.getCategories().size(); i++) {
				Assert.assertEquals(expectedChildren[i].getId(), actualChildren[i].getId());
				Assert.assertEquals(expectedChildren[i].getName(), actualChildren[i].getName());

				assertEntity(expectedChildren[i], actualChildren[i]);
			}
		}
	}

	@Test
	public void save_shouldSaveAllChildCategoriesWithParent() {
		Category parent = new Category("parent");
		Category child = parent.addCategory("child");
		Category child2 = parent.addCategory("child2");
		Category grandchild = child.addCategory("grandchild");

		// Save all categories
		service.save(parent);
		Context.flushSession();

		// Test that hierarchy was saved properly
		Assert.assertNotNull(parent.getId());
		Assert.assertEquals(2, parent.getCategories().size());
		Category[] categories = new Category[parent.getCategories().size()];
		parent.getCategories().toArray(categories);
		Assert.assertEquals(child, categories[0]);
		Assert.assertEquals(child2, categories[1]);
		Assert.assertEquals(1, child.getCategories().size());
		categories = new Category[child.getCategories().size()];
		child.getCategories().toArray(categories);
		Assert.assertEquals(grandchild, categories[0]);

		// Test that id's were created for each category
		Assert.assertNotNull(parent.getId());
		Assert.assertNotNull(child.getId());
		Assert.assertNotNull(child2.getId());
		Assert.assertNotNull(grandchild.getId());

		// Test that categories are retrieved properly
		Category getParent = service.getById(parent.getId());

		Assert.assertNotNull(getParent.getId());
		Assert.assertEquals(2, getParent.getCategories().size());
		categories = new Category[getParent.getCategories().size()];
		getParent.getCategories().toArray(categories);
		Assert.assertEquals(child.getId(), categories[0].getId());
		Assert.assertEquals(child2.getId(), categories[1].getId());

		Category getChild = categories[0];
		Assert.assertEquals(1, getChild.getCategories().size());
		categories = new Category[getChild.getCategories().size()];
		getChild.getCategories().toArray(categories);
		Assert.assertEquals(grandchild.getId(), categories[0].getId());
	}

	@Test
	public void save_shouldAllowCategoriesToBeMoved() {
		Category parent = new Category("parent");
		Category child = parent.addCategory("child");
		Category grandchild = child.addCategory("grandchild");

		// Save all categories
		service.save(parent);
		Context.flushSession();

		Assert.assertEquals(1, parent.getCategories().size());
		Category[] categories = new Category[parent.getCategories().size()];
		parent.getCategories().toArray(categories);
		Assert.assertEquals(child, categories[0]);
		Assert.assertNotNull(grandchild.getId());
		Integer grandChildId = grandchild.getId();

		// Move the grandchild to a child of the parent
		grandchild.setParentCategory(parent);

		service.save(grandchild);
		Context.flushSession();

		Assert.assertEquals(2, parent.getCategories().size());
		categories = new Category[parent.getCategories().size()];
		parent.getCategories().toArray(categories);
		Assert.assertEquals(child, categories[0]);
		Assert.assertEquals(grandchild, categories[1]);

		// Make sure the category id did not change
		Assert.assertEquals(grandChildId, grandchild.getId());
	}

	@Test
	public void save_shouldThrowApiExceptionIfCategoryCycle() {
		Category parent = new Category("parent");
		Category child = parent.addCategory("child");
		Category grandchild = child.addCategory("grandchild");

		// Save all categories
		service.save(parent);
		Context.flushSession();

		Assert.assertNotNull(parent.getId());
		Assert.assertNotNull(child.getId());
		Assert.assertNotNull(grandchild.getId());

		// Set parent category of parent to child
		APIException expected = null;
		parent.setParentCategory(child);
		try {
			service.save(parent);
		} catch (APIException ex) {
			expected = ex;
		}
		Assert.assertNotNull(expected);

		// Set parent category of parent to grandchild
		expected = null;
		parent.setParentCategory(grandchild);
		try {
			service.save(parent);
		} catch (APIException ex) {
			expected = ex;
		}
		Assert.assertNotNull(expected);
	}
}
