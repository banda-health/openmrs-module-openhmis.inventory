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
package org.openmrs.module.webservices.rest.helper;

import java.math.BigDecimal;

import org.openmrs.module.webservices.rest.web.response.ConversionException;

/**
 * Helper class for data-type conversions.
 */
public class Converter {
	protected Converter() {}

	public static BigDecimal objectToBigDecimal(Object number) {
		if (Double.class.isAssignableFrom(number.getClass())) {
			return BigDecimal.valueOf((Double)number);
		} else if (Integer.class.isAssignableFrom(number.getClass())) {
			return BigDecimal.valueOf((Integer)number);
		} else {
			throw new ConversionException("Can't convert given number to " + BigDecimal.class.getSimpleName());
		}
	}

	public static Integer objectToInteger(Object number) {
		if (Integer.class.isAssignableFrom(number.getClass())) {
			return Integer.valueOf((Integer)number);
		} else {
			throw new ConversionException("Can't convert given number to " + Integer.class.getSimpleName());
		}
	}
}
