package com.example.demo.controllers;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
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

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/user")
public class UserController {

	private String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

	@Autowired
	private CloudComputingUserService cloudComputingUserService;

	@GetMapping("{id}")
	public ResponseEntity<String> read(@PathVariable Long id, HttpServletRequest request) {

		if (request.getHeader("Authorization") == null || !request.getHeader("Authorization").startsWith("Basic ")) {

			return new ResponseEntity<String>("You need to login first!", HttpStatus.UNAUTHORIZED);
		}

		String encodedValue = request.getHeader("Authorization").replace("Basic ", "");
		String decodedValue = new String(Base64Utils.decodeFromString(encodedValue));

		String username = decodedValue.substring(0, decodedValue.lastIndexOf(":"));
		CloudComputingDBUser user = cloudComputingUserService.getUser(username);

		if (user == null || !user.getId().equals(id)) {

			return new ResponseEntity<String>("You CAN'T GET details of other users!", HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<String>(user.toString(), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<String> create(@RequestBody final SignupForm form) {

		Pattern pattern = Pattern.compile(regex);
		boolean match = pattern.matcher(form.getUsername()).matches();

		if (!match) {

			return new ResponseEntity<String>("Invalid username", HttpStatus.BAD_REQUEST);
		}

		String user = cloudComputingUserService.register(form).toString();

		return new ResponseEntity<String>(user, HttpStatus.CREATED);
	}

	@PutMapping("{id}")
	public ResponseEntity<String> update(@RequestBody CloudComputingDBUser user, @PathVariable Long id,
			HttpServletRequest request) {

		if (request.getHeader("Authorization") == null || !request.getHeader("Authorization").startsWith("Basic ")) {

			return new ResponseEntity<String>("You need to login first!", HttpStatus.UNAUTHORIZED);
		}

		String encodedValue = request.getHeader("Authorization").replace("Basic ", "");
		String decodedValue = new String(Base64Utils.decodeFromString(encodedValue));

		String username = decodedValue.substring(0, decodedValue.lastIndexOf(":"));
		CloudComputingDBUser ccUser = cloudComputingUserService.getUser(username);

		if (ccUser == null || !ccUser.getId().equals(id)) {

			return new ResponseEntity<String>("You CAN'T UPDATE details of other users!", HttpStatus.FORBIDDEN);
		}

		if (user == null || (user.getFirst_name().equals("") && user.getLast_name().equals("")
				&& user.getPassword().equals("") && user.getUsername().equals(""))) {

			return new ResponseEntity<String>("You need to provide atleast 1 field!", HttpStatus.NO_CONTENT);
		}

		if (!user.getUsername().equals("")) {

			return new ResponseEntity<String>("Cant update username", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<String>(cloudComputingUserService.updateUser(user, id).toString(),
				HttpStatus.BAD_REQUEST);
	}
}