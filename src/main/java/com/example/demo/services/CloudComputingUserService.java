package com.example.demo.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.daos.SignupForm;
import com.example.demo.models.CloudComputingDBUser;
import com.example.demo.repositories.UserRepository;

@Service
public class CloudComputingUserService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private PasswordEncoder encoder;

	public CloudComputingDBUser register(SignupForm form) {

		System.out.println("Mapping user details database object ....");

		CloudComputingDBUser user = new CloudComputingDBUser();
		user.setUsername(form.getUsername());
		user.setPassword(encoder.encode(form.getPassword()));
		user.setFirst_name(form.getFirst_name());
		user.setLast_name(form.getLast_name());

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

		user.setAccount_created(timeStamp);
		user.setAccount_updated(timeStamp);

		System.out.println("Database object created ....");

		/*
		 * Authority authority = new Authority() ; authority.setAuthority( "USER" );
		 * authority.setUsername(user);
		 * 
		 * List<Authority> authorities = new ArrayList<Authority>() ;
		 * authorities.add(authority) ;
		 * 
		 * user.setAuthorities( authorities );
		 */

		repository.saveAndFlush(user);
		// authRepository.saveAndFlush( authority ) ;

		return user;
	}

	public CloudComputingDBUser getUser(Long id) {

		return repository.getReferenceById(id);
	}

	public CloudComputingDBUser getUser(String username) {

		Optional<CloudComputingDBUser> user = repository.findByUsername(username);

		if (!user.isPresent()) {
			return null;
		}

		return user.get();
	}

	public CloudComputingDBUser updateUser(SignupForm user, CloudComputingDBUser existing) {
		
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		
		if (!user.getFirst_name().equals(existing.getFirst_name()) && !user.getFirst_name().equals("")) {

			existing.setFirst_name(user.getFirst_name());
			existing.setAccount_updated(timeStamp);
		}

		if (!user.getLast_name().equals(existing.getLast_name()) && !user.getLast_name().equals("")) {

			existing.setLast_name(user.getLast_name());
			existing.setAccount_updated(timeStamp);
		}

		if (!encoder.matches(user.getPassword(), existing.getPassword()) && !user.getPassword().equals("")) {

			existing.setPassword(encoder.encode(user.getPassword()));
			existing.setAccount_updated(timeStamp);
		}

		return repository.saveAndFlush(existing);
	}
}
