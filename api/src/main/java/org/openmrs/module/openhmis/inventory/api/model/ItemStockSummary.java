package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.OpenmrsObject;

import java.util.Date;

public class ItemStockSummary implements OpenmrsObject {
	public static final long serialVersionUID = 0L;

	private Item item;
	private Date expiration;
	private Long quantity;
	private Long actualQuantity;

	// These are aggregate models and thus have no id or uuid.
	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public void setId(Integer id) {

	}

	@Override
	public String getUuid() {
		return null;
	}

	@Override
	public void setUuid(String uuid) {

	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Long getActualQuantity() {
		return actualQuantity;
	}

	public void setActualQuantity(Long actualQuantity) {
		this.actualQuantity = actualQuantity;
	}
}
