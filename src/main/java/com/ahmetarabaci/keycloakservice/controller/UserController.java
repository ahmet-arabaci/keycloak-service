package com.ahmetarabaci.keycloakservice.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ahmetarabaci.keycloakservice.model.UserDto;
import com.ahmetarabaci.keycloakservice.service.KeycloakAdminUtil;
import jakarta.ws.rs.core.Response;

@RestController
@RequestMapping("/user")
public class UserController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private KeycloakAdminUtil util;
	
	@Value("${realm}")
	private String realm;
	
	/**
	 * It returns realm user list.
	 * 
	 * @author Ahmet Arabacı
	 * @since 05.05.2025 11:10
	 */
	@GetMapping("/list")
	public List<UserDto> getUserList() {
		Keycloak keycloak = util.getInstance();
		List<UserRepresentation> userRepList = keycloak.realm(realm).users().list();
		List<UserDto> userList = new ArrayList<UserDto>();
		
		for (UserRepresentation userRep : userRepList) {
			userList.add(map(userRep));
			LOGGER.info(String.format("First Name: %s, Last Name: %s, Email: %s, Username: %s", 
					userRep.getFirstName(), userRep.getLastName(), userRep.getEmail(), userRep.getUsername()));
		}
		return userList;
	}
	
	/**
	 * It returns realm user.
	 * 
	 * @author Ahmet Arabacı
	 * @since 05.05.2025 12:05
	 */
	@GetMapping("/get/{id}")
	public UserDto getUser(@PathVariable("id") String id) {		
		Keycloak keycloak = util.getInstance();
		UserRepresentation userRep = keycloak.realm(realm).users().get(id).toRepresentation();
	
		LOGGER.info(String.format("First Name: %s, Last Name: %s, Email: %s, Username: %s", 
				userRep.getFirstName(), userRep.getLastName(), 
				userRep.getEmail(), userRep.getUsername()));		
		return map(userRep);		
	}
	
	/**
	 * It creates new user in specific realm.
	 * 
	 * @author Ahmet Arabacı
	 * @since 05.05.2025 11:17
	 */
	@PostMapping("/create")
	public String createUser(@RequestBody UserDto dto) {
		try {
			Keycloak keycloak = util.getInstance();
			Response res = keycloak.realm(realm).users().create(map(dto));
			return "User created successfully.";
		} catch (Exception e) {
			LOGGER.error("Exception occurred while executing createUser function!", e);
			return "Exception occurred while executing createUser function!";
		}
		
	}
	
	/**
	 * It updates user in specific realm.
	 * 
	 * @author Ahmet Arabacı
	 * @since 05.05.2025 14:38
	 */
	@PutMapping("/update")
	public String updateUser(@RequestBody UserDto dto) {
		try {
			Keycloak keycloak = util.getInstance();
			UserRepresentation userRep = map(dto);
			keycloak.realm(realm).users().get(dto.getId()).update(userRep);
			return "User updated successfully.";
		} catch (Exception e) {
			LOGGER.error("Exception occurred while executing updateUser function!", e);
			return "Exception occurred while executing updateUser function!";
		}
		
	}
	
	/**
	 * It deletes specific user with ID.
	 * 
	 * @author Ahmet Arabacı
	 * @since 05.05.2025 11:17
	 */
	@DeleteMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") String id) {
		try {
			Keycloak keycloak = util.getInstance();			
			keycloak.realm(realm).users().delete(id);
			return "User deleted successfully.";
		} catch (Exception e) {
			LOGGER.error("Exception occurred while executing deleteUser function!", e);
			return "Exception occurred while executing deleteUser function!";
		}
		
	}
	
	/**
	 * It returns all roles of specific user.
	 * 
	 * @author Ahmet Arabacı
	 * @since 05.05.2025 11:17
	 */
	@GetMapping("/roles/{id}")
	public String getRoles(@PathVariable("id") String id) {
		try {
			Keycloak keycloak = util.getInstance();			
			List<RoleRepresentation> roleRepList = keycloak.realm(realm).users()
					.get(id).roles().realmLevel().listAll();
			for (RoleRepresentation roleRep : roleRepList) {
				LOGGER.info(String.format("Role ID: %s, Role Name: %s", 
					roleRep.getId(), roleRep.getName()));
			}
			return "OK";
		} catch (Exception e) {
			LOGGER.error("Exception occurred while executing getRoles function!", e);
			return "Exception occurred while executing getRoles function!";
		}
		
	}
	
	private UserDto map(UserRepresentation userRep) {
		UserDto dto = new UserDto();		
		dto.setFirstName(userRep.getFirstName());
		dto.setLastName(userRep.getLastName());
		dto.setEmail(userRep.getEmail());
		dto.setUserName(userRep.getUsername());
		return dto;
	}
	
	/**
	 * It maps "UserDto" into "UserRepresentation".
	 * 
	 * @author Ahmet Arabacı
	 * @since 13.04.2025 22:49
	 */
	private UserRepresentation map(UserDto dto) {
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
		userRep.setId(dto.getId());
		userRep.setFirstName(dto.getFirstName());
		userRep.setLastName(dto.getLastName());
		userRep.setEmail(dto.getEmail());
		userRep.setUsername(dto.getUserName());
		userRep.setEnabled(true);
		userRep.setEmailVerified(true);
		userRep.setCredentials(credentialRepList);
		userRep.setAttributes(attributes);
		return userRep;
	}
	
}
