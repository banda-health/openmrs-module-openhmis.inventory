package org.openmrs.module.openhmis.inventory.api.model;

public class Settings {
	public static final long serialVersionUID = 0L;

	private Boolean autoGenerateOperationNumber;
	private Integer operationNumberGeneratorSourceId;
	private Integer stockTakeReportId;
	private Integer stockCardReportId;

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
}
