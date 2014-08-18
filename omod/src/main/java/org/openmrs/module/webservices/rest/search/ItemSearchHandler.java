@Test
    public void findItemsByConcept_shouldFindEveryItemWithTheSpecifiedConcept() throws Exception {
	    Concept concept = Context.getConceptService().getConcept(2);
	    List<Item> itemsByConcept = service.getItemsByConcept(concept);
	    Assert.assertEquals(2, itemsByConcept.size());
    }
	
	@Test
    public void findItemsWithoutConcept_shouldOnlyReturnItemsWithoutConcept() throws Exception {
	    List<Item> items = service.getItemsWithoutConcept(null, null);
	    
	    Assert.assertEquals(4, items.size());
	    Assert.assertTrue(items.contains(service.getById(0)));
	    Assert.assertTrue(items.contains(service.getById(1)));
	    Assert.assertTrue(items.contains(service.getById(2)));
	    Assert.assertTrue(items.contains(service.getById(5)));
    }
	
	@Test
    public void findItemsWithoutConcept_shouldLimitTheResultsToGivenSize() throws Exception {
		Integer resultLimit = 2;
		List<Item> items = service.getItemsWithoutConcept(null, resultLimit);
		Assert.assertEquals(2, items.size());
    }
	
	@Test
    public void findItemsWithoutConcept_shouldNotContainExcludedItems() throws Exception {
		Item excludedItem1 = service.getById(0);
		Item excludedItem2 = service.getById(1);
		
		List<Integer> excludedItemIds = new ArrayList<Integer>(2);
		excludedItemIds.add(excludedItem1.getId());
		excludedItemIds.add(excludedItem2.getId());
		
		List<Item> items = service.getItemsWithoutConcept(excludedItemIds, null);

		Assert.assertEquals(2, items.size());
		Assert.assertFalse(items.contains(excludedItem1));
		Assert.assertFalse(items.contains(excludedItem2));
		Assert.assertTrue(items.contains(service.getById(2)));
		Assert.assertTrue(items.contains(service.getById(5)));
    }
	
	@Test
    public void findItemsWithoutConcept_shouldReturnAnEmptyListIfNoResultsAreFound() throws Exception {
		Item excludedItem1 = service.getById(0);
		Item excludedItem2 = service.getById(1);
		Item excludedItem3 = service.getById(2);
		Item excludedItem4 = service.getById(5);
		
		List<Integer> excludedItemIds = new ArrayList<Integer>(2);
		excludedItemIds.add(excludedItem1.getId());
		excludedItemIds.add(excludedItem2.getId());
		excludedItemIds.add(excludedItem3.getId());
		excludedItemIds.add(excludedItem4.getId());

		List<Item> items = service.getItemsWithoutConcept(excludedItemIds, null);
		
		Assert.assertNotNull(items);
		Assert.assertEquals(0, items.size());
    }
	
	@Test
    public void findItemsWithoutConcept_shouldOnlyReturnItemsWhereConceptAcceptedIsFalse() throws Exception {
	    List<Item> items = service.getItemsWithoutConcept(null, null);
	    Assert.assertEquals(4, items.size());
	    
	    Item item = service.getById(0);
	    item.setConceptAccepted(true);
	    
	    items = service.getItemsWithoutConcept(null, null);
	    Assert.assertEquals(3, items.size());
	    
	    
    }
}
