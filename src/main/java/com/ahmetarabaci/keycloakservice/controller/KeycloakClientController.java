package com.ahmetarabaci.keycloakservice.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ahmetarabaci.keycloakservice.model.KeycloakUserDto;
import com.ahmetarabaci.keycloakservice.service.KeycloakUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.ws.rs.core.Response;

@RestController
@RequestMapping("/keycloakclient")
@SecurityRequirement(name = "Keycloak")
public class KeycloakClientController {

	private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakClientController.class);
	
	@Autowired
	private KeycloakUtil keycloakUtil;
	
	@Value("${realm}")
	private String realm;
	
	@GetMapping("/getuserlist")
	public List<KeycloakUserDto> getUserList() {
		LOGGER.info("'/keycloakclient/getuserlist' REST endpoint has been called.");
		
		Keycloak keycloak = keycloakUtil.getInstance();
		List<UserRepresentation> userRepList = keycloak.realm(realm).users().list();
		
		List<KeycloakUserDto> keycloakUserList = new ArrayList<KeycloakUserDto>();
		for (UserRepresentation userRep : userRepList) {
			keycloakUserList.add(map(userRep));
		}
		return keycloakUserList;
	}
	
	@PostMapping("/createuser")
	public Response createUser(@RequestBody KeycloakUserDto dto) {
		LOGGER.info("'/keycloakclient/createuser' REST endpoint has been called.");
		
		Keycloak keycloak = keycloakUtil.getInstance();
		Response res = keycloak.realm(realm).users().create(map(dto));
		return Response.ok(dto).build();
	}
	
	/**
	 * It maps "UserRepresentation" into "KeycloakUserDto".
	 * 
	 * @author Ahmet Arabacı
	 * @since 13.04.2025 22:49
	 */
	private KeycloakUserDto map(UserRepresentation userRep) {
		KeycloakUserDto dto = new KeycloakUserDto();
		dto.setFirstName(userRep.getFirstName());
		dto.setLastName(userRep.getLastName());
		dto.setEmail(userRep.getEmail());
		dto.setUsername(userRep.getUsername());
		return dto;
	}
	
	/**
	 * It maps "KeycloakUserDto" into "UserRepresentation".
	 * 
	 * @author Ahmet Arabacı
	 * @since 13.04.2025 22:49
	 */
	private UserRepresentation map(KeycloakUserDto dto) {		
		// Prepare "CredentialRepresentation" list data.
		List<CredentialRepresentation> credentialRepList = new ArrayList<CredentialRepresentation>();
		CredentialRepresentation credentialRep = new CredentialRepresentation();
		credentialRep.setTemporary(false);
		credentialRep.setValue(dto.getPassword());
		credentialRepList.add(credentialRep);
		
		// Prepare "Attributes" data.
		Map<String, List<String>> attributes = new HashMap<String, List<String>>();
		attributes.put("street", Arrays.asList("XXX"));
		attributes.put("locality", Arrays.asList("XXX"));
		attributes.put("postal_code", Arrays.asList("34494"));
		attributes.put("country", Arrays.asList("XXX"));
		attributes.put("region", Arrays.asList("XXX"));
		attributes.put("formatted", Arrays.asList("XXX"));
		
		// Prepare "UserRepresentation" data.
		UserRepresentation userRep = new UserRepresentation();
		userRep.setFirstName(dto.getFirstName());
		userRep.setLastName(dto.getLastName());
		userRep.setEmail(dto.getEmail());
		userRep.setUsername(dto.getUsername());
		userRep.setEnabled(true);
		userRep.setEmailVerified(true);		
		userRep.setCredentials(credentialRepList);						
		userRep.setAttributes(attributes);		
		return userRep;
	}
}
