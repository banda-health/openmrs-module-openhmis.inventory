package org.openmrs.module.openhmis.inventory.web.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.jasperreport.JasperReport;
import org.openmrs.module.jasperreport.JasperReportService;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Type of a RestController to check up setting values in the Inventory Module Settings.
 */
@Controller(value = "moduleSettings")
@RequestMapping(ModuleWebConstants.MODULE_SETTINGS_ROOT)
public class ModuleSettingsController {
	private AdministrationService adminService;
	private JasperReportService reportService;
	private static final Log LOG = LogFactory.getLog(ModuleSettingsController.class);

	@Autowired
	public ModuleSettingsController(AdministrationService adminService) {
		this.adminService = adminService;
	}

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public SimpleObject get(
	        @RequestParam(value = "setting", required = false) String setting,
	        @RequestParam(value = "report", required = false) String report) {
		SimpleObject results = new SimpleObject();
		try {
			if (StringUtils.isNotEmpty(setting)) {
				results.put("results", this.adminService.getGlobalProperty(setting));
			} else if (StringUtils.isNotEmpty(report)) {
				if (this.reportService == null) {
					this.reportService = Context.getService(JasperReportService.class);
				}
				String reportId = this.adminService.getGlobalProperty(report);
				JasperReport jasperReport = this.reportService.getJasperReport(Integer.valueOf(reportId));
				results.put("reportId", jasperReport.getReportId());
				results.put("reportName", jasperReport.getName());
				results.put("reportDescription", jasperReport.getDescription());
			}
		} catch (Exception e) {
			results.put("error", "Error retrieving setting/report");
			LOG.error("Error retrieving setting/report ", e);
		}

		return results;
	}
}
