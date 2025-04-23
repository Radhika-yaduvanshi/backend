package com.TodayTask.Admin.Panel.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

//	private String userName;
	private String userName;
	private String token;

	private List<SimpleGrantedAuthority> accessRole;
}
