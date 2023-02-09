package com.example.demo.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.CloudComputingDBProduct;

@Repository
public interface ProductRepository extends JpaRepository<CloudComputingDBProduct,Long> {
	
	CloudComputingDBProduct findBySku(String sku);
}
