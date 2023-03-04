package com.example.demo.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.services.CloudComputingUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new CloudComputingUserDetailsService();
	}

	@Bean
	public PasswordEncoder myPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/*@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(HttpMethod.POST, "/v1/user")
        								.requestMatchers(HttpMethod.GET, "/healthz")
        								.requestMatchers(HttpMethod.GET, "/v1/product/*");
    }*/
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests()
		.requestMatchers(HttpMethod.POST, "/v1/user").permitAll()
		.requestMatchers(HttpMethod.GET, "/healthz").permitAll()
		.requestMatchers(HttpMethod.GET, "/v1/product/*").permitAll()
        .anyRequest().authenticated()
        .and()
        .httpBasic()
        .and()
        .csrf().disable();
		
		return http.build();
	}
}
