package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.User;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockRoom;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction;

import java.util.List;

public interface IStockRoomTransactionDataService extends IObjectDataService<StockRoomTransaction> {
	/**
	 * Returns the {@link StockRoomTransaction} with the specified transaction number.
	 * @param transactionNumber The transaction number.
	 * @return The {@link StockRoomTransaction} or {@code null} if there is no stock room with the specified
	 * transaction number.
	 * @should return null if transaction number not found
	 * @should return transaction with specified transaction number
	 * @should throw IllegalArgumentException if transaction number is null
	 * @should throw IllegalArgumentException if transaction number is empty
	 * @should throw IllegalArgumentException is transaction number is longer than 50 characters
	 */
	StockRoomTransaction getTransactionByNumber(String transactionNumber);

	/**
	 * Returns the {@link StockRoomTransaction}s for the specified {@link StockRoom}.
	 * @param stockRoom The {@link StockRoom} that the transactions occurred in.
	 * @param paging The paging information or {@code null} to return all results.
	 * @return The transactions for the specified stock room.
	 * @should return transactions for specified room
	 * @should return empty list when no transactions
	 * @should return paged transactions when paging is specified
	 * @should return all transactions when paging is null
	 * @should return transactions with any status
	 * @should throw NullPointerException when stock room is null
	 */
	List<StockRoomTransaction> getTransactionsByRoom(StockRoom stockRoom, PagingInfo paging);

	/**
	 * Returns the {@link StockRoomTransaction}s that are pending and can be completed by the specified
	 * {@link User}.
	 * @param user The {@link User}.
	 * @param paging The paging information or {@code null} to return all results.
	 * @return The pending transactions for the specified user.
	 * @should return all pending transactions for specified user
	 * @should return all pending transactions for specified user role
	 * @should not return any completed or cancelled transactions
	 * @should return empty list when no transactions
	 * @should return paged transactions when paging is specified
	 * @should return all transactions when paging is null
	 * @should throw NullPointerException when user is null
	 */
	List<StockRoomTransaction> getUserPendingTransactions(User user, PagingInfo paging);
}

