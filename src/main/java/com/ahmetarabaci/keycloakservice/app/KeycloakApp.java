package com.ahmetarabaci.keycloakservice.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ahmetarabaci.*"})
@SecurityScheme(
	name = "Keycloak", 
	openIdConnectUrl = "http://localhost:8080/realms/test-realm/.well-known/openid-configuration",
	scheme = "bearer",
	type = SecuritySchemeType.OPENIDCONNECT,
	in = SecuritySchemeIn.HEADER	
)
public class KeycloakApp {

	public static void main(String[] args) {
		SpringApplication.run(KeycloakApp.class, args);
	}
	
}

