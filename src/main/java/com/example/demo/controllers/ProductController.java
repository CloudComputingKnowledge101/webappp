package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.daos.ProductForm;
import com.example.demo.models.CloudComputingDBProduct;
import com.example.demo.models.CloudComputingDBUser;
import com.example.demo.services.CloudComputingProductService;
import com.example.demo.services.CloudComputingUserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/product")
public class ProductController {
	
	@Autowired
	private CloudComputingProductService cloudComputingProductService;

	@Autowired
	private CloudComputingUserService cloudComputingUserService;
	
	
	@GetMapping("{id}")
	public ResponseEntity<CloudComputingDBProduct> read(@PathVariable Long id) {

		CloudComputingDBProduct product = cloudComputingProductService.getProduct(id);
		
		if(product == null) {
			
			return ResponseEntity.notFound().build();
		}
		
		return new ResponseEntity<CloudComputingDBProduct>(product, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<CloudComputingDBProduct> create(@RequestBody final ProductForm form) {
		System.out.println("INSIDE");
		
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CloudComputingDBUser dbUser = null;
		
		if (principal instanceof UserDetails) {
			String username = ((UserDetails) principal).getUsername();

			dbUser = cloudComputingUserService.getUser(username);

			if (dbUser != null) {
				
				if(form.getName().equals("") ||
						form.getDescription().equals("") ||
						form.getManufacturer().equals("") ||
						form.getSku().equals("") ||
						form.getQuantity() == 0) {
					
					return ResponseEntity.badRequest().build();
				}
				
				if( (form.getQuantity()) < 0) {
					
					return ResponseEntity.badRequest().build();
				}
				
				if(cloudComputingProductService.fetchBySku(form.getSku()) != null) {
					
					return ResponseEntity.badRequest().build();
				}
				
				
			}else {
				
				return ResponseEntity.notFound().build();
			}
		}
		
		CloudComputingDBProduct product = cloudComputingProductService.register(form, dbUser);

		return new ResponseEntity<CloudComputingDBProduct>(product, HttpStatus.CREATED);
	}
	

	@PutMapping("{id}")
	public ResponseEntity<CloudComputingDBProduct> update(@PathVariable Long id, @RequestBody ProductForm form,
			HttpServletRequest request) {
		
		System.out.println("INSIDE");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth == null) {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		Object principal = auth.getPrincipal();
		CloudComputingDBUser dbUser = null;
		CloudComputingDBProduct dbProduct = null;
		
		if (principal instanceof UserDetails) {
			
			String username = ((UserDetails) principal).getUsername();
			dbUser = cloudComputingUserService.getUser(username);
			dbProduct = cloudComputingProductService.getProduct(id);
			
			if (dbUser == null || dbProduct == null ) {
				
				return ResponseEntity.notFound().build();
			}
			
			if( !dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id()) ) {
				
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			
			if( dbProduct.getName().equals("") ||
						dbProduct.getDescription().equals("") ||
						dbProduct.getManufacturer().equals("") ||
						dbProduct.getSku().equals("") ||
						dbProduct.getQuantity() == 0 ) {
				
				return ResponseEntity.badRequest().build();
			}
			
			if( (form.getQuantity()) < 0) {
				
				return ResponseEntity.badRequest().build();
			}
			
			if(cloudComputingProductService.fetchBySku(form.getSku()) != null) {
				
				return ResponseEntity.badRequest().build();
			}
		}else {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		dbProduct = cloudComputingProductService.updateProduct(form, dbProduct);
		
		return new ResponseEntity<CloudComputingDBProduct>(dbProduct, HttpStatus.OK);
	}
	/*@PatchMapping ("{id}")
	public ResponseEntity<CloudComputingDBProduct> update1(@PathVariable Long id, @RequestBody ProductForm form,
			HttpServletRequest request) {
		
		System.out.println("INSIDE");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth == null) {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		Object principal = auth.getPrincipal();
		CloudComputingDBUser dbUser = null;
		CloudComputingDBProduct dbProduct = null;
		
		if (principal instanceof UserDetails) {
			
			String username = ((UserDetails) principal).getUsername();
			dbUser = cloudComputingUserService.getUser(username);
			dbProduct = cloudComputingProductService.getProduct(id);
			
			if (dbUser == null || dbProduct == null ) {
				
				return ResponseEntity.notFound().build();
			}
			
			if( !dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id()) ) {
				
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			
			if( dbProduct.getName().equals("") ||
						dbProduct.getDescription().equals("") ||
						dbProduct.getManufacturer().equals("") ||
						dbProduct.getSku().equals("") ||
						dbProduct.getQuantity().equals("") ) {
				
				return ResponseEntity.badRequest().build();
			}
			
			if( Integer.parseInt(form.getQuantity()) < 0) {
				
				return ResponseEntity.badRequest().build();
			}
			
			if(cloudComputingProductService.fetchBySku(form.getSku()) != null) {
				
				return ResponseEntity.badRequest().build();
			}
		}else {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		dbProduct = cloudComputingProductService.updateProduct(form, dbProduct);
		
		return new ResponseEntity<CloudComputingDBProduct>(dbProduct, HttpStatus.OK);
	}*/
	@PatchMapping("{id}")
	public ResponseEntity<CloudComputingDBProduct> update1(@PathVariable Long id, @RequestBody ProductForm form,
	HttpServletRequest request) {
		System.out.println("INSIDE");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth == null) {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		Object principal = auth.getPrincipal();
		CloudComputingDBUser dbUser = null;
		CloudComputingDBProduct dbProduct = null;
		
		if (principal instanceof UserDetails) {
			
			String username = ((UserDetails) principal).getUsername();
			dbUser = cloudComputingUserService.getUser(username);
			dbProduct = cloudComputingProductService.getProduct(id);
			
			if (dbUser == null || dbProduct == null ) {
				
				return ResponseEntity.notFound().build();
			}
			
			if( !dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id()) ) {
				
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			
			if( form.getQuantity() != 0 && (form.getQuantity()) < 0) {
				
				return ResponseEntity.badRequest().build();
			}
			
			if(form.getSku() != null && cloudComputingProductService.fetchBySku(form.getSku()) != null) {
				
				return ResponseEntity.badRequest().build();
			}
		}else {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		dbProduct = cloudComputingProductService.updateProduct(form, dbProduct);
		
		return new ResponseEntity<CloudComputingDBProduct>(dbProduct, HttpStatus.OK);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		
		System.out.println("INSIDE");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth == null) {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		Object principal = auth.getPrincipal();
		CloudComputingDBUser dbUser = null;
		CloudComputingDBProduct dbProduct = null;
		
		if (principal instanceof UserDetails) {
			
			String username = ((UserDetails) principal).getUsername();
			dbUser = cloudComputingUserService.getUser(username);
			dbProduct = cloudComputingProductService.getProduct(id);
			
			if (dbUser == null || dbProduct == null) {
				
				return ResponseEntity.notFound().build();
			}
			
			if( !dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id()) ) {
				
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}else {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		return new ResponseEntity<String>(cloudComputingProductService.delete(id), HttpStatus.NO_CONTENT);
	}
}
