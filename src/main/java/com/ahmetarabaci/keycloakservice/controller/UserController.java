package com.ahmetarabaci.keycloakservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ahmetarabaci.keycloakservice.model.UserDto;
import com.ahmetarabaci.keycloakservice.service.UserService;

@RestController
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService service;
	
	// Authorization not required.
	@GetMapping("/getuserinfo")
	public UserDto getUserInfo() {
		LOGGER.info("getUserInfo REST endpoint has been called.");
		return service.getUserInfo();
	}
	
	// Authorization Role: 'admin'
	@GetMapping("/getadmininfo")	
	@PreAuthorize("hasRole('admin')")
	public UserDto getAdminInfo() {
		LOGGER.info("getAdminInfo REST endpoint has been called.");
		return service.getAdminInfo();
	}
	
	// Authorization Role: 'manager'
	@GetMapping("/getmanagerinfo")
	@PreAuthorize("hasRole('manager')")
	public UserDto getManagerInfo() {
		LOGGER.info("getManagerInfo REST endpoint has been called.");
		return service.getManagerInfo();
	}
}

