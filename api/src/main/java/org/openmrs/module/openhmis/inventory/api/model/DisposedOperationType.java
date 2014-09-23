package org.openmrs.module.openhmis.inventory.api.model;

/**
 * A disposed operation is for expired items that must be removed from circulation.  While it could technically be
 * implemented as a form of an adjustment operation, it is notable enough (for reporting) to warrant it's own type.
 */
public class DisposedOperationType extends AdjustmentOperationType {
	@Override
	protected boolean negateAppliedQuantity() {
		return true;
	}
}
