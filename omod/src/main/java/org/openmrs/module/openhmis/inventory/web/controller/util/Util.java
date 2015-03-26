package org.openmrs.module.openhmis.inventory.web.controller.util;

import org.openmrs.User;
import org.openmrs.module.openhmis.inventory.api.WellKnownOperationTypes;
import org.openmrs.module.openhmis.inventory.api.model.IStockOperationType;

public class Util {

	public static boolean userCanPerformAdjustmentOperation(User user) {
		IStockOperationType adjustment = WellKnownOperationTypes.getAdjustment();
		return adjustment.userCanProcess(user);
	}
}
