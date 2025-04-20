package com.ahmetarabaci.keycloakservice.service;

import org.springframework.stereotype.Service;
import com.ahmetarabaci.keycloakservice.model.UserDto;

@Service
public class UserService {

	public UserDto getUserInfo() {
		return new UserDto("f8283641-d709-4d5c-97c5-27ea1f1ca1c9", "Ahmet", "Arabaci");
	}
	
	public UserDto getAdminInfo() {
		return new UserDto("233db5c4-ae55-4ce6-8392-1834449d9ee8", "Rahmi", "Arabaci");
	}
	
	public UserDto getManagerInfo() {
		return new UserDto("570411b5-86b8-48d2-8e93-025c69e6819e", "Gulsen", "Arabaci");
	}
	
	public UserDto updateUserInfo(String guid) {
		return new UserDto(guid, "Ahmet", "Arabaci");
	}
	
}
