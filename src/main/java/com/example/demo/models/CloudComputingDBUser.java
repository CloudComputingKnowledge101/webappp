package com.example.demo.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "User")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CloudComputingDBUser implements Serializable {
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="first_name")
	private String first_name;
	
	@Column(name="last_name")
	private String last_name;
	
	@Column(name="password")
	private String password;
	
	@Column(name="username")
	private String username;
	
	@Column(name="account_created")
	private String account_created;
	
	@Column(name="account_updated")
	private String account_updated;
	
	public CloudComputingDBUser() {
		
	}
	
	public CloudComputingDBUser(Long id, String first_name, String last_name, String password, String username,
			String account_created, String account_updated) {
		super();
		this.id = id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.password = password;
		this.username = username;
		this.account_created = account_created;
		this.account_updated = account_updated;
	}

	public Long getId() {
		return id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAccount_created() {
		return account_created;
	}

	public void setAccount_created(String account_created) {
		this.account_created = account_created;
	}

	public String getAccount_updated() {
		return account_updated;
	}

	public void setAccount_updated(String account_updated) {
		this.account_updated = account_updated;
	}

	@Override
	public String toString() {
		return "CloudComputingDBUser [id=" + id + ", first_name=" + first_name + ", last_name=" + last_name
				+ ", username=" + username + ", account_created=" + account_created + ", account_updated="
				+ account_updated + "]";
	}
}
