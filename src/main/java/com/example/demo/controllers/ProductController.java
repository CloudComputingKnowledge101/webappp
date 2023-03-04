package com.example.demo.controllers;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.daos.ProductForm;
import com.example.demo.models.CloudComputingDBImage;
import com.example.demo.models.CloudComputingDBProduct;
import com.example.demo.models.CloudComputingDBUser;
import com.example.demo.services.CloudComputingImageService;
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

	@Autowired
	private CloudComputingImageService cloudComputingImageService;

	@GetMapping("{id}")
	public ResponseEntity<CloudComputingDBProduct> read(@PathVariable Long id) {

		CloudComputingDBProduct product = cloudComputingProductService.getProduct(id);

		if (product == null) {

			return ResponseEntity.notFound().build();
		}

		return new ResponseEntity<CloudComputingDBProduct>(product, HttpStatus.OK);
	}

	@GetMapping("{product_id}/image")
	public ResponseEntity<List<CloudComputingDBImage>> getImages(@PathVariable("product_id") Long id) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Object principal = auth.getPrincipal();
		CloudComputingDBUser dbUser = null;
		CloudComputingDBProduct dbProduct = null;

		if (principal instanceof UserDetails) {

			String username = ((UserDetails) principal).getUsername();
			dbUser = cloudComputingUserService.getUser(username);

			if (dbUser == null) {
				return ResponseEntity.notFound().build();
			}

			dbProduct = cloudComputingProductService.getProduct(id);

			if (dbProduct == null) {
				return ResponseEntity.notFound().build();
			}

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}

		if (dbUser == null || dbProduct == null) {
			return ResponseEntity.badRequest().build();
		}

		List<CloudComputingDBImage> images = cloudComputingProductService.getProduct(id).getImages();

		if (images == null || images.size() == 0) {

			return ResponseEntity.notFound().build();
		}

		return new ResponseEntity<List<CloudComputingDBImage>>(images, HttpStatus.OK);
	}

	@GetMapping("{product_id}/image/{image_id}")
	public ResponseEntity<CloudComputingDBImage> getImage(@PathVariable("product_id") Long product_id,
			@PathVariable("image_id") Long image_id) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Object principal = auth.getPrincipal();
		CloudComputingDBUser dbUser = null;
		CloudComputingDBProduct dbProduct = null;
		CloudComputingDBImage dbImage = null;
		
		if (principal instanceof UserDetails) {
			
			String username = ((UserDetails) principal).getUsername();
			dbUser = cloudComputingUserService.getUser(username);

			if (dbUser == null) {
				return ResponseEntity.notFound().build();
			}

			dbProduct = cloudComputingProductService.getProduct(product_id);

			if (dbProduct == null) {
				return ResponseEntity.notFound().build();
			}

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}
		
		if(dbUser == null || dbProduct == null) {
			
			return ResponseEntity.badRequest().build();
		}
		
		dbImage = cloudComputingImageService.getImage(image_id);
		
		if(dbImage == null) {
			
			return ResponseEntity.noContent().build();
		}
		
		if(!dbImage.getProduct().getProduct_id().equals(dbProduct.getProduct_id())) {
			
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		return new ResponseEntity<CloudComputingDBImage>(dbImage, HttpStatus.OK);
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

				if (form.getName() == null || form.getDescription() == null || form.getManufacturer() == null
						|| form.getSku() == null || form.getQuantity() <= 0) {

					return ResponseEntity.badRequest().build();
				}

				if ((form.getQuantity()) < 0) {

					return ResponseEntity.badRequest().build();
				}

				if (cloudComputingProductService.fetchBySku(form.getSku()) != null) {

					return ResponseEntity.badRequest().build();
				}
			} else {

				return ResponseEntity.notFound().build();
			}
		}

		CloudComputingDBProduct product = cloudComputingProductService.register(form, dbUser);

		return new ResponseEntity<CloudComputingDBProduct>(product, HttpStatus.CREATED);
	}

	@PostMapping("{product_id}/image")
	public ResponseEntity<CloudComputingDBImage> uploadImage(@PathVariable("product_id") Long product_id,
			@RequestParam("file") MultipartFile multipartFile) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Object principal = auth.getPrincipal();
		CloudComputingDBUser dbUser = null;
		CloudComputingDBProduct dbProduct = null;
		CloudComputingDBImage dbImage = null;

		if (principal instanceof UserDetails) {

			String username = ((UserDetails) principal).getUsername();
			dbUser = cloudComputingUserService.getUser(username);

			if (dbUser == null) {
				return ResponseEntity.notFound().build();
			}

			dbProduct = cloudComputingProductService.getProduct(product_id);

			if (dbProduct == null) {
				return ResponseEntity.notFound().build();
			}

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		}

		if (dbUser == null || dbProduct == null) {
			return ResponseEntity.badRequest().build();
		}

		dbImage = cloudComputingImageService.register(multipartFile, dbProduct);

		if (dbImage == null) {
			return ResponseEntity.badRequest().build();
		}

		return new ResponseEntity<CloudComputingDBImage>(dbImage, HttpStatus.CREATED);
	}

	@PutMapping("{id}")
	public ResponseEntity<CloudComputingDBProduct> update(@PathVariable Long id, @RequestBody ProductForm form,
			HttpServletRequest request) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {

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

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {

				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			if (dbProduct.getName().equals("") || dbProduct.getDescription().equals("")
					|| dbProduct.getManufacturer().equals("") || dbProduct.getSku().equals("")
					|| dbProduct.getQuantity() == 0) {

				return ResponseEntity.badRequest().build();
			}

			if ((form.getQuantity()) < 0) {

				return ResponseEntity.badRequest().build();
			}

			if (cloudComputingProductService.fetchBySku(form.getSku()) != null) {

				return ResponseEntity.badRequest().build();
			}
		} else {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		dbProduct = cloudComputingProductService.updateProduct(form, dbProduct);

		return new ResponseEntity<CloudComputingDBProduct>(dbProduct, HttpStatus.OK);
	}

	@PatchMapping("{id}")
	public ResponseEntity<CloudComputingDBProduct> update1(@PathVariable Long id, @RequestBody ProductForm form,
			HttpServletRequest request) {
		System.out.println("INSIDE");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {

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

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {

				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			if (form.getQuantity() != 0 && (form.getQuantity()) < 0) {

				return ResponseEntity.badRequest().build();
			}

			if (form.getSku() != null && cloudComputingProductService.fetchBySku(form.getSku()) != null) {

				return ResponseEntity.badRequest().build();
			}
		} else {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		dbProduct = cloudComputingProductService.updateProduct(form, dbProduct);

		return new ResponseEntity<CloudComputingDBProduct>(dbProduct, HttpStatus.OK);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {

		System.out.println("INSIDE");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {

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

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {

				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		} else {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		return new ResponseEntity<String>(cloudComputingProductService.delete(id), HttpStatus.NO_CONTENT);
	}

	@DeleteMapping("{product_id}/image/{image_id}")
	public ResponseEntity<String> deleteImage(@PathVariable("product_id") Long product_id,
			@PathVariable("image_id") Long image_id) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Object principal = auth.getPrincipal();
		CloudComputingDBUser dbUser = null;
		CloudComputingDBProduct dbProduct = null;
		CloudComputingDBImage dbImage = null;
		
		if (principal instanceof UserDetails) {
			
			String username = ((UserDetails) principal).getUsername();
			dbUser = cloudComputingUserService.getUser(username);

			if (dbUser == null) {
				return ResponseEntity.notFound().build();
			}

			dbProduct = cloudComputingProductService.getProduct(product_id);

			if (dbProduct == null) {
				return ResponseEntity.notFound().build();
			}

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}
		
		if(dbUser == null || dbProduct == null) {
			
			return ResponseEntity.badRequest().build();
		}
		
		dbImage = cloudComputingImageService.getImage(image_id);
		
		if(dbImage == null) {
			
			return ResponseEntity.notFound().build();
		}
		
		if(!dbImage.getProduct().getProduct_id().equals(dbProduct.getProduct_id())) {
			
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		cloudComputingImageService.delete(image_id);
		
		return new ResponseEntity<String>("Image deleted", HttpStatus.OK);
	}
}
