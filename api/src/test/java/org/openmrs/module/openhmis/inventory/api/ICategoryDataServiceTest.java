package org.openmrs.module.openhmis.inventory.api;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.inventory.api.model.Category;

import java.util.Arrays;
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
	public Category createEntity(boolean valid) {
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
			assertCollection(expected.getCategories(), actual.getCategories(), new Action2<Category, Category>() {
				@Override
				public void apply(Category expectedCategory, Category actualCategory) {
					assertOpenmrsMetadata(expectedCategory, actualCategory);

					assertEntity(expectedCategory, actualCategory);
				}
			});
		}
	}

	@Test
	public void save_shouldSaveAllChildCategoriesWithParent() {
		Category parent = new Category("parent");
		Category child = parent.addCategory("a child");
		Category child2 = parent.addCategory("b child");
		Category grandchild = child.addCategory("grandchild");

		// Save all categories
		service.save(parent);
		Context.flushSession();

		// Test that hierarchy was saved properly
		Assert.assertNotNull(parent.getId());
		Assert.assertEquals(2, parent.getCategories().size());
		Assert.assertTrue(parent.getCategories().contains(child));
		Assert.assertTrue(parent.getCategories().contains(child2));
		Assert.assertEquals(1, child.getCategories().size());
		Category[] categories = new Category[child.getCategories().size()];
		child.getCategories().toArray(categories);
		Assert.assertEquals(grandchild, categories[0]);

		// Test that id's were created for each category
		Assert.assertNotNull(parent.getId());
		Assert.assertNotNull(child.getId());
		Assert.assertNotNull(child2.getId());
		Assert.assertNotNull(grandchild.getId());
	}

	@Test
	public void save_shouldAllowCategoriesToBeMoved() {
		Category parent = new Category("parent");
		Category child = parent.addCategory("a child");
		Category grandchild = child.addCategory("b grandchild");

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

		// Refresh parent from db
		parent = service.getById(parent.getId());

		Assert.assertEquals(2, parent.getCategories().size());
		categories = new Category[parent.getCategories().size()];
		parent.getCategories().toArray(categories);
		Assert.assertTrue(Arrays.asList(categories).contains(child));
		Assert.assertTrue(Arrays.asList(categories).contains(grandchild));

		// Make sure the category id did not change
		Assert.assertEquals(grandChildId, grandchild.getId());

		// Make sure the category is no longer a child of child
		Assert.assertEquals(0, child.getCategories().size());
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

	@Test
	public void retire_shouldRetireAllChildCategories() {
		Category parent = service.getById(0);

		// Everything is currently not retired
		Assert.assertNotNull(parent);
		Assert.assertEquals(false, parent.getRetired());
		for (Category child : parent.getCategories()) {
			Assert.assertEquals(false, child.getRetired());
		}

		// Retire just the parent
		service.retire(parent, "something");
		Context.flushSession();

		// The parent and all children should now be retired
		Assert.assertEquals(true, parent.getRetired());
		for (Category child : parent.getCategories()) {
			Assert.assertEquals(true, child.getRetired());
		}

		parent = service.getById(0);
		Assert.assertEquals(true, parent.getRetired());
		for (Category child : parent.getCategories()) {
			Assert.assertEquals(true, child.getRetired());
		}
	}

	@Test
	public void unretire_shouldUnretireAllChildCategories() {
		Category parent = service.getById(0);

		service.retire(parent, "something");
		Context.flushSession();

		// Ensure that parent and children are now retired
		parent = service.getById(0);
		Assert.assertEquals(true, parent.getRetired());
		for (Category child : parent.getCategories()) {
			Assert.assertEquals(true, child.getRetired());
		}

		service.unretire(parent);
		Context.flushSession();

		Assert.assertEquals(false, parent.getRetired());
		for (Category child : parent.getCategories()) {
			Assert.assertEquals(false, child.getRetired());
		}

		parent = service.getById(0);
		Assert.assertEquals(false, parent.getRetired());
		for (Category child : parent.getCategories()) {
			Assert.assertEquals(false, child.getRetired());
		}
	}
}
