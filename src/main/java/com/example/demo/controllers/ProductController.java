package com.example.demo.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.timgroup.statsd.StatsDClient;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/product")
public class ProductController {
	
	private static final Logger LOG = LogManager.getLogger(ProductController.class);
	@Autowired
	private StatsDClient statsDClient;


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
            
			LOG.error("###########__PRODUCT_ID_NOT_FOUND_(GET./V1/Product/{ProductID})__ #############");
			return ResponseEntity.notFound().build();
			
		}
		
		statsDClient.incrementCounter("counts_api_call_GET.v1/product/{ProductID}");
		LOG.info("###########___PRODUCT_FOUND__#############");
		return new ResponseEntity<CloudComputingDBProduct>(product, HttpStatus.OK);
	}

	@GetMapping("{product_id}/image")
	public ResponseEntity<List<CloudComputingDBImage>> getImages(@PathVariable("product_id") Long id) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			LOG.error("###########__AUTHENTICATION_FAILURE_(GET.{ProductID/Image})__#############");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Object principal = auth.getPrincipal();
		CloudComputingDBUser dbUser = null;
		CloudComputingDBProduct dbProduct = null;

		if (principal instanceof UserDetails) {

			String username = ((UserDetails) principal).getUsername();
			dbUser = cloudComputingUserService.getUser(username);

			if (dbUser == null) {
				LOG.error("###########__USER_NOT_FOUND__(GET.{ProductID/Image})#############");
				return ResponseEntity.notFound().build();
			}

			dbProduct = cloudComputingProductService.getProduct(id);

			if (dbProduct == null) {
				LOG.error("###########__PRODUCT_NOT_FOUND__(GET.{ProductID/Image})#############");
				return ResponseEntity.notFound().build();
			}

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {
				LOG.error("###########__FORBIDDEN_USER__(GET.{ProductID/Image})#############");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}

		if (dbUser == null || dbProduct == null) {
			LOG.error("###########__USER/ PRODUCT_DOES_NOT_EXIST__(GET.{ProductID/Image})#############");
			return ResponseEntity.badRequest().build();
		}

		List<CloudComputingDBImage> images = cloudComputingProductService.getProduct(id).getImages();

		if (images == null || images.size() == 0) {
			LOG.error("###########__IMAGE_NOT_FOUND__(GET.{ProductID/Image})#############");
			return ResponseEntity.notFound().build();
		}
		statsDClient.incrementCounter("counts_api_call_GET.v1/product/{ProductID}/Image");
		LOG.info("###########__GETTING_ALL_IMAGES__#############");
		return new ResponseEntity<List<CloudComputingDBImage>>(images, HttpStatus.OK);
	}

	@GetMapping("{product_id}/image/{image_id}")
	public ResponseEntity<CloudComputingDBImage> getImage(@PathVariable("product_id") Long product_id,
			@PathVariable("image_id") Long image_id) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			LOG.error("###########__AUTHENTICATION_FAILURE_(ProductID/Image/{ImageID})__#############");
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
				LOG.error("###########__USER_NOT_FOUND_(ProductID/Image/{ImageID})#############");
				return ResponseEntity.notFound().build();
			}

			dbProduct = cloudComputingProductService.getProduct(product_id);

			if (dbProduct == null) {
				LOG.error("###########__PRODUCT_NOT_FOUND_(ProductID/Image/{ImageID})__#############");
				return ResponseEntity.notFound().build();
			}

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {
				LOG.error("###########__FORBIDDEN_USER_(ProductID/Image/{ImageID})__#############");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}
		
		if(dbUser == null || dbProduct == null) {
			LOG.error("###########__USER/PRODUCT_DOES_NOT_EXIST_(ProductID/Image/{ImageID})__#############");
			return ResponseEntity.badRequest().build();
		}
		
		dbImage = cloudComputingImageService.getImage(image_id);
		
		if(dbImage == null) {
			LOG.error("###########__IMAGE_DOES_NOT_EXIST_(ProductID/Image/{ImageID})__#############");
			return ResponseEntity.noContent().build();
		}
		
		if(!dbImage.getProduct().getProduct_id().equals(dbProduct.getProduct_id())) {
			LOG.error("###########__INCORRECT_PRODUCT_(ProductID/Image/{ImageID})__#############");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		LOG.info("###########__IMAGE_FOUND_(ProductID/Image/{ImageID})__#############");
		statsDClient.incrementCounter("counts_api_call_GET.v1/product/{ProductID}/Image/{ImageID}");
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
					LOG.error("###########_USER_DOESNT_EXIST_(POST.v1/Product)__#############");
					return ResponseEntity.badRequest().build();
				}

				if ((form.getQuantity()) < 0) {
					LOG.error("###########__UNACCEPTABLE_QUANTITY_(POST.v1/Product)__#############");
					return ResponseEntity.badRequest().build();
				}

				if (cloudComputingProductService.fetchBySku(form.getSku()) != null) {
					LOG.error("###########__INVALID_SKU_(POST.v1/Product)__#############"); 
					return ResponseEntity.badRequest().build();
				}
			} else {
				LOG.info("###########__SKU_NOT_PROVIDED_()POST.v1/Product)__#############");
				return ResponseEntity.notFound().build();
			}
		}

		CloudComputingDBProduct product = cloudComputingProductService.register(form, dbUser);
		
		statsDClient.incrementCounter("counts_api_call_POST.v1/Product");
		LOG.info("###########__PRODUCT_CREATED_#############");
		return new ResponseEntity<CloudComputingDBProduct>(product, HttpStatus.CREATED);
	}

	@PostMapping("{product_id}/image")
	public ResponseEntity<CloudComputingDBImage> uploadImage(@PathVariable("product_id") Long product_id,
			@RequestParam("file") MultipartFile multipartFile) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			LOG.error("###########__AUTHENTICATION_FAILURE_(POST.v1/Product/{ProductID}/Image)#############");
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
				LOG.error("############__USER_NOT_FOUND_(POST.v1/Product/{ProductID}/Image)__#############");
				return ResponseEntity.notFound().build();
			}

			dbProduct = cloudComputingProductService.getProduct(product_id);

			if (dbProduct == null) {
				LOG.error("###########__PRODUCT_NOT_FOUND_(POST.v1/Product/{ProductID}/Image)__#############");
				return ResponseEntity.notFound().build();
			}

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {
				LOG.error("###########__UNAUTHORIZED_USER_(POST.v1/Product/{ProductID}/Image)__#############");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		}

		if (dbUser == null || dbProduct == null) {
			LOG.error("###########__USER/PRODUCT_DOESNT_EXIST_(POST.v1/Product/{ProductID}/Image)__#############");
			return ResponseEntity.badRequest().build();
		}

		dbImage = cloudComputingImageService.register(multipartFile, dbProduct);

		if (dbImage == null) {
			LOG.error("###########__IMAGE_DOESNT_EXIST_(POST.v1/Product/{ProductID}/Image)__#############");
			return ResponseEntity.badRequest().build();
		}
		
		statsDClient.incrementCounter("counts_api_call_POST.v1/Product/{ProductID}/Image");
		LOG.info("###########__IMAGE_POSTED__#############");
		return new ResponseEntity<CloudComputingDBImage>(dbImage, HttpStatus.CREATED);
	}

	@PutMapping("{id}")
	public ResponseEntity<CloudComputingDBProduct> update(@PathVariable Long id, @RequestBody ProductForm form,
			HttpServletRequest request) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			LOG.error("###########__AUTHENTICATION_FAILURE_(PUT./v1/product/{productId})__#############");
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
				LOG.error("###########__USER/PRODUCT_NOT_FOUND_(PUT./v1/product/{productId})__#############");
				return ResponseEntity.notFound().build();
			}

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {
				LOG.error("###########__FORBIDDEN_USER_(PUT./v1/product/{productId})__#############");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			if (dbProduct.getName().equals("") || dbProduct.getDescription().equals("")
					|| dbProduct.getManufacturer().equals("") || dbProduct.getSku().equals("")
					|| dbProduct.getQuantity() == 0) {
				LOG.error("###########__EMPTY_RECORDS_(PUT./v1/product/{productId})__#############");
				return ResponseEntity.badRequest().build();
			}

			if ((form.getQuantity()) < 0) {
				LOG.error("###########__INVALID_QUANTITY_(PUT./v1/product/{productId})__#############");
				return ResponseEntity.badRequest().build();
			}

			if (cloudComputingProductService.fetchBySku(form.getSku()) != null) {
				LOG.error("###########__INVALID_SKU_(PUT./v1/product/{productId})__#############");
				return ResponseEntity.badRequest().build();
			}
		} else {
			LOG.error("###########__SKU_NOT_FOUND_(PUT./v1/product/{productId})__#############");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		dbProduct = cloudComputingProductService.updateProduct(form, dbProduct);
		
		statsDClient.incrementCounter("counts_api_call_(PUT./v1/product/{productId})");
		LOG.info("###########__UPDATED_PRODUCT__#############");
		return new ResponseEntity<CloudComputingDBProduct>(dbProduct, HttpStatus.OK);
	}

	@PatchMapping("{id}")
	public ResponseEntity<CloudComputingDBProduct> update1(@PathVariable Long id, @RequestBody ProductForm form,
			HttpServletRequest request) {
		System.out.println("INSIDE");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			LOG.error("###########__AUTHENTICATION_FAILURE_(PATCH./v1/product/{productId})__#############");
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
				LOG.error("###########__USER/PRODUCT_NOT_FOUND_(PATCH./v1/product/{productId})__#############");
				return ResponseEntity.notFound().build();
			}

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {
				LOG.error("###########__FORBIDDEN_USER_(PATCH./v1/product/{productId})__#############");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			if (form.getQuantity() != 0 && (form.getQuantity()) < 0) {
				LOG.error("###########__INVALID_QUANTITY_(PATCH./v1/product/{productId})__#############");
				return ResponseEntity.badRequest().build();
			}

			if (form.getSku() != null && cloudComputingProductService.fetchBySku(form.getSku()) != null) {
				LOG.error("###########__INVALID_SKU_(PATCH./v1/product/{productId})__#############");
				return ResponseEntity.badRequest().build();
			}
		} else {
			LOG.error("###########__SKU_NOT_FOUND_(PATCH./v1/product/{productId})__#############");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		dbProduct = cloudComputingProductService.updateProduct(form, dbProduct);
		statsDClient.incrementCounter("counts_api_call_getv1/user/userid/product/");
		LOG.info("###########__PRODUCT_UPDATED__#############");
		return new ResponseEntity<CloudComputingDBProduct>(dbProduct, HttpStatus.OK);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {

		System.out.println("INSIDE");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			LOG.error("###########__AUTHENTICATION_FAILURE_(DELETE./v1/product/{productId})__#############");
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
				LOG.error("###########__USER/PRODUCT_NULL_(DELETE./v1/product/{productId})__#############");
				return ResponseEntity.notFound().build();
			}

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {
				LOG.error("###########__FORBIDDEN_USER_(DELETE./v1/product/{productId})__#############");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		} else {
			LOG.error("###########__UNAUTHORIZED_USER_(DELETE./v1/product/{productId})__#############");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		statsDClient.incrementCounter("counts_api_call_(DELETE./v1/product/{productId})__");
		LOG.info("###########DELETED PRODUCT#############");
		return new ResponseEntity<String>(cloudComputingProductService.delete(id), HttpStatus.NO_CONTENT);
	}

	@DeleteMapping("{product_id}/image/{image_id}")
	public ResponseEntity<String> deleteImage(@PathVariable("product_id") Long product_id,
			@PathVariable("image_id") Long image_id) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			LOG.error("###########__AUTHENTICATION_FAILURE_(DELETE./v1/product/{productId}/image/{image_id})__#############");
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
				LOG.error("###########__USER_NOT_FOUND_(DELETE./v1/product/{productId}/image/{image_id})__#############");
				return ResponseEntity.notFound().build();
			}

			dbProduct = cloudComputingProductService.getProduct(product_id);

			if (dbProduct == null) {
				LOG.error("###########__PRODUCT_NOT_FOUND_(DELETE./v1/product/{productId}/image/{image_id})__#############");
				return ResponseEntity.notFound().build();
			}

			if (!dbProduct.getOwner().getUser_id().equals(dbUser.getUser_id())) {
				LOG.error("###########__FORBIDDEN_USER_(DELETE./v1/product/{productId}/image/{image_id})__#############");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}
		
		if(dbUser == null || dbProduct == null) {
			LOG.error("###########__USER/PRODUCT_NULL_(DELETE./v1/product/{productId}/image/{image_id})__#############");
			return ResponseEntity.badRequest().build();
		}
		
		dbImage = cloudComputingImageService.getImage(image_id);
		
		if(dbImage == null) {
			LOG.error("###########__IMAGE NOT FOUND_(DELETE./v1/product/{productId}/image/{image_id})__#############");
			return ResponseEntity.notFound().build();
		}
		
		if(!dbImage.getProduct().getProduct_id().equals(dbProduct.getProduct_id())) {
			LOG.error("###########__FORBIDDEN_USER_(DELETE./v1/product/{productId}/image/{image_id})__#############");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		cloudComputingImageService.delete(image_id);
		statsDClient.incrementCounter("counts_api_call_(DELETE./v1/product/{productId}/image/{image_id})");
		LOG.info("###########__DELETED_IMAGE__#############");
		return new ResponseEntity<String>("Image deleted", HttpStatus.OK);
	}
}
