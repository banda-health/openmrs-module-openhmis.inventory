/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransactionType;

public class WellKnownTransactionTypes {
	public static final String INITIAL_UUID = "2f2e072a-9c72-41d8-b1d0-ab50c01b6e4d";
	public static final String INTAKE_UUID = "8eb783b4-260b-43fd-ab83-2d3c413bfa56";
	public static final String TRANSFER_UUID = "b61cabfb-93e9-4cab-97a2-3b682cdb8c96";
	public static final String DISTRIBUTION_UUID = "09bdbc0c-306d-484e-8a0e-61811152a1d8";
	public static final String CORRECTION_UUID = "b20c2a24-29e4-405a-9e56-054351cbf5f7";
	public static final String EXPIRATION_UUID = "c4f258a9-8ed2-4faf-b4ab-e3894c5d1c60";

	// Make this class static
	private WellKnownTransactionTypes() {}

	public static StockRoomTransactionType getInitial() {
		return getTransactionType(INITIAL_UUID);
	}

	public static StockRoomTransactionType getIntake() {
		return getTransactionType(INTAKE_UUID);
	}

	public static StockRoomTransactionType getTransfer() {
		return getTransactionType(TRANSFER_UUID);
	}

	public static StockRoomTransactionType getDistribution() {
		return getTransactionType(DISTRIBUTION_UUID);
	}

	public static StockRoomTransactionType getCorrection() {
		return getTransactionType(CORRECTION_UUID);
	}

	public static StockRoomTransactionType getExpiration() {
		return getTransactionType(EXPIRATION_UUID);
	}

	private static StockRoomTransactionType getTransactionType(String uuid) {
		IStockRoomTransactionTypeDataService service = Context.getService(IStockRoomTransactionTypeDataService.class);

		return service.getByUuid(uuid);
	}
}
