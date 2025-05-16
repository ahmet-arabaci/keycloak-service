package com.ahmetarabaci.keycloakservice.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakAdminUtil {

	private Keycloak keycloak;
	
	@Value("${realm}")
	private String realm;
	
	@Value("${server-url}")
	private String serverURL;
	
	@Value("${client-id}")
	private String clientID;
	
	@Value("${grant-type}")
	private String grantType;
	
	@Value("${keycloak-username}")
	private String username;
	
	@Value("${keycloak-password}")
	private String password;
	
	public Keycloak getInstance() {
		if (keycloak == null) {
			keycloak = KeycloakBuilder.builder()
				.realm(realm).serverUrl(serverURL).clientId(clientID).grantType(grantType)
				.username(username).password(password)
				.build();
		}
		return keycloak;
	}
	
}
