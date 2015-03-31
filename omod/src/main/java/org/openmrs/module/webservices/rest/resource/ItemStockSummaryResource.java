package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockSummary;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = ModuleRestConstants.INVENTORY_STOCK_TAKE_SUMMARY_RESOURCE, supportedClass = ItemStockSummary.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*" })
public class ItemStockSummaryResource extends DelegatingCrudResource<ItemStockSummary> {
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("item", Representation.DEFAULT);
		description.addProperty("expiration", Representation.DEFAULT);
		description.addProperty("quantity", Representation.DEFAULT);
		description.addProperty("actualQuantity", Representation.DEFAULT);
		
		return description;
	}
	
	@PropertySetter("expiration")
	public void setExpiration(ItemStockSummary instance, String dateText) {
		System.out.println("aaaaaaaaaaaaaa");
	}
	
	@Override
	public ItemStockSummary newDelegate() {
		return new ItemStockSummary();
	}

	@Override
	protected PageableResult doSearch(RequestContext context) {
		return super.doSearch(context);
	}

	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return super.doGetAll(context);
	}

	@Override
	public ItemStockSummary save(ItemStockSummary delegate) {
		return null;
	}

	@Override
	public ItemStockSummary getByUniqueId(String uniqueId) {
		return null;
	}

	@Override
	protected void delete(ItemStockSummary delegate, String reason, RequestContext context) throws ResponseException {
		// Deletes not supported
	}

	@Override
	public void purge(ItemStockSummary delegate, RequestContext context) throws ResponseException {
		// Purges not supported
	}
}
