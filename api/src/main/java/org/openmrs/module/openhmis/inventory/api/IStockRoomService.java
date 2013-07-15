package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.inventory.api.model.StockRoom;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransactionType;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IStockRoomService {
	/**
	 * Gets all the {@link StockRoom}s.
	 * @return A list containing all the {@link StockRoom}s.
	 * @should return all the stock rooms
	 * @should return an empty list if there are no stock rooms
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_STOCK_ROOMS})
	List<StockRoom> getStockRooms();

	/**
	 * Gets all the {@link StockRoomTransactionType}s.
	 * @return A list containing all the {@link StockRoomTransactionType}s.
	 * @should return all the transaction types
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_STOCK_ROOMS})
	List<StockRoomTransactionType> getTransactionTypes();

	/**
	 * Gets all the {@link StockRoomTransaction}s for the specified stock room.
	 * @param stockRoom The {@link StockRoom} to find the transactions for.
	 * @return A list containing all the {@link StockRoomTransaction} for the specified stock room.
	 * @should return all the transactions for the stock room
	 * @should not return transactions for other stock rooms
	 * @should return an empty list if there are no transactions
	 * @should throw a NullReferenceException if the stock room is null
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_TRANSACTIONS})
	List<StockRoomTransaction> getTransactions(StockRoom stockRoom);

	/**
	 * Creates a new {@link StockRoomTransaction} and validates the settings.
	 * @param type The transaction type.
	 * @param source The optional source {@link StockRoom}.
	 * @param destination The optional destination {@link StockRoom}.
	 * @return A newly created {@link StockRoomTransaction}.
	 * @should create a new stock room transaction with the specified settings
	 * @should throw a NullReferenceException if the transaction type is null
	 * @should throw an APIException if the type requires a source and the source is null
	 * @should throw an APIException if the type requires a destination and the destination is null
	 */
	@Transactional
	@Authorized( {PrivilegeConstants.MANAGE_TRANSACTIONS})
	StockRoomTransaction createTransaction(StockRoomTransactionType type, StockRoom source, StockRoom destination) throws APIException;

	/**
	 * Validates and submits the specified {@link StockRoomTransaction}. This will subtract the item quantities from the
	 * source stock room, if one is defined.  The transaction may be completed if all the required
	 * attributes are defined, otherwise the status will be PENDING.  If the transaction is completed the item quantities
	 * will added to the destination stock room, if one is defined.
	 * @param transaction The {@link StockRoomTransaction} to submit.
	 * @throws APIException
	 * @should update the source stock room item quantities
	 * @should remove empty items from the source stock room
	 * @should set the correct quantity type for the transaction item quantity
	 * @should not attempt to update the source stock room if none is defined
	 * @should throw an APIException if the transaction type is null
	 * @should throw an APIException if the transaction has no items
	 * @should throw an APIException if the transaction type requires a source and the source is null
	 * @should throw an APIException if the transaction type requires a destination and the destination is null
	 */
	@Transactional
	@Authorized( {PrivilegeConstants.MANAGE_TRANSACTIONS})
	void submitTransaction(StockRoomTransaction transaction) throws APIException;

	/**
	 * Validates and completes the specified {@link StockRoomTransaction}.  This will add the transaction item quantities
	 * to the destination stock room, if one is defined.
	 * @param transaction The {@link StockRoomTransaction} to complete.
	 * @throws APIException
	 * @should update the destination stock room item quantities
	 * @should create a new item if the destination stock room does not have the item
	 * @should set the transaction item quantity to zero
	 * @should not attempt to update the destination stock room if none is defined
	 * @should throw an APIException if the transaction type is null
	 * @should throw an APIException if the transaction has no items
	 * @should throw an APIException if the transaction type requires a source and the source is null
	 * @should throw an APIException if the transaction type requires a destination and the destination is null
	 */
	@Transactional
	@Authorized( {PrivilegeConstants.MANAGE_TRANSACTIONS})
	void completeTransaction(StockRoomTransaction transaction) throws APIException;
}
