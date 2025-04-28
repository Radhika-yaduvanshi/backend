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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebSecurity
public class Config implements WebMvcConfigurer
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
		http.authorizeHttpRequests(auth->auth.requestMatchers("/user/generate/**","/user/loginReq","/user/register","/user/update","update/**","/user/forgot-password/**","/user/reset-password/**","/user/validate-token/**","/user/reset-password/**","/user/getProfileImage/**").permitAll().
				anyRequest().authenticated());
		http.httpBasic(Customizer.withDefaults());
//		http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
		http.exceptionHandling(auth->auth.authenticationEntryPoint(jwtEntryPoint));
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		                http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

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


	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of("http://localhost:52575", "http://localhost:4200"));

		configuration.setAllowedHeaders(List.of("*"));

		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}




}
