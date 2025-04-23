package com.TodayTask.Admin.Panel.authConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        try {
            System.out.println("do filter internal");
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String userName = null;
//            String email = null;

            System.err.println("header is here : " + authHeader);

            if (authHeader != null && authHeader.startsWith("Bearer ")) //header prefix Bearer
            {
                token = authHeader.substring(7);//remove bearer
                System.out.println("Token in do internal : " + token);

                // Extracting username from the token
	            userName =applicationContext.getBean(JwtService.class).extractUserName(token);
//                email = applicationContext.getBean(JwtService.class).extractUserName(token);
                System.out.println("User name  in Dointernal : " + userName);
//                System.out.println("email in do internal filter : " + email);
                jwtService.extractUserName(token);
            }

            // If username is extracted and there is no authentication in the current SecurityContext
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                //check the username should not null and securitycontext should be null

                // Loading UserDetails by username extracted from the token
                UserDetails userDetails = applicationContext.getBean(CustomUserService.class).loadUserByUsername(userName);

                System.err.println("USer Details : " + userDetails);
                 System.out.println(userDetails);
                // Validating the token with loaded UserDetails
                if (jwtService.verifyTocken(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    System.err.println("");

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            System.out.println("catch block from jwt filter..");
            response.setContentType("application/json");
//			new ObjectMapper().writeValue(response.getOutputStream(), new ErrorResponse(e.getMessage(), 404));
        }

    }

}
