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
package org.openmrs.module.openhmis.inventory.uiframwork;

import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The OpenMRS UI Framework configuration settings.
 */
public class UiConfigurationInventory implements BeanFactoryPostProcessor {

	private static Log log = LogFactory.getLog(UiConfigurationInventory.class);

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		log.info("Register openhmis.inventory module");
		try {
			// load UiFramework's StandardModuleUiConfiguration class
			Class cls = OpenmrsClassLoader.getInstance()
					.loadClass("org.openmrs.ui.framework.StandardModuleUiConfiguration");

			// get spring's bean definition builder
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(cls);

			// set the module Id
			builder.addPropertyValue("moduleId", "openhmis.inventory");

			// register bean
			((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(
					"openhmisInventoryStandardModuleUiConfiguration", builder.getBeanDefinition());
		} catch (ClassNotFoundException ex) {
			// StandardModuleUiConfiguration class not found!
			log.error("ERROR registering openhmis.inventory module::::" + ex.getMessage());
		}
	}
}
