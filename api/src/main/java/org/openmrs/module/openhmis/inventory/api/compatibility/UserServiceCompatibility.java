package org.openmrs.module.openhmis.inventory.api.compatibility;

import org.openmrs.User;
import org.openmrs.api.APIException;

/**
 * Compatibility class for working with the user service save user
 */
public interface UserServiceCompatibility {

	User saveUser(User user, String password) throws APIException;
}
