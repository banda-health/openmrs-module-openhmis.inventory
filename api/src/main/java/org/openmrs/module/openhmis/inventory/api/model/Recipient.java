package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.Patient;
import org.openmrs.module.openhmis.commons.api.entity.model.BaseSerializableOpenmrsMetadata;

public class Recipient extends BaseSerializableOpenmrsMetadata {
	public static final long serialVersionUID = 0L;

	private Integer recipientId;
	private Patient patient;
	private Institution institution;

	@Override
	public Integer getId() {
		return recipientId;
	}

	@Override
	public void setId(Integer id) {
		recipientId = id;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}

	public int getPatientId() {
		return patient.getId();
	}

	public String getPatientUuid() {
		return patient.getUuid();
	}

	public int getInstitutionId() {
		return institution.getId();
	}

	public String getInstitutionUuid() {
		return institution.getUuid();
	}

	public boolean isPatient() {
		return patient != null;
	}

	public boolean isInstitution() {
		return institution != null;
	}

}
