package org.openmrs.module.openhmis.inventory.uiframwork;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.ui.framework.StandardModuleUiConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenmrsProfile(modules = { "uiframework:*.*" })
public class UiConfigurationInventory {
	
	@Bean
	public StandardModuleUiConfiguration createUiConfigurationBean() {
			StandardModuleUiConfiguration standardModuleUiConfiguration = new StandardModuleUiConfiguration();
			standardModuleUiConfiguration.setModuleId("openhmis.inventory");
			return standardModuleUiConfiguration;
	}
}
