package com.TodayTask.Admin.Panel.controller;

import com.TodayTask.Admin.Panel.Entity.LoginRequest;
import com.TodayTask.Admin.Panel.Entity.LoginResponse;
import com.TodayTask.Admin.Panel.Entity.UserEntity;
import com.TodayTask.Admin.Panel.proxy.ResetPasswordRequest;
import com.TodayTask.Admin.Panel.proxy.UserProxy;
import com.TodayTask.Admin.Panel.repository.UserRepo;
import com.TodayTask.Admin.Panel.service.UserService;
import com.TodayTask.Admin.Panel.serviceImpl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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


    @GetMapping("/getalluser")
    public Page<UserProxy> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsers(page, size);
    }

//    @GetMapping("/getalluser")
//    public List<UserProxy> allusers(){
//        return userService.getAllUser();
//    }



    @GetMapping("/getUserById/{id}")
    public UserProxy getuserbyId(@PathVariable("id")Long id){
        return  userService.getUserById(id);
    }

    @GetMapping("/getuserIdByUserName/{name}")
    public Long userIdByUserName(@PathVariable("name") String name){
        return userService.getUserIdByName(name);
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) {
        System.out.println("inside controller : ");
        String response = userService.deleteUser(id);

        System.out.println("here after delete : ");

        // Log response for debugging
        System.out.println("Delete response: " + response);

        if (response.equals("User marked as deleted successfully!")) {
            return ResponseEntity.ok(response);  // 200 OK response
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);  // 404 if user not found
        }
    }


//    @GetMapping("/getNonDeletedUsers")
//    public ResponseEntity<List<UserEntity>> getIsDeletedFalseActicveTrue(){
//        System.out.println("inside nondeleted controller ...");
//
//        List<UserEntity> users = userService.getIsDeletedFalseActicveTrue();
//        System.out.println("users non deleted : "+users);
////        if(users.isEmpty()){
////            return  ResponseEntity.noContent().build();
////        }
//        return ResponseEntity.ok((users));
//    }


    @GetMapping("/isDeleted")
    public List<UserEntity> isDeleted(){
        return userService.isDeleted();

    }
//
//    @GetMapping("/nonDeletedUsers")
//    public List<UserEntity> nonDeleted(){
//        return userService.nonDeleted();
//    }
@GetMapping("/getNonDeletedUsers")
public Page<UserProxy> getNonDeletedUsers(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size) {
    return userService.getNonDeletedUsers(page, size);
}



    @PutMapping("/update/{id}")
    public String updateUser(@RequestBody UserProxy userProxy,
                             @PathVariable("id") Long id) {
        try {
            System.out.println("INside update controller ===========================================================================================================");
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd")); // Optional if using date fields
//            UserProxy userProxy = mapper.readValue(userJson, UserProxy.class); // Manually convert JSON string to UserProxy

            return userService.updateUser(userProxy,id); // Call the service method to update user
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

    @PostMapping("/forgot-password/{payload}")
    public ResponseEntity<?> forgotPassword(@PathVariable String payload) {
        System.err.println("controller");
        System.err.println(payload);
        userService.generateResetToken(payload);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Reset link sent");
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/validate-token/{token}")
//    public ResponseEntity<?> validateToken(@PathVariable String token) {
//        boolean valid = userService.validateToken(token);
//        return ResponseEntity.ok(valid);
//    }

    @GetMapping("/validate-token/{token}")
    public ResponseEntity<Boolean> validateToken(@PathVariable String token) {
        boolean valid = userService.validateToken(token);
        return valid ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }


    @PostMapping("/reset-password/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token, @RequestBody ResetPasswordRequest password) {
        System.err.println(password.getPassword());
        userService.resetPassword(token, password.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successful");
        return ResponseEntity.ok(response);
    }




    //download excel sheet
    @GetMapping("/download-users")
    public ResponseEntity<byte[]> getdataFromDbtoExcell() throws IOException{
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usersList.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(userService.downloadUsers().toByteArray());
    }

    @GetMapping("/totalUsers")
    public long totalusers(){
        return userService.countUsers();
    }


        @PostMapping(path="/uploadProfileImage/{userId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<?> updateProfileImage(@PathVariable("userId") Long userId,
                                                    @RequestParam("file") MultipartFile file) throws IOException {
            try {
                System.out.println("Inside controller: Received request to upload profile image.");
                System.out.println("User ID: " + userId);
                System.out.println("File name: " + file.getOriginalFilename());
                System.out.println("File size: " + file.getSize());


                System.out.println("inside controller =======================================================================================================");
                String storedFileName = userService.updateProfileImage(userId, file);
                // Send back the stored file name as part of the response
                System.out.println("Returning response: fileName = " + storedFileName);

                return ResponseEntity.ok(Map.of("fileName", storedFileName));
            } catch (Exception e) {
                e.printStackTrace();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", e.getMessage()));
            }
        }


    @GetMapping("/getProfileImage/{imageName}")
    public byte[] getProfileImage(@PathVariable("imageName") String imageName) throws IOException{
        return userService.getProfileImage(imageName);
    }
    @GetMapping("/getProfileImageById/{id}")
    public void getImageById(@PathVariable("id") Long id) throws IOException{
        userService.getImageById(id);

    }
}
