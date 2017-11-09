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
package org.openmrs.module.openhmis.inventory.web.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.jasperreport.ReportsControllerBase;
import org.openmrs.module.openhmis.inventory.ModuleSettings;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.Settings;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Controller for the Jasper report renderer.
 */
@Controller(value = "invJasperReportController")
@RequestMapping(value = ModuleWebConstants.JASPER_REPORT_PAGE)
public class JasperReportController extends ReportsControllerBase {

	@Override
	public String parse(int reportId, WebRequest request, HttpServletResponse response) throws IOException {
		Settings settings = ModuleSettings.loadSettings();
		if (settings.getStockTakeReportId() != null && reportId == settings.getStockTakeReportId()) {
			return renderStockTakeReport(reportId, request, response);
		} else if (settings.getStockCardReportId() != null && reportId == settings.getStockCardReportId()) {
			return renderStockCardReport(reportId, request, response);
		} else if (settings.getStockOperationsByStockroomReportId() != null
		        && reportId == settings.getStockOperationsByStockroomReportId()) {
			return renderStockOperationsByStockroomReport(reportId, request, response);
		} else if (settings.getStockroomReportId() != null && reportId == settings.getStockroomReportId()) {
			return renderStockroomReport(reportId, request, response);
		} else if (settings.getExpiringStockReportId() != null && reportId == settings.getExpiringStockReportId()) {
			return renderExpiringStocksReport(reportId, request, response);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unknown report.");
		}
		return null;
	}

	private String renderStockTakeReport(int reportId, WebRequest request, HttpServletResponse response) throws IOException {
		int stockroomId;
		String temp = request.getParameter("stockroomId");
		if (!StringUtils.isEmpty(temp) && StringUtils.isNumeric(temp)) {
			stockroomId = Integer.parseInt(temp);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The stockroom id ('" + temp + "') must be "
			        + "defined and be numeric.");
			return null;
		}

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("stockroomId", stockroomId);

		return renderReport(reportId, params, null, response);
	}

	private String renderStockCardReport(int reportId, WebRequest request, HttpServletResponse response) throws IOException {
		int itemId;
		String itemName;
		Date beginDate = null, endDate = null;

		String temp = request.getParameter("itemUuid");
		if (!StringUtils.isEmpty(temp)) {
			IItemDataService itemService = Context.getService(IItemDataService.class);
			Item item = itemService.getByUuid(temp);
			if (item != null) {
				itemId = item.getId();
				itemName = item.getName();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				    "No item with UUID '" + temp + "' could be found.");
				return null;
			}
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The item uuid must be defined.");
			return null;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		temp = request.getParameter("beginDate");
		if (!StringUtils.isEmpty(temp)) {
			try {
				beginDate = dateFormat.parse(temp);
			} catch (Exception ex) {
				// Whatevs... dealing with stupid checked exceptions
			}
		}

		temp = request.getParameter("endDate");
		if (!StringUtils.isEmpty(temp)) {
			try {
				endDate = dateFormat.parse(temp);
			} catch (Exception ex) {
				// Whatevs... dealing with stupid checked exceptions
			}
		}

		if (beginDate == null || endDate == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The begin and end dates must be defined.");
			return null;
		}

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("itemId", itemId);
		params.put("beginDate", beginDate);
		params.put("endDate", endDate);

		return renderReport(reportId, params, "Item Stock Card - " + itemName, response);
	}

	private String renderStockOperationsByStockroomReport(int reportId, WebRequest request, HttpServletResponse response)
	        throws IOException {
		int itemId;
		Date beginDate = null, endDate = null;
		int stockroomId;

		String temp = request.getParameter("stockroomId");
		if (!StringUtils.isEmpty(temp) && StringUtils.isNumeric(temp)) {
			stockroomId = Integer.parseInt(temp);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The stockroom id ('" + temp + "') must be "
			        + "defined and be numeric.");
			return null;
		}

		temp = request.getParameter("itemUuid");
		if (!StringUtils.isEmpty(temp)) {
			IItemDataService itemService = Context.getService(IItemDataService.class);
			Item item = itemService.getByUuid(temp);
			if (item != null) {
				itemId = item.getId();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				    "No item with UUID '" + temp + "' could be found.");
				return null;
			}
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The item uuid must be defined.");
			return null;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		temp = request.getParameter("beginDate");
		if (!StringUtils.isEmpty(temp)) {
			try {
				beginDate = dateFormat.parse(temp);
			} catch (Exception ex) {
				// Whatevs... dealing with stupid checked exceptions
			}
		}

		temp = request.getParameter("endDate");
		if (!StringUtils.isEmpty(temp)) {
			try {
				endDate = dateFormat.parse(temp);
			} catch (Exception ex) {
				// Whatevs... dealing with stupid checked exceptions
			}
		}

		if (beginDate == null || endDate == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The begin and end dates must be defined.");
			return null;
		}

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("itemId", itemId);
		params.put("beginDate", beginDate);
		params.put("endDate", endDate);
		params.put("stockroomId", stockroomId);

		return renderReport(reportId, params, null, response);
	}

	private String renderStockroomReport(int reportId, WebRequest request, HttpServletResponse response) throws IOException {
		int stockroomId;
		Date beginDate = null, endDate = null;

		String temp = request.getParameter("stockroomId");
		if (!StringUtils.isEmpty(temp) && StringUtils.isNumeric(temp)) {
			stockroomId = Integer.parseInt(temp);
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The stockroom id ('" + temp + "') must be "
			        + "defined and be numeric.");
			return null;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		temp = request.getParameter("beginDate");
		if (!StringUtils.isEmpty(temp)) {
			try {
				beginDate = dateFormat.parse(temp);
			} catch (Exception ex) {
				// Whatevs... dealing with stupid checked exceptions
			}
		}

		temp = request.getParameter("endDate");
		if (!StringUtils.isEmpty(temp)) {
			try {
				endDate = dateFormat.parse(temp);
			} catch (Exception ex) {
				// Whatevs... dealing with stupid checked exceptions
			}
		}

		if (beginDate == null || endDate == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The begin and end dates must be defined.");
			return null;
		}

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("stockroomId", stockroomId);
		params.put("beginDate", beginDate);
		params.put("endDate", endDate);

		return renderReport(reportId, params, null, response);
	}

	private String renderExpiringStocksReport(int reportId, WebRequest request, HttpServletResponse response)
	        throws IOException {
		Date expiryDate = null;

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		String temp = request.getParameter("expiresBy");
		if (!StringUtils.isEmpty(temp)) {
			try {
				expiryDate = dateFormat.parse(temp);
			} catch (Exception ex) {
				// Whatevs... dealing with stupid checked exceptions
			}
		}

		if (expiryDate == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The expiry date must be defined.");
			return null;
		}
		String stockroomId = request.getParameter("stockroomId");

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("expiresBy", expiryDate);
		if (StringUtils.isNotBlank(stockroomId)) {
			params.put("stockroomId", stockroomId);

		}

		return renderReport(reportId, params, null, response);
	}
}
