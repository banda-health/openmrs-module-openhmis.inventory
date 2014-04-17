package org.openmrs.module.webservices.rest.helper;

import java.math.BigDecimal;

import org.openmrs.module.webservices.rest.web.response.ConversionException;

public class Converter {

    public static BigDecimal objectToBigDecimal(Object number) throws ConversionException {
        if (Double.class.isAssignableFrom(number.getClass()))
            return BigDecimal.valueOf((Double) number);
        else if (Integer.class.isAssignableFrom(number.getClass()))
            return BigDecimal.valueOf((Integer) number);
        else
            throw new ConversionException("Can't convert given number to " + BigDecimal.class.getSimpleName());
    }

    public static Integer objectToInteger(Object number) throws ConversionException {
        if (Integer.class.isAssignableFrom(number.getClass()))
            return Integer.valueOf((Integer) number);
        else
            throw new ConversionException("Can't convert given number to " + Integer.class.getSimpleName());
    }

}
