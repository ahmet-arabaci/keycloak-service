package com.ahmetarabaci.keycloakservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ahmetarabaci.keycloakservice.model.UserDto;
import com.ahmetarabaci.keycloakservice.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(name = "Keycloak")
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService service;
	
	// Authorization not required.
	@GetMapping("/getuserinfo")
	public UserDto getUserInfo() {
		LOGGER.info("'/getuserinfo' REST endpoint has been called.");
		return service.getUserInfo();
	}
	
	// Authorization -> Role: 'admin', Scope: POST
	@PostMapping("/updateuserinfo")
	public UserDto createUserInfo(@RequestBody String guid) {
		LOGGER.info("'/updateuserinfo' (HTTP POST) REST endpoint has been called.");
		return service.updateUserInfo(guid);
	}
	
	// Authorization -> Role: 'manager', Scope: PUT
	@PutMapping("/updateuserinfo")
	public UserDto updateUserInfo(@RequestBody String guid) {
		LOGGER.info("'/updateuserinfo' (HTTP PUT) REST endpoint has been called.");
		return service.updateUserInfo(guid);
	}
		
	// Authorization -> Role: 'admin'
	@GetMapping("/getadmininfo")	
	// @PreAuthorize("hasRole('admin')")
	public UserDto getAdminInfo() {
		LOGGER.info("/getadmininfo' REST endpoint has been called.");
		return service.getAdminInfo();
	}
	
	// Authorization -> Role: 'manager'
	@GetMapping("/getmanagerinfo")
	// @PreAuthorize("hasRole('manager')")
	public UserDto getManagerInfo() {
		LOGGER.info("/getmanagerinfo' REST endpoint has been called.");
		return service.getManagerInfo();
	}
	
	/**
	 * Authorization -> Policy: 'User'
	 * Authorized User: 'manager'
	 * Unauthorized User: 'admin', 'regex', 'time'
	 * 
	 * @author Ahmet Arabacı
	 * @since 10.04.2025 23:44
	 */	
	@PostMapping("/testuserpolicy")
	public String testUserPolicy() {
		return "'/testuserpolicy' endpoint has been authorized.";
	}
	
	/**
	 * Authorization -> Policy: 'Group'
	 * Authorized User: 'admin'
	 * Unauthorized User: 'manager', 'regex', 'time'
	 * 
	 * @author Ahmet Arabacı
	 * @since 10.04.2025 23:44
	 */	
	@PostMapping("/testgrouppolicy")
	public String testGroupPolicy() {
		return "'/testgrouppolicy' endpoint has been authorized.";
	}
	
	/**
	 * Authorization -> Policy: 'Regex'
	 * Authorized User: 'regex'
	 * Unauthorized User: 'manager', 'admin', 'time'
	 * 
	 * @author Ahmet Arabacı
	 * @since 13.04.2025 14:36
	 */
	@PostMapping("/testregexpolicy")
	public String testRegexPolicy() {
		return "'/testregexpolicy' endpoint has been authorized.";
	}
	
	/**
	 * Authorization -> Policy: 'Time'
	 * Authorized User: 'time', 'manager', 'admin', 'regex'
	 * Unauthorized User: Any one of them which in expires time.
	 * 
	 * @author Ahmet Arabacı
	 * @since 13.04.2025 14:36
	 */
	@PostMapping("/testtimepolicy")
	public String testTimePolicy() {
		return "'/testtimepolicy' endpoint has been authorized.";
	}
}

