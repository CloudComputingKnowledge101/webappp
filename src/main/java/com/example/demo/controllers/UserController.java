package com.example.demo.controllers;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.daos.SignupForm;
import com.example.demo.models.CloudComputingDBUser;
import com.example.demo.services.CloudComputingUserService;

@RestController
@RequestMapping("/v1/user")

public class UserController {

	private String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

	@Autowired
	private CloudComputingUserService cloudComputingUserService;

	@GetMapping("{id}")
	public ResponseEntity<CloudComputingDBUser> read(@PathVariable Long id) {

		System.out.println("INSIDE");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Object principal = auth.getPrincipal();
		CloudComputingDBUser dbUser = null;

		if (principal instanceof UserDetails) {

			String username = ((UserDetails) principal).getUsername();
			dbUser = cloudComputingUserService.getUser(username);

			if (dbUser == null) {

				return ResponseEntity.notFound().build();

			} else if (!dbUser.getUser_id().equals(id)) {

				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		} else {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		return new ResponseEntity<CloudComputingDBUser>(dbUser, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<CloudComputingDBUser> create(@RequestBody final SignupForm form) {

		if (form.getFirst_name().equals("") || form.getLast_name().equals("") || form.getPassword().equals("")
				|| form.getUsername().equals("")) {

			return ResponseEntity.badRequest().build();
		}

		Pattern pattern = Pattern.compile(regex);
		boolean match = pattern.matcher(form.getUsername()).matches();

		if (!match || cloudComputingUserService.getUser(form.getUsername()) != null) {

			return ResponseEntity.badRequest().build();
		}

		CloudComputingDBUser user = cloudComputingUserService.register(form);

		return new ResponseEntity<CloudComputingDBUser>(user, HttpStatus.CREATED);
	}

	@PutMapping("{id}")
	public ResponseEntity<CloudComputingDBUser> update(@PathVariable Long id, @RequestBody final SignupForm user) {

		System.out.println("INSIDE");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Object principal = auth.getPrincipal();
		CloudComputingDBUser dbUser = null;

		if (principal instanceof UserDetails) {

			String username = ((UserDetails) principal).getUsername();
			dbUser = cloudComputingUserService.getUser(username);

			if (dbUser == null) {

				return ResponseEntity.notFound().build();

			} else if (!dbUser.getUser_id().equals(id)) {

				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		} else {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		dbUser = cloudComputingUserService.updateUser(user, dbUser);

		return new ResponseEntity<CloudComputingDBUser>(dbUser, HttpStatus.OK);

	}
}
