package org.openmrs.module.openhmis.inventory.api.compatibility;

import org.openmrs.User;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;

/**
 * Compatibility class for working with the user service save user
 */
@OpenmrsProfile(openmrsPlatformVersion = "2.*")
public class UserServiceCompatibilityV2o0 implements UserServiceCompatibility {

	public User saveUser(User user, String password) throws APIException {
		return Context.getUserService().saveUser(user);
	}
}
