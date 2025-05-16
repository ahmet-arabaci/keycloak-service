package com.ahmetarabaci.keycloakservice.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation for JWT authentication converter configurer.
 * 
 * @author Ahmet Arabacı
 * @since 24.04.2025 15:24
 */
@Component
public class CustomJWTAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
		
	@Override
	public AbstractAuthenticationToken convert(Jwt accessToken) {
		if (accessToken.getClaim("realm_access") != null) {
			Map<String, Object> realmAccess = accessToken.getClaim("realm_access");
			List<String> rolesFromAccessToken = new ObjectMapper().convertValue(
					realmAccess.get("roles"), new TypeReference<List<String>>() {});
			
			List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
			for (String role : rolesFromAccessToken) {
				// roles.add(new SimpleGrantedAuthority("ROLE_" + role));
				roles.add(new SimpleGrantedAuthority(role));
			}			
			return new JwtAuthenticationToken(accessToken, roles);
		}
		return new JwtAuthenticationToken(accessToken, new ArrayList<GrantedAuthority>());
	}
	
}

