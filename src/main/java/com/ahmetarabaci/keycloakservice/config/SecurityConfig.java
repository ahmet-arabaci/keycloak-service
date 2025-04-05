package com.ahmetarabaci.keycloakservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import com.ahmetarabaci.keycloakservice.service.CustomJWTAuthConverter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
		
	@Autowired
	private CustomJWTAuthConverter customJWTAuthConverter;
			
	@Bean
	public SecurityFilterChain initSecurityFilterChain(HttpSecurity http) throws Exception {			
		http.csrf(t -> t.disable());
		http.authorizeHttpRequests(authorize -> { 
			authorize
			.requestMatchers(HttpMethod.GET, "/getuserinfo").permitAll()
			.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
			.anyRequest().authenticated();
		});
		// 1st WAY: JWT ISSUER
		http.oauth2ResourceServer(t -> {
			// t.jwt(configurer -> configurer.jwtAuthenticationConverter(customJWTAuthConverter));
			t.jwt(Customizer.withDefaults());
		});
		// 2nd WAY: OPAQUETOKEN
		// http.oauth2ResourceServer(t -> { t.opaqueToken(Customizer.withDefaults()); });
		http.sessionManagement(t -> { t.sessionCreationPolicy(SessionCreationPolicy.STATELESS); });		
		return http.build();
	}
	
	/**
	 * It is implemented to remove unnecessary CustomJWTAuthConverter - SimpleGrantedAuthority "ROLE_" prefix.
	 * To trigger this bean definition, SecurityFilterChain bean must be use 
	 * "t.jwt(configurer -> configurer.jwtAuthenticationConverter(customJWTAuthConverter));" oauth2 resource server.
	 * 
	 * @author Ahmet Arabaci - AhmetAra
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
	 * @author Ahmet Arabaci - AhmetAra
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
}


