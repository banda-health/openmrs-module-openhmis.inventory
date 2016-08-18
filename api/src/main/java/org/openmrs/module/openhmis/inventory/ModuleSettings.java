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
package org.openmrs.module.openhmis.inventory;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.openhmis.commons.api.util.IdgenUtil;
import org.openmrs.module.openhmis.inventory.api.model.Settings;

/**
 * Helper class to load and save the inventory module global settings.
 */
public class ModuleSettings {
	public static final String AUTO_GENERATE_OPERATION_NUMBER_PROPERTY = "openhmis.inventory.autoGenerateOperationNumber";
	public static final String OPERATION_NUMBER_IDENTIFIER_SOURCE_ID_PROPERTY =
	        "openhmis.inventory.operationNumberIdentifierSourceId";
	public static final String STOCK_TAKE_REPORT_ID_PROPERTY = "openhmis.inventory.reports.stockTake";
	public static final String STOCK_CARD_REPORT_ID_PROPERTY = "openhmis.inventory.reports.stockCard";
	public static final String STOCKROOM_REPORT_ID_PROPERTY = "openhmis.inventory.reports.stockroom";
	public static final String EXPIRING_STOCK_REPORT_ID_PROPERTY = "openhmis.inventory.reports.expiringStock";
	public static final String AUTO_COMPLETE_OPERATIONS_PROPERTY = "openhmis.inventory.autoCompleteOperations";
	public static final String SHOW_OPERATATION_CANCEL_REASEON_FIELD = "openhmis.inventory.showOperationCancelReason";
	public static final String USE_WILDCARD_ITEM_SEARCH_PROPERTY = "openhmis.inventory.useWildcardItemSearch";
	public static final String RESTRICT_NEGATIVE_INVENTORY_STOCK_CREATION_FIELD =
	        "openhmis.inventory.restrictNegativeInventoryStockCreation";
	public static final String AUTO_SELECT_ITEM_STOCK_FURTHEST_EXPIRATION_DATE =
	        "openhmis.inventory.autoSelectItemStockWithFurthestExpiration";
	private static final String STOCK_OPERATIONS_BY_STOCKROOM_REPORT_ID_PROPERTY =
	        "openhmis.inventory.reports.stockOperationsByStockroom";

	public static boolean generateOperationNumber() {
		return generateOperationNumber(Context.getAdministrationService());
	}

	protected static boolean generateOperationNumber(AdministrationService adminService) {
		String property = adminService.getGlobalProperty(AUTO_GENERATE_OPERATION_NUMBER_PROPERTY);
		return Boolean.parseBoolean(property);
	}

	public static boolean isOperationAutoCompleted() {
		AdministrationService adminService = Context.getAdministrationService();
		String property = adminService.getGlobalProperty(AUTO_COMPLETE_OPERATIONS_PROPERTY);
		return Boolean.parseBoolean(property);
	}

	public static boolean showOperationCancelReasonField() {
		AdministrationService adminService = Context.getAdministrationService();
		String property = adminService.getGlobalProperty(SHOW_OPERATATION_CANCEL_REASEON_FIELD);
		return Boolean.parseBoolean(property);
	}

	public static boolean isNegativeStockRestricted() {
		AdministrationService adminService = Context.getAdministrationService();
		String property = adminService.getGlobalProperty(RESTRICT_NEGATIVE_INVENTORY_STOCK_CREATION_FIELD);
		return Boolean.parseBoolean(property);
	}

	public static boolean autoSelectItemStockWithFurthestExpirationDate() {
		AdministrationService adminService = Context.getAdministrationService();
		String property = adminService.getGlobalProperty(AUTO_SELECT_ITEM_STOCK_FURTHEST_EXPIRATION_DATE);
		return Boolean.parseBoolean(property);
	}

	public static boolean useWildcardItemSearch() {
		AdministrationService adminService = Context.getAdministrationService();
		String property = adminService.getGlobalProperty(USE_WILDCARD_ITEM_SEARCH_PROPERTY);
		return Boolean.parseBoolean(property);
	}

	public static Settings loadSettings() {
		Settings settings = new Settings();
		AdministrationService adminService = Context.getAdministrationService();

		settings.setAutoGenerateOperationNumber(generateOperationNumber(adminService));
		if (settings.getAutoGenerateOperationNumber()) {
			IdentifierSource source = IdgenUtil.getIdentifierSource(OPERATION_NUMBER_IDENTIFIER_SOURCE_ID_PROPERTY);
			if (source != null) {
				settings.setOperationNumberGeneratorSourceId(source.getId());
			}
		}

		String prop = adminService.getGlobalProperty(STOCK_TAKE_REPORT_ID_PROPERTY);
		if (StringUtils.isNotEmpty(prop)) {
			settings.setStockTakeReportId(Integer.parseInt(prop));
		}

		prop = adminService.getGlobalProperty(STOCK_CARD_REPORT_ID_PROPERTY);
		if (StringUtils.isNotEmpty(prop)) {
			settings.setStockCardReportId(Integer.parseInt(prop));
		}

		prop = adminService.getGlobalProperty(STOCK_OPERATIONS_BY_STOCKROOM_REPORT_ID_PROPERTY);
		if (StringUtils.isNotEmpty(prop)) {
			settings.setStockOperationsByStockroomReportId(Integer.parseInt(prop));
		}

		prop = adminService.getGlobalProperty(STOCKROOM_REPORT_ID_PROPERTY);
		if (StringUtils.isNotEmpty(prop)) {
			settings.setStockroomReportId(Integer.parseInt(prop));
		}

		prop = adminService.getGlobalProperty(EXPIRING_STOCK_REPORT_ID_PROPERTY);
		if (StringUtils.isNotEmpty(prop)) {
			settings.setExpiringStockReportId(Integer.parseInt(prop));
		}

		prop = adminService.getGlobalProperty(STOCK_OPERATIONS_BY_STOCKROOM_REPORT_ID_PROPERTY);
		if (StringUtils.isNotEmpty(prop)) {
			settings.setStockOperationsByStockroomReportId(Integer.parseInt(prop));
		}

		prop = adminService.getGlobalProperty(AUTO_COMPLETE_OPERATIONS_PROPERTY);
		if (StringUtils.isNotEmpty(prop)) {
			settings.setAutoCompleteOperations(Boolean.parseBoolean(prop));
		} else {
			settings.setAutoCompleteOperations(false);
		}

		prop = adminService.getGlobalProperty(AUTO_SELECT_ITEM_STOCK_FURTHEST_EXPIRATION_DATE);
		if (!StringUtils.isEmpty(prop)) {
			settings.setAutoSelectItemStockFurthestExpirationDate(Boolean.parseBoolean(prop));
		} else {
			settings.setAutoSelectItemStockFurthestExpirationDate(false);
		}

		prop = adminService.getGlobalProperty(USE_WILDCARD_ITEM_SEARCH_PROPERTY);
		if (StringUtils.isNotEmpty(prop)) {
			settings.setWildcardItemSearch(Boolean.parseBoolean(prop));
		} else {
			settings.setWildcardItemSearch(false);
		}

		return settings;
	}

	public static void saveSettings(Settings settings) {
		if (settings == null) {
			throw new IllegalArgumentException("The settings to save must be defined.");
		}

		AdministrationService adminService = Context.getAdministrationService();
		Boolean generate = settings.getAutoGenerateOperationNumber();
		if (Boolean.TRUE.equals(generate)) {
			adminService.setGlobalProperty(AUTO_GENERATE_OPERATION_NUMBER_PROPERTY, Boolean.TRUE.toString());
		} else {
			adminService.setGlobalProperty(AUTO_GENERATE_OPERATION_NUMBER_PROPERTY, Boolean.FALSE.toString());
		}

		Integer sourceId = settings.getOperationNumberGeneratorSourceId();
		if (sourceId != null) {
			adminService.setGlobalProperty(OPERATION_NUMBER_IDENTIFIER_SOURCE_ID_PROPERTY, sourceId.toString());
		} else {
			adminService.setGlobalProperty(OPERATION_NUMBER_IDENTIFIER_SOURCE_ID_PROPERTY, "");
		}

		Integer reportId = settings.getStockTakeReportId();
		if (reportId != null) {
			adminService.setGlobalProperty(STOCK_TAKE_REPORT_ID_PROPERTY, reportId.toString());
		} else {
			adminService.setGlobalProperty(STOCK_TAKE_REPORT_ID_PROPERTY, "");
		}

		reportId = settings.getStockCardReportId();
		if (reportId != null) {
			adminService.setGlobalProperty(STOCK_CARD_REPORT_ID_PROPERTY, reportId.toString());
		} else {
			adminService.setGlobalProperty(STOCK_CARD_REPORT_ID_PROPERTY, "");
		}

		reportId = settings.getStockroomReportId();
		if (reportId != null) {
			adminService.setGlobalProperty(STOCKROOM_REPORT_ID_PROPERTY, reportId.toString());
		} else {
			adminService.setGlobalProperty(STOCKROOM_REPORT_ID_PROPERTY, "");
		}

		reportId = settings.getExpiringStockReportId();
		if (reportId != null) {
			adminService.setGlobalProperty(EXPIRING_STOCK_REPORT_ID_PROPERTY, reportId.toString());
		} else {
			adminService.setGlobalProperty(EXPIRING_STOCK_REPORT_ID_PROPERTY, "");
		}

		reportId = settings.getStockOperationsByStockroomReportId();
		if (reportId != null) {
			adminService.setGlobalProperty(STOCK_OPERATIONS_BY_STOCKROOM_REPORT_ID_PROPERTY, reportId.toString());
		} else {
			adminService.setGlobalProperty(STOCK_OPERATIONS_BY_STOCKROOM_REPORT_ID_PROPERTY, "");
		}

		Boolean autoComplete = settings.getAutoCompleteOperations();
		if (Boolean.TRUE.equals(autoComplete)) {
			adminService.setGlobalProperty(AUTO_COMPLETE_OPERATIONS_PROPERTY, Boolean.TRUE.toString());
		} else {
			adminService.setGlobalProperty(AUTO_COMPLETE_OPERATIONS_PROPERTY, Boolean.FALSE.toString());
		}

		Boolean autoSelectItemStockFurthestExpirationDate = settings.getAutoSelectItemStockFurthestExpirationDate();
		if (Boolean.TRUE.equals(autoSelectItemStockFurthestExpirationDate)) {
			adminService.setGlobalProperty(AUTO_SELECT_ITEM_STOCK_FURTHEST_EXPIRATION_DATE, Boolean.TRUE.toString());
		} else {
			adminService.setGlobalProperty(AUTO_SELECT_ITEM_STOCK_FURTHEST_EXPIRATION_DATE, Boolean.FALSE.toString());
		}

		Boolean wildcardItemSearch = settings.getWildcardItemSearch();
		if (Boolean.TRUE.equals(wildcardItemSearch)) {
			adminService.setGlobalProperty(USE_WILDCARD_ITEM_SEARCH_PROPERTY, Boolean.TRUE.toString());
		} else {
			adminService.setGlobalProperty(USE_WILDCARD_ITEM_SEARCH_PROPERTY, Boolean.FALSE.toString());
		}
	}

	protected ModuleSettings() {}
}
