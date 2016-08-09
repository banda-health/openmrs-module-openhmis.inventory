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
