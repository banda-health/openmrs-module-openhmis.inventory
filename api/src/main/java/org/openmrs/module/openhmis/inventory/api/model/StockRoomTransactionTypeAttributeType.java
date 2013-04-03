package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.module.openhmis.commons.api.entity.model.BaseInstanceAttributeType;

public class StockRoomTransactionTypeAttributeType extends BaseInstanceAttributeType<StockRoomTransactionType> {
	public static final long serialVersionUID = 0L;

	private User user;
	private Role role;
	private Patient patient;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
}
