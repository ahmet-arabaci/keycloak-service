package com.ahmetarabaci.keycloakservice.controller;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahmetarabaci.keycloakservice.model.RoleDto;
import com.ahmetarabaci.keycloakservice.service.KeycloakAdminUtil;

@RestController
@RequestMapping("/role")
public class RoleController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);
	
	@Autowired
	private KeycloakAdminUtil util;
	
	@Value("${realm}")
	private String realm;
	
	@GetMapping("/get/{name}")
	public String getRole(@PathVariable("name") String name) {
		try {
			Keycloak keycloak = util.getInstance();
			RoleRepresentation roleRep = keycloak.realm(realm).roles()
					.get(name).toRepresentation();
			
			LOGGER.info(String.format("ID: %s, Name: %s, Description: %s", 
				roleRep.getId(), roleRep.getName(), roleRep.getDescription()));
			return "OK";
		} catch (Exception e) {
			LOGGER.error("Exception occurred while executing getRole function!", e);
			return "Exception occurred while executing getRole function!";
		}
	}
	
	@PostMapping("/create")
	public String createRole(@RequestBody RoleDto role) {
		try {
			Keycloak keycloak = util.getInstance();
			keycloak.realm(realm).roles().create(map(role));
			return "OK";
		} catch (Exception e) {
			LOGGER.error("Exception occurred while executing createRole function!", e);
			return "Exception occurred while executing createRole function!";
		}
	}
	
	private RoleRepresentation map(RoleDto dto) {
		RoleRepresentation roleRep = new RoleRepresentation();
		roleRep.setId(dto.getId());
		roleRep.setName(dto.getName());
		roleRep.setDescription(dto.getDescription());
		return roleRep;
	}
}
