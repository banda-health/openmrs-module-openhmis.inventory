/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.api.model.IStockOperationType;

/**
 * This class provides a standard way to get the various system-defined {@link IStockOperationType}s.
 */
public final class WellKnownOperationTypes {
	public static final String ADJUSTMENT_UUID = "288fd7fe-1374-4f7a-89e6-d5f1ac97d4a5";
	public static final String DISPOSED_UUID = "84be0aaf-70cf-4ebb-83e3-088e5d375905";
	public static final String DISTRIBUTION_UUID = "c264f34b-c795-4576-9928-454d1fa20e09";
	public static final String INITIAL_UUID = "20f3734f-5b1a-490d-8676-1225a9cdddf7";
	public static final String RECEIPT_UUID = "fce0b4fc-9402-424a-aacb-f99599e51a9f";
	public static final String RETURN_UUID = "128924d7-72ee-414e-ae40-52f1f89d3e7d";
	public static final String TRANSFER_UUID = "db40707f-9175-4199-8df2-a5702f41ec7d";

	// Make this class static
	private WellKnownOperationTypes() {}

	public static IStockOperationType getAdjustment() {
		return getOperationType(ADJUSTMENT_UUID);
	}

	public static IStockOperationType getDisposed() {
		return getOperationType(DISPOSED_UUID);
	}

	public static IStockOperationType getDistribution() {
		return getOperationType(DISTRIBUTION_UUID);
	}

	public static IStockOperationType getReceipt() {
		return getOperationType(RECEIPT_UUID);
	}

	public static IStockOperationType getReturn() {
		return getOperationType(RETURN_UUID);
	}

	public static IStockOperationType getTransfer() {
		return getOperationType(TRANSFER_UUID);
	}

	public static IStockOperationType getInitial() {
		return getOperationType(INITIAL_UUID);
	}

	private static IStockOperationType getOperationType(String uuid) {
		IStockOperationTypeDataService service = Context.getService(IStockOperationTypeDataService.class);

		return service.getByUuid(uuid);
	}
}
