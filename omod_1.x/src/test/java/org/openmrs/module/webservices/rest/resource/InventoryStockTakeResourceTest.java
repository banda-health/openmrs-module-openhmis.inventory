package org.openmrs.module.webservices.rest.resource;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.api.IStockOperationService;
import org.openmrs.module.openhmis.inventory.api.WellKnownOperationTypes;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockSummary;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.model.InventoryStockTake;
import org.openmrs.module.webservices.rest.helper.IdgenHelper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.client.RestClientException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, StockOperationTypeResource.class,
        WellKnownOperationTypes.class, IdgenHelper.class })
public class InventoryStockTakeResourceTest {

	private IStockOperationService stockOperationService;
	private InventoryStockTakeResource resource;
	private InventoryStockTake delegate;

	private Item item1, item2;
	private ItemStockSummary iss1, iss2;

	@Before
	public void setUp() {
		mockStatic(Context.class);
		when(Context.getService(IStockOperationService.class)).thenReturn(stockOperationService);

		mockStatic(WellKnownOperationTypes.class);
		when(WellKnownOperationTypes.getAdjustment()).thenReturn(null);

		mockStatic(StockOperationTypeResource.class);
		mockStatic(IdgenHelper.class);

		Stockroom stockroom = new Stockroom();
		stockroom.setName("TestStockroom");

		resource = new InventoryStockTakeResource();
		delegate = resource.newDelegate();

		delegate.setStockroom(stockroom);
		delegate.setOperationNumber("M-Test-2");

		item1 = new Item(1);
		item2 = new Item(2);

		iss1 = new ItemStockSummary();
		iss1.setExpiration(null);
		iss1.setItem(item1);
		iss1.setActualQuantity(3);
		iss1.setQuantity(5);

		iss2 = new ItemStockSummary();
		iss2.setExpiration(null);
		iss2.setItem(item2);
		iss2.setActualQuantity(5);
		iss2.setQuantity(0);
	}

	@Test(expected = RestClientException.class)
	public void save_shouldThrowExceptionIfUserNotAuthorised() throws Exception {
		when(StockOperationTypeResource.userCanProcess(WellKnownOperationTypes.getAdjustment())).thenReturn(false);
		resource.save(delegate);
	}

	@Test
	public void createOperation_shouldCreateOperation_generatedOperationNumber() throws Exception {
		when(StockOperationTypeResource.userCanProcess(WellKnownOperationTypes.getAdjustment())).thenReturn(true);
		when(IdgenHelper.isOperationNumberGenerated()).thenReturn(true);
		when(IdgenHelper.generateId()).thenReturn("A-Test-1");

		List<ItemStockSummary> itemStockSummaries = new ArrayList<ItemStockSummary>(2);
		itemStockSummaries.add(iss1);
		itemStockSummaries.add(iss2);

		delegate.setItemStockSummaryList(itemStockSummaries);

		StockOperation operation = resource.createOperation(delegate);

		Assert.assertEquals("A-Test-1", operation.getOperationNumber());
		Assert.assertEquals(2, operation.getItems().size());

		Set<StockOperationItem> items = operation.getItems();
		for (StockOperationItem item : items) {
			if (item.getItem().getId() == 1) {
				Assert.assertEquals(new Integer(-2), item.getQuantity());
			}
			if (item.getItem().getId() == 2) {
				Assert.assertEquals(new Integer(5), item.getQuantity());
			}
		}
	}

	@Test
	public void createOperation_shouldCreateOperation_manualOperationNumber() throws Exception {
		when(StockOperationTypeResource.userCanProcess(WellKnownOperationTypes.getAdjustment())).thenReturn(true);
		when(IdgenHelper.isOperationNumberGenerated()).thenReturn(false);

		List<ItemStockSummary> itemStockSummaries = new ArrayList<ItemStockSummary>(2);
		itemStockSummaries.add(iss1);
		itemStockSummaries.add(iss2);

		delegate.setItemStockSummaryList(itemStockSummaries);

		StockOperation operation = resource.createOperation(delegate);

		Assert.assertEquals("M-Test-2", operation.getOperationNumber());
	}
}
