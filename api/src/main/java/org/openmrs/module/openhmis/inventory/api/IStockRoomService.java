package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.inventory.api.model.StockRoom;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransactionType;

import java.util.List;

public interface IStockRoomService {
	List<StockRoom> getStockRooms();

	List<StockRoomTransactionType> getTransactionTypes();

	List<StockRoomTransaction> getTransactions(StockRoom stockRoom);

	/**
	 * Creates a new {@link StockRoomTransaction} and validates the settings.
	 * @param type The transaction type.
	 * @param source The optional source {@link StockRoom}.
	 * @param destination The optional destination {@link StockRoom}.
	 * @return A newly created {@link StockRoomTransaction}.
	 */
	StockRoomTransaction createTransaction(StockRoomTransactionType type, StockRoom source, StockRoom destination) throws APIException;

	/**
	 * Validates and submits the specified {@link StockRoomTransaction}. This will subtract the item quantities from the
	 * source stock room, if one is defined.  The transaction may be completed if all the required
	 * attributes are defined, otherwise the status will be PENDING.  If the transaction is completed the item quantities
	 * will added to the destination stock room, if one is defined.
	 * @param transaction The {@link StockRoomTransaction} to submit.
	 * @throws APIException
	 */
	void submitTransaction(StockRoomTransaction transaction) throws APIException;

	/**
	 * Validates and completes the specified {@link StockRoomTransaction}.  This will add the transaction item quantities
	 * to the destination stock room, if one is defined.
	 * @param transaction The {@link StockRoomTransaction} to complete.
	 * @throws APIException
	 */
	void completeTransaction(StockRoomTransaction transaction) throws APIException;
}
