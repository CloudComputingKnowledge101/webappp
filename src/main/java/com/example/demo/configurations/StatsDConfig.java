package com.example.demo.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

@Configuration
public class StatsDConfig {

    @Value("${aws.cloudwatch.statsd.host}")
    private String metricHost;

    @Value("${aws.cloudwatch.statsd.port}")
    private int portNumber;

    @Value("${aws.cloudwatch.statsd.prefix}")
    private String prefix;

    @Bean
    public StatsDClient metricClient() {
            
    	return new NonBlockingStatsDClient(prefix, metricHost, portNumber);
    }
}
