package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.module.idgen.IdentifierSource;

public class Settings {
	public static final long serialVersionUID = 0L;

	private Boolean autoGenerateOperationNumber;
	private Integer operationNumberGeneratorSourceId;

	public Boolean getAutoGenerateOperationNumber() {
		return autoGenerateOperationNumber;
	}

	public void setAutoGenerateOperationNumber(Boolean autoGenerateOperationNumber) {
		this.autoGenerateOperationNumber = autoGenerateOperationNumber;
	}

	public Integer getOperationNumberGeneratorSourceId() {
		return operationNumberGeneratorSourceId;
	}

	public void setOperationNumberGeneratorSourceId(Integer sourceId) {
		this.operationNumberGeneratorSourceId = sourceId;
	}
}
