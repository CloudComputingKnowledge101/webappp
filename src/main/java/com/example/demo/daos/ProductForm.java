package com.example.demo.daos;

import java.io.Serializable;

public class ProductForm implements Serializable {
	
	private String name;

	private String description;

	private String sku;

	private String manufacturer;

	private int quantity;
	
	public ProductForm() {
		
	}
	
	public ProductForm(String name, String description, String sku, String manufacturer, int quantity) {
		super();
		this.name = name;
		this.description = description;
		this.sku = sku;
		this.manufacturer = manufacturer;
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
