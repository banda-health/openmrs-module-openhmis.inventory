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
package org.openmrs.module.openhmis.inventory.api.model;

/**
 * Model class that represents the configurable inventory module settings
 */
public class Settings {
	public static final long serialVersionUID = 1L;

	private Boolean autoGenerateOperationNumber;
	private Boolean autoCompleteOperations;
	private Boolean wildcardItemSearch;
	private Integer operationNumberGeneratorSourceId;
	private Integer stockTakeReportId;
	private Integer stockCardReportId;
	private Integer stockOperationsByStockroomReportId;
	private Integer stockroomReportId;
	private Integer expiringStockReportId;
	private Boolean autoSelectItemStockFurthestExpirationDate;

	public Boolean getAutoGenerateOperationNumber() {
		return autoGenerateOperationNumber;
	}

	public void setAutoGenerateOperationNumber(Boolean autoGenerateOperationNumber) {
		this.autoGenerateOperationNumber = autoGenerateOperationNumber;
	}

	public Boolean getAutoCompleteOperations() {
		return autoCompleteOperations;
	}

	public void setAutoCompleteOperations(Boolean autoCompleteOperations) {
		this.autoCompleteOperations = autoCompleteOperations;
	}

	public Boolean getWildcardItemSearch() {
		return wildcardItemSearch;
	}

	public void setWildcardItemSearch(Boolean wildcardItemSearch) {
		this.wildcardItemSearch = wildcardItemSearch;
	}

	public Integer getOperationNumberGeneratorSourceId() {
		return operationNumberGeneratorSourceId;
	}

	public void setOperationNumberGeneratorSourceId(Integer sourceId) {
		this.operationNumberGeneratorSourceId = sourceId;
	}

	public Integer getStockTakeReportId() {
		return stockTakeReportId;
	}

	public void setStockTakeReportId(Integer stockTakeReportId) {
		this.stockTakeReportId = stockTakeReportId;
	}

	public Integer getStockCardReportId() {
		return stockCardReportId;
	}

	public void setStockCardReportId(Integer stockCardReportId) {
		this.stockCardReportId = stockCardReportId;
	}

	public Integer getStockOperationsByStockroomReportId() {
		return stockOperationsByStockroomReportId;
	}

	public void setStockOperationsByStockroomReportId(Integer stockOperationsByStockroomReportId) {
		this.stockOperationsByStockroomReportId = stockOperationsByStockroomReportId;
	}

	public Integer getStockroomReportId() {
		return stockroomReportId;
	}

	public void setStockroomReportId(Integer stockroomReportId) {
		this.stockroomReportId = stockroomReportId;
	}

	public Integer getExpiringStockReportId() {
		return expiringStockReportId;
	}

	public void setExpiringStockReportId(Integer expiringStockReportId) {
		this.expiringStockReportId = expiringStockReportId;
	}

	public Boolean getAutoSelectItemStockFurthestExpirationDate() {
		return autoSelectItemStockFurthestExpirationDate;
	}

	public void setAutoSelectItemStockFurthestExpirationDate(Boolean autoSelectItemStockFurthestExpirationDate) {
		this.autoSelectItemStockFurthestExpirationDate = autoSelectItemStockFurthestExpirationDate;
	}
}
