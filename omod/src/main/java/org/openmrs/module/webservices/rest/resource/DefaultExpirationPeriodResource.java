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

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.IDefaultExpirationPeriodDataService;
import org.openmrs.module.openhmis.inventory.api.model.DefaultExpirationPeriod;
import org.openmrs.module.openhmis.inventory.api.model.TimePeriod;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

@Resource(name= ModuleRestConstants.DEFAULT_EXPIRATION_PERIOD_RESOURCE, supportedClass=DefaultExpirationPeriod.class, supportedOpenmrsVersions={"1.9"})
public class DefaultExpirationPeriodResource extends BaseRestMetadataResource<DefaultExpirationPeriod> implements IMetadataDataServiceResource<DefaultExpirationPeriod> {

    @PropertySetter(value = "timeValue")
    public void setTimeValue(DefaultExpirationPeriod instance, Object timeValue) throws ConversionException {
        String timeValueString = (String) timeValue;
        if (!StringUtils.isBlank(timeValueString)) {
            instance.setTimeValue(Integer.parseInt(timeValueString));
        } else {
            instance.setTimeValue(null);
        }
    }

    @PropertySetter(value = "timePeriod")
    public void setTimePeriod(DefaultExpirationPeriod instance, Object timePeriod) throws ConversionException {
        String timePeriodString = (String) timePeriod;
        if (!StringUtils.isBlank(timePeriodString)) {
            instance.setTimePeriod(TimePeriod.valueOf(timePeriodString));
        } else {
            instance.setTimePeriod(null);
        }
    }

    @PropertyGetter(value = "timeValue")
    public String getDefaultExpirationPeriod(DefaultExpirationPeriod instance) {
        return instance.getTimeValue().toString();
    }


    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = super.getRepresentationDescription(rep);
        description.addProperty("timeValue");
        description.addProperty("timePeriod");

        return description;
    }

    @Override
    public DefaultExpirationPeriod newDelegate() {
        return new DefaultExpirationPeriod();
    }

    @Override
    public Class<? extends IMetadataDataService<DefaultExpirationPeriod>> getServiceClass() {
        return IDefaultExpirationPeriodDataService.class;
    }

}
