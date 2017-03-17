package org.openmrs.module.openhmis.inventory.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.BaseModuleContextTest;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemConceptSuggestion;

public class IItemConceptSuggestionDataServiceTest extends BaseModuleContextTest {
	public static final String ITEM_DATASET = TestConstants.BASE_DATASET_DIR + "ItemTest.xml";

	private IItemConceptSuggestionDataService service;
	private IItemDataService itemService;
	private ConceptService conceptService;

	@Before
	public void before() throws Exception {
		service = Context.getService(IItemConceptSuggestionDataService.class);
		itemService = Context.getService(IItemDataService.class);
		conceptService = Context.getConceptService();

		executeDataSet(ITEM_DATASET);
	}

	@Test
	public void getItemsWithConceptSuggestions_shouldOnlyItemsConceptsWhereConceptSuggestionsAreNotAccepted()
	        throws Exception {
		Item item1 = itemService.getById(0);
		Item item2 = itemService.getById(1);
		Item item3 = itemService.getById(2);
		Item item4 = itemService.getById(5);

		item1.setConceptAccepted(true);
		item2.setConceptAccepted(true);
		item3.setConceptAccepted(true);
		item4.setConceptAccepted(true);

		List<ItemConceptSuggestion> itemsWithConceptSuggestions = service.getItemsWithConceptSuggestions();
		assertNotNull(itemsWithConceptSuggestions);
		assertEquals(0, itemsWithConceptSuggestions.size());

		item1.setConceptAccepted(false);
		item2.setConceptAccepted(false);
		item3.setConceptAccepted(false);
		item4.setConceptAccepted(false);

		itemsWithConceptSuggestions = service.getItemsWithConceptSuggestions();
		assertEquals(4, itemsWithConceptSuggestions.size());
	}

	@Test
	public void getItemsWithConceptSuggestions_shouldReturnEmptyListIfNoSuggestionsFound() throws Exception {
		Item item1 = itemService.getById(0);
		Item item2 = itemService.getById(1);
		Item item3 = itemService.getById(2);
		Item item4 = itemService.getById(5);

		item1.setConceptAccepted(true);
		item2.setConceptAccepted(true);
		item3.setConceptAccepted(true);
		item4.setConceptAccepted(true);

		List<ItemConceptSuggestion> itemsWithConceptSuggestions = service.getItemsWithConceptSuggestions();
		assertNotNull(itemsWithConceptSuggestions);
		assertEquals(0, itemsWithConceptSuggestions.size());
	}

	@Test
	public void getItemsWithConceptSuggestions_shouldFindSuggestionsAccordingToItemName() throws Exception {
		Item item1 = itemService.getById(0);
		Item item2 = itemService.getById(1);

		item1.setConceptAccepted(true);
		item2.setConceptAccepted(true);

		Concept matchingConcept = conceptService.getConcept(1);

		List<ItemConceptSuggestion> itemsWithConceptSuggestions = service.getItemsWithConceptSuggestions();
		assertEquals(2, itemsWithConceptSuggestions.size());

		for (ItemConceptSuggestion itemConceptSuggestion : itemsWithConceptSuggestions) {
			if (itemConceptSuggestion.getItemId() == 5) {
				assertEquals("test 6", itemConceptSuggestion.getConceptName());
				assertEquals(matchingConcept.getUuid(), itemConceptSuggestion.getConceptUuid());
			} else if (itemConceptSuggestion.getItemId() == 2) {
				assertTrue(StringUtils.isEmpty(itemConceptSuggestion.getConceptName()));
				assertTrue(StringUtils.isEmpty(itemConceptSuggestion.getConceptUuid()));
			}

		}
	}

	@Test
	public void getItemsWithConceptSuggestions_shouldNotFindRetiredConcepts() throws Exception {
		Item item1 = itemService.getById(0);
		Item item2 = itemService.getById(1);

		item1.setConceptAccepted(true);
		item2.setConceptAccepted(true);

		Concept retiredConcept = conceptService.getConcept(1);
		retiredConcept.setRetired(true);

		List<ItemConceptSuggestion> itemsWithConceptSuggestions = service.getItemsWithConceptSuggestions();
		assertEquals(2, itemsWithConceptSuggestions.size());

		for (ItemConceptSuggestion itemConceptSuggestion : itemsWithConceptSuggestions) {
			if (itemConceptSuggestion.getItemId() == 5) {
				assertTrue(StringUtils.isEmpty(itemConceptSuggestion.getConceptName()));
				assertTrue(StringUtils.isEmpty(itemConceptSuggestion.getConceptUuid()));
			} else if (itemConceptSuggestion.getItemId() == 2) {
				assertTrue(StringUtils.isEmpty(itemConceptSuggestion.getConceptName()));
				assertTrue(StringUtils.isEmpty(itemConceptSuggestion.getConceptUuid()));
			}

		}
	}
}
