package com.TodayTask.Admin.Panel.authConfig;

//import com.auth.config.CustomUser;
//import com.blogwebsite.user.authConfig.CustomUser;

import com.TodayTask.Admin.Panel.Entity.UserEntity;
import com.TodayTask.Admin.Panel.repository.UserRepo;
import com.TodayTask.Admin.Panel.util.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component(value = "bean from custom user service")
public class CustomUserService implements UserDetailsService
{

	@Autowired
	private UserRepo userRepo;
	@Autowired
	private Helper helper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		if (username == null || username.isEmpty()) {
			throw new UsernameNotFoundException("Username cannot be null or empty");
		}
		System.err.println("inside loadbyusername : =========================");
		UserEntity byUserName = userRepo.findByUserName(username).orElseThrow(()->new RuntimeException("User not found"));
//		UserEntity userEntity = userRepo.findByEmail(username);
		System.err.println("email of usr in loaduserbyusername function in userdetailservices : "+byUserName.getUserName());
		if (byUserName == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}


		System.err.println("User found: " + byUserName.getUserName());
		return new CustomUser(byUserName);
//		return new CustomUser(byUserName);
//		return new CustomUser(userEntity);

//		UserEntity userbyemail = userRepo.findByUserName(email);
//		UserEntity user=helper.convert(userbyemail, UserEntity.class);
//
//		if(user==null){
//			throw  new UsernameNotFoundException("User not found with email : " +email);
//		}
//
////		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
//		return new User(byUserName.getName(), byUserName.getPassword(), new ArrayList<>());

		//we also can return custom-user->CustomUser(user)
		//but here we are returning spring's default user


	}
	
	
}
