package org.openmrs.module.webservices.rest.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.openhmis.commons.api.util.IdgenUtil;
import org.openmrs.module.openhmis.commons.api.util.ModuleUtil;
import org.openmrs.module.openhmis.inventory.ModuleSettings;

/**
 * Helper class for ID number generation.
 */
public class IdgenHelper {

	private static final Log LOG = LogFactory.getLog(IdgenHelper.class);

	public static boolean isOperationNumberGenerated() {
		return ModuleUtil.isLoaded(ModuleUtil.IDGEN_MODULE_ID) && ModuleSettings.generateOperationNumber();
	}

	public static String generateId() {
		try {
			String generatedId = IdgenUtil.generateId(ModuleSettings.OPERATION_NUMBER_IDENTIFIER_SOURCE_ID_PROPERTY);
			return generatedId;
		} catch (Exception ex) {
			LOG.error("Could not generate operation number: " + ex.getMessage());
			throw new IllegalStateException("The Operation Number was not defined and could not be generated.", ex);
		}
	}
}
