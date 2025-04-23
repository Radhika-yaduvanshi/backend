package com.TodayTask.Admin.Panel.authConfig;//package com.blogwebsite.user.authConfig;
//
import com.TodayTask.Admin.Panel.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

public class CustomUser implements UserDetails
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public CustomUser(UserEntity user) {
		super();
		this.user = user;
	}

	@Autowired
	private UserEntity user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
          String role = "ROLE_" + user.getAccessRole();

		SimpleGrantedAuthority simpleGrantedAuthority=new SimpleGrantedAuthority(role);
		System.err.println("Role is : "+user.getAccessRole());
		return Arrays.asList(simpleGrantedAuthority);

//		return Collections.singleton(new SimpleGrantedAuthority(user.getRole()));
//		return  null;

	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}
//
//
//
//
}
