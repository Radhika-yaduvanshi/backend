package com.TodayTask.Admin.Panel.authConfig;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class Config 
{
	
	@Autowired
	private JwtFilter jwtFilter;
	
	@Autowired
	private JwtEntryPoint jwtEntryPoint;
	
	@Autowired
	private CustomUserService customUserService;

	
	@Bean(name = "userDetails Service Bean")
	public UserDetailsService userDetailsService()
	{
		return customUserService;
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
		System.out.println("security filter chain called..");
		http.csrf(csrf->csrf.disable());
//		http.authorizeHttpRequests(auth->auth.requestMatchers("/","/user/saveUser","/user/generate","/extractAll/**","/dateEx/**","/user/loginReq","/user/register",
//				"/generateOTP").permitAll().
		http.authorizeHttpRequests(auth->auth.requestMatchers("/user/generate","/user/loginReq","/user/register","/user/**").permitAll().
				anyRequest().authenticated());
		http.httpBasic(Customizer.withDefaults());
		http.exceptionHandling(auth->auth.authenticationEntryPoint(jwtEntryPoint));
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
	@Bean
	public BCryptPasswordEncoder byBCryptPasswordEncoder()
	{
		System.out.println("bcryptPasswordEncoder called..");
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider daoAuthenticationProvider()
	{
		System.out.println("Authentication Provider called..");
		DaoAuthenticationProvider dap=new DaoAuthenticationProvider();
		
		dap.setUserDetailsService(userDetailsService());
		dap.setPasswordEncoder(byBCryptPasswordEncoder());
		return dap;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
	{
		System.out.println("authentication manager called.");
		return authenticationConfiguration.getAuthenticationManager();
	}
}
