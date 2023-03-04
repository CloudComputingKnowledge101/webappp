package com.example.demo.models;

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
@Table(name = "Image")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class CloudComputingDBImage {
	
	@Id
	@Column(name = "image_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long image_id;

	@Column(name = "filename")
	private String filename;

	@Column(name = "date_created")
	private String date_created;

	@Column(name = "s3_bucket_path", unique = true)
	private String s3_bucket_path;

	@ManyToOne
	@JoinColumn(name = "product", referencedColumnName = "product_id", nullable=false)
	private CloudComputingDBProduct product;
	
	public CloudComputingDBImage() {}
	
	public CloudComputingDBImage(Long image_id, String filename, String date_created, String s3_bucket_path,
			CloudComputingDBProduct product) {
		super();
		this.image_id = image_id;
		this.filename = filename;
		this.date_created = date_created;
		this.s3_bucket_path = s3_bucket_path;
		this.product = product;
	}

	public Long getImage_id() {
		return image_id;
	}

	public void setImage_id(Long image_id) {
		this.image_id = image_id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getDate_created() {
		return date_created;
	}

	public void setDate_created(String date_created) {
		this.date_created = date_created;
	}

	public String getS3_bucket_path() {
		return s3_bucket_path;
	}

	public void setS3_bucket_path(String s3_bucket_path) {
		this.s3_bucket_path = s3_bucket_path;
	}

	public CloudComputingDBProduct getProduct() {
		return product;
	}

	public void setProduct(CloudComputingDBProduct product) {
		this.product = product;
	}
}
