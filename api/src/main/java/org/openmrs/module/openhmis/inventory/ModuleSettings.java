package org.openmrs.module.openhmis.inventory;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.openhmis.commons.api.util.IdgenUtil;
import org.openmrs.module.openhmis.inventory.api.model.Settings;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;

public class ModuleSettings {
	public static final String AUTO_GENERATE_OPERATION_NUMBER_PROPERTY = "openhmis.inventory.autoGenerateOperationNumber";
	public static final String OPERATION_NUMBER_IDENTIFIER_SOURCE_ID_PROPERTY =
			"openhmis.inventory.operationNumberIdentifierSourceId";

	public static boolean generateOperationNumber() {
		AdministrationService adminService = Context.getAdministrationService();
		String property = adminService.getGlobalProperty(AUTO_GENERATE_OPERATION_NUMBER_PROPERTY);

		return Boolean.parseBoolean(property);
	}

	public static Settings loadSettings() {
		Settings settings = new Settings();

		settings.setAutoGenerateOperationNumber(generateOperationNumber());
		if (settings.getAutoGenerateOperationNumber()) {
			IdentifierSource source = IdgenUtil.getIdentifierSource(OPERATION_NUMBER_IDENTIFIER_SOURCE_ID_PROPERTY);
			if (source != null) {
				settings.setOperationNumberGeneratorSourceId(source.getId());
			}
		}

		return settings;
	}

	public static void saveSettings(Settings settings) {
		if (settings == null) {
			throw new IllegalArgumentException("The settings to save must be defined.");
		}

		AdministrationService adminService = Context.getAdministrationService();
		Boolean generate = settings.getAutoGenerateOperationNumber();
		if (Boolean.TRUE.equals(generate)) {
			adminService.setGlobalProperty(AUTO_GENERATE_OPERATION_NUMBER_PROPERTY, Boolean.TRUE.toString());
		} else {
			adminService.setGlobalProperty(AUTO_GENERATE_OPERATION_NUMBER_PROPERTY, Boolean.FALSE.toString());
		}

		Integer sourceId = settings.getOperationNumberGeneratorSourceId();
		if (sourceId != null) {
			adminService.setGlobalProperty(OPERATION_NUMBER_IDENTIFIER_SOURCE_ID_PROPERTY, sourceId.toString());
		} else {
			adminService.setGlobalProperty(OPERATION_NUMBER_IDENTIFIER_SOURCE_ID_PROPERTY, "");
		}
	}

	protected ModuleSettings() {}
}
