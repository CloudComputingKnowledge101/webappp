package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.CloudComputingDBImage;

@Repository
public interface ImageRepository extends JpaRepository<CloudComputingDBImage,Long> {
	
}