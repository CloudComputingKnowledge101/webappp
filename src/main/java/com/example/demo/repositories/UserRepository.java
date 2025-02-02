package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.CloudComputingDBUser;

@Repository
public interface UserRepository extends JpaRepository<CloudComputingDBUser,Long> {
	
	Optional<CloudComputingDBUser> findByUsername(String username);
}























