package com.TodayTask.Admin.Panel.serviceImpl;

import com.TodayTask.Admin.Panel.Entity.LoginRequest;
import com.TodayTask.Admin.Panel.Entity.LoginResponse;
import com.TodayTask.Admin.Panel.Entity.UserEntity;
import com.TodayTask.Admin.Panel.authConfig.JwtService;
import com.TodayTask.Admin.Panel.enums.Gender;
import com.TodayTask.Admin.Panel.enums.Role;
import com.TodayTask.Admin.Panel.proxy.UserProxy;
import com.TodayTask.Admin.Panel.repository.UserRepo;
import com.TodayTask.Admin.Panel.service.UserService;
import com.TodayTask.Admin.Panel.util.Helper;
import com.github.javafaker.Faker;
import jakarta.mail.internet.MimeMessage;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AuthenticationManager authmanager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private Helper helper;

    @Autowired
    private JwtService jwtService;


    @Autowired
    private JavaMailSender mailSender;

    private void sendOtpEmail(String email,String otp){
        try{
            MimeMessage mimeMailMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper= new MimeMessageHelper(mimeMailMessage,"utf-8");

            String subject= "Password Reset OTP";
            String content= "Dear User,<br><br>Your OTP for password reset is: <b>" + otp + "</b><br><br>Regards,<br>Radhika Yadav";

            helper.setText(content,true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setFrom("quillist001@gmail.com");
            mailSender.send(mimeMailMessage);


        }catch (Exception ex){
            throw new RuntimeException("Failed to send OTP email", ex);

        }
    }


    public String generateAndSendOtp(String email) {

        UserEntity userEntity= userRepo.findByUserName(email).orElseThrow(()->new RuntimeException("User not found"));
        System.out.println("email from user entity to send otp : "+email+"========================================");
        if(userEntity != null){
            String otp = String.format("%06d", new Random().nextInt(999999));
            System.out.println("Otp is : "+otp);

            UserEntity user=userRepo.findByUserName(email).orElseThrow(()->new RuntimeException("user not found"));
            System.out.println("USer form email is : "+user);

//            user.setOtp(otp);
//            user.setOtpRequestedTime(LocalDateTime.now());
            userRepo.save(user);
            System.out.println("USer after save : "+user);

            sendOtpEmail(email,otp);
            return  otp;
        }else{
            throw new RuntimeException("User not found with email: " + email);
        }
    }



    //varify OTP

    public boolean verifyOtp(String email, String otp) {
//        UserEntity user = userRepo.findByUserName(email).orElseThrow(()->new RuntimeException("user not found"));
//        if (user == null) {
//            throw new RuntimeException("User not found");
//        }
//
//        if (user.getOtp() == null) {
//            throw new RuntimeException("OTP not generated yet");
//        }
//
//        if (user.getOtp().equals(otp)) {
//            // Optional: You can check for expiry here if you want
//            user.setOtp(null);
//            user.setOtpRequestedTime(null);
//            userRepo.save(user);
//            return true;
//        } else {
//            return false;
//        }
        return false;
    }


    @Override
    public List<UserEntity> searchUsers(String keyword) {
        return userRepo.findByNameContainingIgnoreCaseOrUserNameContainingIgnoreCase(keyword, keyword);
    }



    @Override
    public String registerUser(UserProxy userProxy,MultipartFile image) {
        Optional<UserEntity> existingUser = userRepo.findByUserName(userProxy.getUserName());

        if (existingUser.isPresent()) {
            return "User with User Name already exists ....";
        }


        String imageUrl = saveImage(image);


        // Set user details
        UserEntity newUser = new UserEntity();
        newUser.setName(userProxy.getName());
        newUser.setDob(userProxy.getDob());
        newUser.setUserName(userProxy.getUserName());
        newUser.setPassword(passwordEncoder.encode(userProxy.getPassword()));  // Encoding the password
        newUser.setGender(userProxy.getGender());
        newUser.setAddress(userProxy.getAddress());
        newUser.setProfileImage(imageUrl);  // Storing image URL path
        newUser.setContactNumber(userProxy.getContactNumber());
        newUser.setPincode(userProxy.getPincode());
        newUser.setAccessRole(userProxy.getAccessRole());

        // Save user to the repository


        userRepo.save(helper.convert(newUser,UserEntity.class));

        return "Registration successful!";
    }




    private String saveImage(MultipartFile image) {
        // Define the directory where images will be stored
        String uploadDirectory = "C://springBoot/";

        // Create the directory if it doesn't exist
        File dir = new File(uploadDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Generate a unique filename using UUID
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + image.getOriginalFilename();

        // Define the path where the image will be saved
        Path path = Paths.get(uploadDirectory + fileName);

        try {
            // Write the image to the directory
            Files.write(path, image.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return null;  // Return null if there's an error saving the image
        }

        // Return the relative URL for the image
        return "/uploads/images/" + fileName;
    }




    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        System.out.println("login response from emp service called..");
//		Authentication authentication=new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword());
        Authentication authentication=new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword());
        System.err.println("Authentication : "+authentication);

        Authentication verified = authmanager	.authenticate(authentication);

        System.err.println("is varified : "+verified);
        System.err.println(verified.isAuthenticated());
        if(!verified.isAuthenticated())
        {
            //System.out.println("user not found");
            //System.err.println("user not found");
            //throw new BadCredicialException(null, null);
            //throw new BadCredentialsException("Bad credentials...");
            System.out.println("bad credials..");
            //throw new ErrorResponse("bad credentials",404);
        }
        System.out.println("generated token is : "+jwtService.genearteTocken(loginRequest.getUserName()));

        return new LoginResponse(loginRequest.getUserName(),jwtService.genearteTocken(loginRequest.getUserName()),(List<SimpleGrantedAuthority>) verified.getAuthorities());

    }


    public List<UserProxy> getAllUsers(){
        List<UserEntity> allusers = userRepo.findAll();

        return helper.convertList(allusers,UserProxy.class);
    }

    @Override
    public UserProxy getUserById(Long id) {
        UserEntity user = userRepo.findById(id).orElseThrow(()->new RuntimeException("User Not Found"));

        return  helper.convert(user,UserProxy.class);
    }

    @Override
    public Long getUserIdByName(String name) {
        UserEntity user = userRepo.findByUserName(name).orElseThrow(()->new RuntimeException());

        Long userid= user.getId();

        return userid;
    }

    @Override
    public String deleteUser(Long id) {
        userRepo.deleteById(id);
        return "User Deleted Successfully!!!!";
    }



    public UserProxy getuserByUserName(String name){
        UserEntity user = userRepo.findByUserName(name).orElseThrow(()->new RuntimeException("User Not Fount"));

        return helper.convert(user,UserProxy.class);

    }

    @Override
    public String updateUser(UserProxy userProxy,Long id) {
        Optional<UserEntity> user = userRepo.findById(id);
        if(user.isPresent()){
            UserEntity existingUser= user.get();
            existingUser.setUserName(userProxy.getUserName());
            existingUser.setName(userProxy.getName());
            existingUser.setDob(userProxy.getDob());
            existingUser.setPassword(passwordEncoder.encode(userProxy.getPassword())); // Encoding the password
            existingUser.setGender(userProxy.getGender());
            existingUser.setAddress(userProxy.getAddress());
//            existingUser.setProfileImage(userProxy.getProfileImage());  // Update profile image URL
            existingUser.setContactNumber(userProxy.getContactNumber());
            existingUser.setPincode(userProxy.getPincode());
            existingUser.setAccessRole(userProxy.getAccessRole());
//            if (image != null && !image.isEmpty()) {
//                String imageUrl = saveImage(image);
//                existingUser.setProfileImage(imageUrl);
//            }


            // Save the updated user entity in the repository
            userRepo.save(helper.convert(existingUser, UserEntity.class));
        }

        return "User Updated Sucessfully......";
    }


    @Override
    public byte[] getProfileImage(String imageName) throws IOException {
        return Files.readAllBytes(Path.of(getFullPath() + File.separator + imageName));
    }
    private String getFullPath() throws IOException {
        String dynamicPath = new ClassPathResource("").getFile().getAbsolutePath();
        return dynamicPath + File.separator + "resources" + File.separator + "static" + File.separator + "profiles";
    }


    @Override
    public void generateFakeUsers(int count){
        Faker faker = new Faker();
        Gender[] genders = Gender.values();
        Role[] roles = Role.values();
        for(int i =0;i<count;i++){
            UserEntity fakeuser= new UserEntity();

            fakeuser.setName(faker.name().fullName());
            fakeuser.setUserName(faker.name().username());
            fakeuser.setEmail(faker.internet().emailAddress());
            fakeuser.setPassword(passwordEncoder.encode("password123")); // default password
            fakeuser.setDob(faker.date().birthday()); // you can randomize this too
            fakeuser.setGender(genders[new Random().nextInt(genders.length)]);            fakeuser.setAddress(faker.address().fullAddress());
            fakeuser.setProfileImage("/uploads/images/default-profile.png"); // or use a static image
            String contactNumber = faker.number().digits(10); // Adjust length as needed (e.g., 10-digit Indian number)
            fakeuser.setContactNumber(contactNumber);            fakeuser.setPincode(Integer.parseInt(faker.address().zipCode().replaceAll("[^0-9]", "")));
            fakeuser.setAccessRole(roles[new Random().nextInt(roles.length)]);

            userRepo.save(fakeuser);
        }
    }



    @Override
    public void generateResetToken(String email) {
        System.err.println(email);
        UserEntity user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepo.save(user);
        System.out.println("http://localhost:4200/reset-password?token=" + token);
    }
    @Override
    public boolean validateToken(String token) {
        UserEntity user = userRepo.findByResetToken(token).orElse(null);
        return user != null && user.getTokenExpiry().isAfter(LocalDateTime.now());
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        UserEntity user = userRepo.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

//        emp.setPassword(newPassword); // Use encoder in real apps
        user.setResetToken(null);
        user.setTokenExpiry(null);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    @Override
    public List<UserProxy> getAllUser() {
        List<UserEntity> allusers = userRepo.findAll();
     return    helper.convertList(allusers,UserProxy.class);
    }


}
