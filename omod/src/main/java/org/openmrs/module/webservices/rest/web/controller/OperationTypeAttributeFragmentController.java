package org.openmrs.module.webservices.rest.web.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.openhmis.commons.api.entity.model.IInstanceAttributeType;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationTypeDataService;
import org.openmrs.module.openhmis.inventory.api.model.IStockOperationType;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTypeBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = AttributeFragmentControllerBase.REQUEST_MAPPING_PATH)
public class OperationTypeAttributeFragmentController extends AttributeFragmentControllerBase {
	private IStockOperationTypeDataService service;

	@Autowired
	public OperationTypeAttributeFragmentController(IStockOperationTypeDataService service) {
		this.service = service;
	}

	@Override
	protected List<? extends IInstanceAttributeType<?>> getAttributeTypes(HttpServletRequest request) {
		String uuid = request.getParameter("uuid");
		if (StringUtils.isEmpty(uuid)) {
			throw new IllegalArgumentException("The uuid for the operation type must be defined.");
		}

		IStockOperationType type = service.getByUuid(uuid);
		if (type == null) {
			throw new IllegalArgumentException("Could not find operation type '" + uuid + "'.");
		}

		return type.getAttributeTypes();
	}
}
