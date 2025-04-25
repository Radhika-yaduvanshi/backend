//package com.TodayTask.Admin.Panel.authConfig;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class AppConfig implements WebMvcConfigurer {
//
//
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")  // Allow CORS on all paths
//                .allowedOrigins("http://localhost:4200")  // Allow requests from this origin (e.g., Angular app)
//                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Allowed HTTP methods
//                .allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept")  // Allowed headers
//                .exposedHeaders("Content-Disposition")  // Expose specific headers (e.g., for file downloads)
//                .allowCredentials(true)  // Allow credentials (cookies, HTTP authentication)
//                .maxAge(3600);  // Cache CORS preflight response for 1 hour
//    }
//}