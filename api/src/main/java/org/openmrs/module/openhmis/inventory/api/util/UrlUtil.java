package org.openmrs.module.openhmis.inventory.api.util;

import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;

/**
 * Url Util class
 */
public class UrlUtil {
	
	/**
	 * Returns the full page url for a module page.
	 * @param relativePath The page url, relative to the module web directory.
	 * @return The full page url.
	 */
	public static String getModulePageUrl(String relativePath) {
		return ModuleWebConstants.MODULE_ROOT + relativePath;
	}
}
