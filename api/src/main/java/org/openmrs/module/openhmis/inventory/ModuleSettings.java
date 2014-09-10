package org.openmrs.module.openhmis.inventory;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;

public class ModuleSettings {
	public static final String AUTO_GENERATE_OPERATION_NUMBER_PROPERTY = "openhmis.inventory.autoGenerateOperationNumber";
	public static final String OPERATION_NUMBER_IDENTIFIER_SOURCE_ID_PROPERTY =
			"openhmis.inventory.operationNumberIdentifierSourceId";

	public static boolean generateOperationNumber() {
		AdministrationService administrationService = Context.getAdministrationService();
		String property = administrationService.getGlobalProperty(AUTO_GENERATE_OPERATION_NUMBER_PROPERTY);

		return Boolean.parseBoolean(property);
	}

	protected ModuleSettings() {}
}
