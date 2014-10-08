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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.commons.api.exception.PrivilegeException;
import org.openmrs.module.openhmis.inventory.api.IDepartmentDataService;
import org.openmrs.module.openhmis.inventory.api.model.Department;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = ModuleRestConstants.DEPARTMENT_RESOURCE, supportedClass=Department.class,
		supportedOpenmrsVersions={"1.9.*", "1.10.*"})
@Handler(supports = { Department.class }, order = 0)
public class DepartmentResource extends BaseRestMetadataResource<Department> {

    private static final Log LOG = LogFactory.getLog(DepartmentResource.class);

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = super.getRepresentationDescription(rep);
        description.addProperty("description", Representation.REF);

        return description;
    }

	@Override
	public Department newDelegate() {
		return new Department();
	}

	@Override
	public Class<? extends IMetadataDataService<Department>> getServiceClass() {
		return IDepartmentDataService.class;
	}

    @Override
    public void purge(Department department, RequestContext context) {
        try {
            super.purge(department, context);
        } catch (PrivilegeException ce) {
        	LOG.error("Exception occured when trying to purge item <" + department.getName() + ">", ce);
        	throw new PrivilegeException("Can't purge department with name <" +  department.getName() + "> as required privilege is missing");
        } catch(APIException e) {
            LOG.error("Exception occured when trying to purge department <" + department.getName() + ">", e);
            throw new ResponseException("Can't purge department with name <" +  department.getName() + "> as it is still in use") {
                private static final long serialVersionUID = 1L;
            };
        }
    }
}


