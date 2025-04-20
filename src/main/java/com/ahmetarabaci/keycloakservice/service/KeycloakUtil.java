package com.ahmetarabaci.keycloakservice.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakUtil {
	
	private Keycloak keycloak;
	
	@Value("${realm}")
	private String realm;
	
	@Value("${server-url}")
	private String serverUrl;
	
	@Value("${client-id}")
	private String clientId;
	
	@Value("${grant-type}")	
	private String grantType;
	
	@Value("${keycloak-username}")
	private String username;
	
	@Value("${keycloak-password}")
	private String password;
	
	/**
	 * It returns a Keycloak instance.
	 * 
	 * @author Ahmet Arabacı
	 * @since 13.04.2025 21:44
	 */
	public Keycloak getInstance() {
		if (keycloak == null) {
			keycloak = KeycloakBuilder.builder()
				.realm(realm)
				.serverUrl(serverUrl)
				.clientId(clientId)
				.grantType(grantType)
				.username(username)
				.password(password)
				.build();
		}
		return keycloak;
	}
	
}

