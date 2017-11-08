package org.openmrs.module.openhmis.inventory.api.compatibility;

import org.openmrs.User;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;

/**
 * Compatibility class for working with the user service save user
 */
@OpenmrsProfile(openmrsPlatformVersion = "1.9.9 - 1.12.*")
public class UserServiceCompatibilityV1o9 implements UserServiceCompatibility {

	public User saveUser(User user, String password) throws APIException {
		return Context.getUserService().saveUser(user, password);
	}
}
