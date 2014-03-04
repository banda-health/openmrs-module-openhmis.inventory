/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.ICategoryDataService;
import org.openmrs.module.openhmis.inventory.api.model.Category;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = ModuleRestConstants.CATEGORY_RESOURCE, supportedClass=Category.class, supportedOpenmrsVersions={"1.9"})
@Handler(supports = { Category.class }, order = 0)
public class CategoryResource extends BaseRestMetadataResource<Category> {

    private static Log LOG = LogFactory.getLog(CategoryResource.class);

	@Override
	public Category newDelegate() {
		return new Category();
	}

	@Override
	public Class<? extends IMetadataDataService<Category>> getServiceClass() {
		return ICategoryDataService.class;
	}

	@Override
	public Category getByUniqueId(String uniqueId) {
		return StringUtils.isEmpty(uniqueId) ? null : super.getByUniqueId(uniqueId);
	}

    @Override
    public void purge(Category category, RequestContext context) throws ResponseException {
        try {
            super.purge(category, context);
        } catch(Exception e) {
            LOG.error("Exception occured when trying to purge category <" + category.getName() + ">", e);
            throw new ResponseException("Can't purge category with name <" +  category.getName() + "> as it is still in use") {
                private static final long serialVersionUID = 1L;
            };
        }
    }
}

