package com.example.demo.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.timgroup.statsd.StatsDClient;

@RestController
@RequestMapping("/healthz")
public class AppHealthController {

	private static final Logger LOG = LogManager.getLogger(AppHealthController.class);
	@Autowired
	private StatsDClient statsDClient;

	@GetMapping
	public String checkHealth() {

		// statsDClient.incrementCounter("healthz");
		// statsDClient.recordExecutionTime("healthz", System.currentTimeMillis());
		statsDClient.incrementCounter("counts_api_call_getv1/healthz");
		//statsDClient.recordGaugeValue("baz", 100);
		//statsDClient.recordExecutionTime("bag", 25);
		//statsDClient.recordSetEvent("qux", "one");
		LOG.info("###########__Healthz_OK__#############");
		return "";
	}
}
