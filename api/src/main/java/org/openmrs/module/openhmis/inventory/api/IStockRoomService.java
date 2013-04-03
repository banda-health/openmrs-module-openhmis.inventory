package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.inventory.api.model.StockRoom;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransactionType;

import java.util.Date;
import java.util.List;

public interface IStockRoomService {
	List<StockRoom> getStockRooms();

	List<StockRoomTransactionType> getTransactionTypes();

	List<StockRoomTransaction> getTransactions(StockRoom stockRoom);

	List<StockRoomTransaction> getTransactions(Date from, Date to);

	/**
	 * Validates and submits the specified {@link StockRoomTransaction}. The transaction may be completed if all the required
	 * attributes are defined, otherwise the status will be PENDING.
	 * @param transaction The {@link StockRoomTransaction} to submit.
	 * @throws APIException
	 */
	void submitTransaction(StockRoomTransaction transaction) throws APIException;

	void completeTransaction(StockRoomTransaction transaction) throws APIException;
}
