package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.model.DefaultExpirationPeriod;
import org.openmrs.module.openhmis.inventory.api.model.ItemCode;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

@Resource(name= ModuleRestConstants.DEFAULT_EXPIRATION_PERIOD_RESOURCE, supportedClass=ItemCode.class, supportedOpenmrsVersions={"1.9"})
public class DefaultExpirationPeriodResource extends BaseRestMetadataResource<DefaultExpirationPeriod> implements IMetadataDataServiceResource<DefaultExpirationPeriod> {

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
        return null;
    }

}
