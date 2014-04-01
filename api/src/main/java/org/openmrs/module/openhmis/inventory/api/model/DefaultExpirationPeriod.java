package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.module.openhmis.commons.api.entity.model.BaseSerializableOpenmrsMetadata;

public class DefaultExpirationPeriod extends BaseSerializableOpenmrsMetadata {
    public static final long serialVersionUID = 0L;

    Integer defaultExpirationPeriodId;
    Integer timeValue;
    TimePeriod timePeriod;

    @Override
    public Integer getId() {
        return defaultExpirationPeriodId;
    }

    @Override
    public void setId(Integer id) {
        defaultExpirationPeriodId = id;
    }

    public Integer getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(Integer timeValue) {
        this.timeValue = timeValue;
    }

    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

}
