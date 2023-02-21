package com.example.demo.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.daos.ProductForm;
import com.example.demo.models.CloudComputingDBProduct;
import com.example.demo.models.CloudComputingDBUser;
import com.example.demo.repositories.ProductRepository;

@Service
public class CloudComputingProductService {

	@Autowired
	private ProductRepository repository;
	
	public CloudComputingDBProduct register(ProductForm form, CloudComputingDBUser user) {

		System.out.println("Mapping user details database object of product....");

		CloudComputingDBProduct product = new CloudComputingDBProduct();
		product.setName(form.getName());
		product.setDescription(form.getDescription());
		product.setQuantity(form.getQuantity());
		product.setSku(form.getSku());
		product.setManufacturer(form.getManufacturer());
		product.setOwner(user);

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

		product.setDate_added(timeStamp);
		product.setDate_last_updated(timeStamp);

		System.out.println("Database object created of product ....");
		
		repository.saveAndFlush(product);
		// authRepository.saveAndFlush( authority ) ;

		return product;
	}

	public CloudComputingDBProduct getProduct(Long id) {
		
		Optional<CloudComputingDBProduct> dbProduct = repository.findById(id);
		
		if(!dbProduct.isPresent()) {
			
			return null;
		}
		
		return dbProduct.get();
	}

	public CloudComputingDBProduct updateProduct(ProductForm product, CloudComputingDBProduct existing) {

		if (!product.getName().equals(existing.getName())) {

			existing.setName(product.getName());
		}

		if (!product.getDescription().equals(existing.getDescription())) {

			existing.setDescription(product.getDescription());
		}
		
		if (product.getQuantity() != (existing.getQuantity())) {

			existing.setQuantity(product.getQuantity());
		}

		if (!product.getSku().equals(existing.getSku())) {

			existing.setSku(product.getSku());
		}
		
		if (!product.getManufacturer().equals(existing.getManufacturer())) {

			existing.setManufacturer(product.getManufacturer());
		}
		
		String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
		existing.setDate_last_updated(timeStamp);

		return repository.saveAndFlush(existing);
	}
	
	public String delete(Long id) {
		
		repository.deleteById(id);
		
		return "Product deleted";
	}
	
	public CloudComputingDBProduct fetchBySku(String sku) {
		
		return repository.findBySku(sku);
	}
}
