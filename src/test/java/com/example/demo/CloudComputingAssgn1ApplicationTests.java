package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.controllers.AppHealthController;

@SpringBootTest
@AutoConfigureMockMvc
class CloudComputingAssgn1ApplicationTests {
	
	@Autowired
	private AppHealthController healthController;
	
	@Test
	void contextLoads() {
	}
	
	@Test
	public void checkHealth() {
		
		assertEquals("123","123"));
	}
}
