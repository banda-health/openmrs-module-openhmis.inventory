package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.module.openhmis.commons.api.entity.model.IInstanceType;

/**
 * Represents classes that can act as a stock operation type.
 */
public interface IStockOperationType extends OpenmrsMetadata, IInstanceType<StockOperationAttributeType> {
	/**
	 * Returns whether the operation type has a source {@link Stockroom}.
	 * @return {@code true} if the operation type has a source stockroom; otherwise, {@code false}.
	 */
	Boolean getHasSource();

	/**
	 * Sets whether the operation type has a source {@link Stockroom}.
	 * @param hasSource Whether the operation type has a source stockroom.
	 */
	void setHasSource(Boolean hasSource);

	/**
	 * Returns whether the operation type has a destination {@link Stockroom}.
	 * @return {@code true} if the operation type has a destination stockroom; otherwise, {@code false}.
	 */
	Boolean getHasDestination();

	/**
	 * Sets whether the operation type has a destination {@link Stockroom}.
	 * @param hasDestination Whether the operation type has a destination stockroom.
	 */
	void setHasDestination(Boolean hasDestination);

	/**
	 * Returns whether the operation type has an associated {@link org.openmrs.Patient}.
	 * @return {@code true} if the operation type has an associated patient; otherwise, {@code false}.
	 */
	Boolean getHasPatient();

	/**
	 * Sets whether the operation type has an associated {@link org.openmrs.Patient}.
	 * @param hasPatient Whether the operation type has an associated patient.
	 */
	void setHasPatient(Boolean hasPatient);

	/**
	 * Returns whether the operation type requires an associated {@link org.openmrs.Patient}.
	 * @return {@code true} if the operation type requires and associated patient; otherwise, {@code false}.
	 */
	Boolean getPatientRequired();

	/**
	 * Sets whether the operation type requires an associated {@link org.openmrs.Patient}.
	 * @param required Whether the operation type requires an associated patient.
	 */
	void setPatientRequired(Boolean required);

	/**
	 * Returns whether the operation items are available while reserved.
	 * @return {@code true} if the operation items are available; otherwise, {@code false}.
	 */
	Boolean getAvailableWhenReserved();

	/**
	 * Sets whether the operation items are available while reserved.
	 * @param available Whether the operation items are available while reserved.
	 */
	void setAvailableWhenReserved(Boolean available);

	/**
	 * Returns the {@link User} required to approve operations of this type.
	 * @return The user required to approve the operation.
	 */
	User getUser();

	/**
	 * Sets the {@link User} required to approve operations of this type.
	 * @param user The user required to approve the operation.
	 */
	void setUser(User user);

	/**
	 * Returns the user {@link Role} for users that are required to approve operations of this type.
	 * @return The role required to approve the operation.
	 */
	Role getRole();

	/**
	 * Sets the user {@link Role} for users that can approve operations of this type.
 	 * @param role The role required to approve the operation.
	 */
	void setRole(Role role);

	/**
	 * Called when the {@link StockOperation} status is initially created and the status is StockOperationStatus.PENDING.
	 * @param operation The associated stock operation.
	 */
	void onPending(StockOperation operation);

	/**
	 * Called when the {@link StockOperation} status is changed to StockOperationStatus.CANCELLED.
	 * @param operation The associated stock operation.
	 */
	void onCancelled(StockOperation operation);

	/**
	 * Called when the {@link StockOperation} status is changed to StockOperationStatus.COMPLETED.
	 * @param operation The associated stock operation.
	 */
	void onCompleted(StockOperation operation);
}
