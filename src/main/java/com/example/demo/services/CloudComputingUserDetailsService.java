package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.demo.models.CloudComputingDBUser;
import com.example.demo.repositories.UserRepository;

public class CloudComputingUserDetailsService implements UserDetailsService {
	private static final Logger LOG = LogManager.getLogger(UserDetailsService.class);
	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		System.out.println("Searching for user in database ....");
		LOG.info("###########__Searching for user in database__#############" + "\tClassname: "
				+ this.getClass().getName() + "\tMethod Name: loadUserByUsername");
		Optional<CloudComputingDBUser> user = repository.findByUsername(username);

		if (!user.isPresent()) {
			LOG.error("###########__No such user present__#############" + "\tClassname: " + this.getClass().getName()
					+ "\tMethod Name: loadUserByUsername");
			throw new BadCredentialsException("No such user present");
		}

		System.out.println("User found ....");
		LOG.info("###########__USER FOUND__#############" + "\tClassname: " + this.getClass().getName()
				+ "\tMethod Name: loadUserByUsername");
		CloudComputingDBUser dbUser = user.get();

		List<GrantedAuthority> gAuthorities = new ArrayList<>();
		gAuthorities.add(new SimpleGrantedAuthority("USER"));

		SecuredCloudComputingUser secUser = new SecuredCloudComputingUser();
		secUser.setAuthorities(gAuthorities);
		secUser.setUsername(dbUser.getUsername());
		secUser.setPassword(dbUser.getPassword());
		LOG.info("###########__AUTHENTICATED USER__#############" + "\tClassname: " + this.getClass().getName()
				+ "\tMethod Name: loadUserByUsername");
		return secUser;
	}

}
