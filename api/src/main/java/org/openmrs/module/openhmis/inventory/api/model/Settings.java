package org.openmrs.module.openhmis.inventory.api.model;

public class Settings {
	public static final long serialVersionUID = 1L;

	private Boolean autoGenerateOperationNumber;
	private Boolean autoCompleteOperations;
	private Integer operationNumberGeneratorSourceId;
	private Integer stockTakeReportId;
	private Integer stockCardReportId;
	private Integer stockOperationsByStockroomReportId;
	private Integer stockroomReportId;
	private Integer expiringStockReportId;

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
}
