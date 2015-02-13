package org.openmrs.module.openhmis.inventory.api.model;

/**
 * The allowable {@link StockOperation} statuses.
 */
public enum StockOperationStatus {
	/**
	 * The operation is being created but has not yet been submitted.
	 */
	NEW(),
	/**
	 * The operation has been requested but not yet started.
	 */
	REQUESTED(),
	/**
	 * The operation has been started and the associated items are being processed.
	 */
	PENDING(),
	/**
	 * The operation was cancelled and the pending transactions were reversed.
	 */
	CANCELLED(),
	/**
	 * The operation was completed and the pending transactions were applied.
	 */
	COMPLETED(),
	/**
	 * The operation was rolled back and all applied transactions were reversed.
	 */
	ROLLBACK();

	private StockOperationStatus() { }
}
