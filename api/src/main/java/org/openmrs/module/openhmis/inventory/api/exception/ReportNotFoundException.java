package org.openmrs.module.openhmis.inventory.api.exception;

import org.openmrs.api.APIException;

public class ReportNotFoundException extends APIException {
	
	public static final long serialVersionUID = 22323L;
	
	public ReportNotFoundException() {
		super();
	}
	
	public ReportNotFoundException(String message) {
		super(message);
	}
	
	public ReportNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public ReportNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
