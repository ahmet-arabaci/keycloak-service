package com.ahmetarabaci.keycloakservice.config;

import java.io.IOException;
import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.adapters.authorization.spi.ConfigurationResolver;
import org.keycloak.adapters.authorization.spi.HttpRequest;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
// @EnableMethodSecurity
public class KeycloakConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakConfig.class);
	
	@Autowired
	private CustomJWTAuthConverter customJWTAuthConverter;
	
	@Bean
	public SecurityFilterChain initSecurityFilterChain(HttpSecurity http) throws Exception {
		
		http.csrf(t -> t.disable());
		
		/*
		http.authorizeHttpRequests(authorize -> {			
			authorize
			.requestMatchers("/test/public").permitAll()
			.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
			.anyRequest().authenticated();
		});
		*/
		
		// OAuth2 Resource Server		
		http.oauth2ResourceServer(t -> {
			// 1st WAY : JWT ISSUER
			t.jwt(Customizer.withDefaults());
			// t.jwt(configurer -> configurer.jwtAuthenticationConverter(customJWTAuthConverter));

			// 2nd WAY: OPAQUETOKEN
			// t.opaqueToken(Customizer.withDefaults()); 			
		});		
		
		// Keycloak-Level Authorization ('keycloak-policy' JSON File)
		http.addFilterAfter(getServletPolicyEnforcerFilter(), BearerTokenAuthenticationFilter.class);
		
		http.sessionManagement(t -> { t.sessionCreationPolicy(SessionCreationPolicy.STATELESS); });
		return http.build();
	}
	
	/**
	 * It is implemented to remove unnecessary CustomJWTAuthConverter - SimpleGrantedAuthority "ROLE_" prefix.
	 * To trigger this bean definition, SecurityFilterChain bean must be use 
	 * "t.jwt(configurer -> configurer.jwtAuthenticationConverter(customJWTAuthConverter));" oauth2 resource server.
	 * 
	 * @author Ahmet Arabacı
	 * @since 04.04.2025 00:24
	 */	
	@Bean
	public DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {		
		DefaultMethodSecurityExpressionHandler secExpHandler = new DefaultMethodSecurityExpressionHandler();
		secExpHandler.setDefaultRolePrefix("");
		return secExpHandler;
	}
	
	/**
	 * Custom bean definition for JWT Authentication Converter which includes granted authorities.
	 * To trigger this bean definition, SecurityFilterChain bean must be use 
	 * "t.jwt(Customizer.withDefaults());" oauth2 resource server.
	 * 
	 * @author Ahmet Arabacı
	 * @since 04.04.2025 00:37
	 */
	@Bean
	public JwtAuthenticationConverter customizedJWTAuthConverter() {
		JwtAuthenticationConverter authenticationCon = new JwtAuthenticationConverter();
		JwtGrantedAuthoritiesConverter authorizationCon = new JwtGrantedAuthoritiesConverter();
		
		// It must be override with empty string. Because its default is "SCOPE_".
		authorizationCon.setAuthorityPrefix("");
		
		// It must be override with role-related field of access token. Because its default is "scope" or "scp".
		authorizationCon.setAuthoritiesClaimName("roles");
		
		authenticationCon.setJwtGrantedAuthoritiesConverter(authorizationCon);
		return authenticationCon;
	}
	
	/**
	 * It returns keycloak policies.
	 * 
	 * @author Ahmet Arabacı
	 * @since 06.04.2025 16:09
	 */
	private ServletPolicyEnforcerFilter getServletPolicyEnforcerFilter() {
		return new ServletPolicyEnforcerFilter(new ConfigurationResolver() {			
			@Override
			public PolicyEnforcerConfig resolve(HttpRequest request) {
				try {
					return JsonSerialization.readValue(getClass().getResourceAsStream("/keycloak-policy.json"),
							PolicyEnforcerConfig.class);
				} catch (IOException e) {			
					LOGGER.error("getServletPolicyEnforcerFilter | IOException occurred "
							+ "while reading values from JSON file!", e);
					throw new RuntimeException(e);
				}
			}
		});
	}
	
}

