package com.example.demo.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Product")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class CloudComputingDBProduct implements Serializable {

	@Id
	@Column(name = "product_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long product_id;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "sku", unique = true)
	private String sku;

	@Column(name = "manufacturer")
	private String manufacturer;

	@Column(name = "quantity")
	private int quantity;

	@Column(name = "date_added")
	private String date_added;

	@Column(name = "date_last_updated")
	private String date_last_updated;

	@ManyToOne
	//@JsonBackReference
	@JoinColumn(name = "owner", referencedColumnName = "user_id", nullable=false)
	private CloudComputingDBUser owner;

	public CloudComputingDBProduct() {
	}

	public CloudComputingDBProduct(Long product_id, String name, String description, String sku, String manufacturer,
			int quantity, String date_added, String date_last_updated, CloudComputingDBUser owner) {
		super();
		this.product_id = product_id;
		this.name = name;
		this.description = description;
		this.sku = sku;
		this.manufacturer = manufacturer;
		this.quantity = quantity;
		this.date_added = date_added;
		this.date_last_updated = date_last_updated;
		this.owner = owner;
	}

	public Long getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Long product_id) {
		this.product_id = product_id;
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

	public String getDate_added() {
		return date_added;
	}

	public void setDate_added(String date_added) {
		this.date_added = date_added;
	}

	public String getDate_last_updated() {
		return date_last_updated;
	}

	public void setDate_last_updated(String date_last_updated) {
		this.date_last_updated = date_last_updated;
	}

	public CloudComputingDBUser getOwner() {
		return owner;
	}

	public void setOwner(CloudComputingDBUser owner) {
		this.owner = owner;
	}
}