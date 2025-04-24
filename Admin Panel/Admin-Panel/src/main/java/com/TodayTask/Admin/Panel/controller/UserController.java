package com.TodayTask.Admin.Panel.controller;

import com.TodayTask.Admin.Panel.Entity.LoginRequest;
import com.TodayTask.Admin.Panel.Entity.LoginResponse;
import com.TodayTask.Admin.Panel.Entity.UserEntity;
import com.TodayTask.Admin.Panel.proxy.UserProxy;
import com.TodayTask.Admin.Panel.repository.UserRepo;
import com.TodayTask.Admin.Panel.service.UserService;
import com.TodayTask.Admin.Panel.serviceImpl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepo userRepo;



    //email
    // 1. Send OTP to Email
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        String otp = userService.generateAndSendOtp(email);
        return ResponseEntity.ok("OTP sent successfully to your email.");
    }

    // 2. Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isVerified = userService.verifyOtp(email, otp);
        if (isVerified) {
            return ResponseEntity.ok("OTP verified successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }
    }


    @GetMapping("/search")
    public List<UserEntity> searchUsers(@RequestParam("keyword") String keyword) {
        return userService.searchUsers(keyword);
    }


    @PostMapping("/generate/{count}")
    public String generateUsers(@PathVariable int count) {
        userService.generateFakeUsers(count);
        return count + " fake users generated successfully!";
    }

    @GetMapping("/find/{name}")
    public String findbyusername(String name){
     UserEntity user  =   userRepo.findByUserName(name).orElseThrow(()->new RuntimeException("User not found" +
             ""));
        System.err.println("user name is : "+user.getUserName());

        return "user found";
    }

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public String registerUser(
            @RequestParam("user") String userJson,        // Get it as a string
            @RequestParam("image") MultipartFile image
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd")); // Optional if using date fields
            UserProxy userProxy = mapper.readValue(userJson, UserProxy.class);  // Manually convert JSON string

            return userService.registerUser(userProxy, image);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while parsing user data: " + e.getMessage();
        }
    }


    @PostMapping("/loginReq") //working
    public LoginResponse login(@RequestBody LoginRequest loginRequest)
    {
        System.out.println("THis is controller");
        System.out.println(loginRequest.getPassword()+"\n"+loginRequest.getUserName());
        return userService.login(loginRequest);
    }


    @GetMapping("/getAllUsers")
    public List<UserProxy> getallusers(){
        return userService.getAllUsers();
    }



    @GetMapping("/getUserById/{id}")
    public UserProxy getuserbyId(@PathVariable("id")Long id){
        return  userService.getUserById(id);
    }

    @GetMapping("/getuserIdByUserName/{name}")
    public Long userIdByUserName(@PathVariable("name") String name){
        return userService.getUserIdByName(name);
    }

    @DeleteMapping("/deleteUser/{id}")
    public String deleteuser(@PathVariable("id") Long id){
        return userService.deleteUser(id);
    }





    @PutMapping("/update")
    public String updateUser(@RequestParam("user") String userJson,
                             @RequestParam("image") MultipartFile image,
                             @RequestParam("id") Long id) {
        try {
            System.out.println("INside update controller ===========================================================================================================");
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd")); // Optional if using date fields
            UserProxy userProxy = mapper.readValue(userJson, UserProxy.class); // Manually convert JSON string to UserProxy

            return userService.updateUser(userProxy,image,id); // Call the service method to update user
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while parsing user data: " + e.getMessage();
        }
    }

    @GetMapping("/get-image/{imageName}")
    public ResponseEntity<byte[]> getEventBanner(@PathVariable String imageName) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/webp"))
                .body(userService.getProfileImage(imageName));
    }

}
