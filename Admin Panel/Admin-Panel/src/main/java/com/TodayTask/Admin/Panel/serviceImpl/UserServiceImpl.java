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
import com.TodayTask.Admin.Panel.util.downloadExcel;
import com.github.javafaker.Faker;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.beans.Transient;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.TodayTask.Admin.Panel.util.downloadExcel.downloadUsersExcel;

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


    @PersistenceContext
    private EntityManager entityManager;

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
    public String registerUser(UserProxy userProxy, MultipartFile image) {
        try {
            // Basic manual validations
            if (userProxy.getUserName() == null || userProxy.getUserName().trim().isEmpty()) {
                return "Username cannot be blank.";
            }
            if (userProxy.getPassword() == null || userProxy.getPassword().length() < 6) {
                return "Password must be at least 6 characters long.";
            }
            if (userProxy.getEmail() == null || !userProxy.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return "Invalid email format.";
            }
            if (userProxy.getContactNumber() == null || !userProxy.getContactNumber().matches("\\d{10}")) {
                return "Contact number must be 10 digits.";
            }

            // Check if user already exists
            Optional<UserEntity> existingUser = userRepo.findByUserName(userProxy.getUserName());
            if (existingUser.isPresent()) {
                return "User with User Name already exists.";
            }

            // Save image and build entity
            String imageUrl = saveImage(image);

            UserEntity newUser = new UserEntity();
            newUser.setName(userProxy.getName());
            newUser.setDob(userProxy.getDob());
            newUser.setEmail(userProxy.getEmail());

            newUser.setUserName(userProxy.getUserName());
            newUser.setPassword(passwordEncoder.encode(userProxy.getPassword()));
            newUser.setGender(userProxy.getGender());
            newUser.setAddress(userProxy.getAddress());
            newUser.setProfileImage(imageUrl);
            newUser.setContactNumber(userProxy.getContactNumber());
            newUser.setPincode(userProxy.getPincode());
            newUser.setAccessRole(userProxy.getAccessRole());

            // Save user
            userRepo.save(helper.convert(newUser, UserEntity.class));

            return "Registration successful!";
        } catch (Exception e) {
            // Log error (optional: use logger)
            e.printStackTrace();
            return "An error occurred during registration: " + e.getMessage();
        }
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
        try {
            System.out.println("login response from emp service called..");
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword());
            System.err.println("Authentication : " + authentication);

            Authentication verified = authmanager.authenticate(authentication);

            System.err.println("is varified : " + verified);
            System.err.println(verified.isAuthenticated());
            if (!verified.isAuthenticated()) {
                System.out.println("bad credials..");
            }

            System.out.println("generated token is : " + jwtService.genearteTocken(loginRequest.getUserName()));

            return new LoginResponse(
                    loginRequest.getUserName(),
                    jwtService.genearteTocken(loginRequest.getUserName()),
                    (List<SimpleGrantedAuthority>) verified.getAuthorities()
            );
        } catch (Exception e) {
            System.err.println("Exception occurred during login: " + e.getMessage());
            e.printStackTrace();
            return null; // Or handle the exception as per your design (e.g., throw custom exception)
        }
    }

    public Page<UserProxy> getAllUsers(int page, int size) {
        try {
            PageRequest pageable = PageRequest.of(page, size);
            Page<UserEntity> userPage = userRepo.findAll(pageable);

            List<UserProxy> userProxyList = helper.convertList(userPage.getContent(), UserProxy.class);

            return new PageImpl<>(userProxyList, pageable, userPage.getTotalElements());
        } catch (Exception e) {
            System.err.println("Exception occurred while fetching users: " + e.getMessage());
            e.printStackTrace();
            return Page.empty(); // Or return null / custom fallback as per your design
        }
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

//    @Override
//    public String deleteUser(Long id) {
////        userRepo.deleteById(id);
//        UserEntity user = new UserEntity();
//        user.setIsdeleted(true);
//        user.setIsactive(false);
//        userRepo.save(user);
//        return "User Deleted Successfully!!!!";
//    }

@Override
@Transactional
public String deleteUser(Long id) {
    Optional<UserEntity> existingUser = userRepo.findById(id);
    System.out.println("esisting user is ; "+existingUser);
    System.out.println("existing user is pressent : "+existingUser.isPresent());
    if (existingUser.isPresent()) {

        UserEntity user = existingUser.get();

        user.setIsDeleted(true);
        System.out.println("if user is deleted : "+user.getIsDeleted());
        user.setIsActive(false);

        return "User marked as deleted successfully!";
    } else {
        return "User not found!";
    }
}

public List<UserEntity> isDeleted(){
    List<UserEntity> deletedUsers = userRepo.findByIsDeletedTrue();
    return deletedUsers;
}

//public List<UserEntity> nonDeleted(){
//    List<UserEntity> nonDeletedUsers=userRepo.findByIsDeletedFalse();
//    return  nonDeletedUsers;
//}

    public Page<UserProxy> getNonDeletedUsers(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<UserEntity> nonDeletedUserPage = userRepo.findByIsDeletedFalse(pageable);
        List<UserProxy> userProxyList = helper.convertList(nonDeletedUserPage.getContent(), UserProxy.class);
        return new PageImpl<>(userProxyList, pageable, nonDeletedUserPage.getTotalElements());
    }




    public List<UserEntity> getActiveUsers() {
        return userRepo.findByIsDeletedFalseAndIsActiveTrue();
    }






    public UserProxy getuserByUserName(String name){
        UserEntity user = userRepo.findByUserName(name).orElseThrow(()->new RuntimeException("User Not Fount"));

        return helper.convert(user,UserProxy.class);

    }



    @Override
    public String updateUser(UserProxy userProxy, Long id) {
        Optional<UserEntity> userOpt = userRepo.findById(id);

        if (userOpt.isPresent()) {
            UserEntity existingUser = userOpt.get();

            if (userProxy.getUserName() != null)
                existingUser.setUserName(userProxy.getUserName());

            if (userProxy.getName() != null)
                existingUser.setName(userProxy.getName());

            if (userProxy.getDob() != null)
                existingUser.setDob(userProxy.getDob());

            if (userProxy.getEmail() != null)
                existingUser.setEmail(userProxy.getEmail());

            // if (userProxy.getPassword() != null)
            //     existingUser.setPassword(passwordEncoder.encode(userProxy.getPassword()));

            if (userProxy.getGender() != null)
                existingUser.setGender(userProxy.getGender());

            if (userProxy.getAddress() != null)
                existingUser.setAddress(userProxy.getAddress());

            if (userProxy.getContactNumber() != null)
                existingUser.setContactNumber(userProxy.getContactNumber());

            if (userProxy.getPincode() != null)
                existingUser.setPincode(userProxy.getPincode());

            if (userProxy.getAccessRole() != null)
                existingUser.setAccessRole(userProxy.getAccessRole());

            userRepo.save(existingUser);
            return "User updated successfully.";
        }

        return "User not found.";
    }



    public void getImageById(Long id) throws IOException {
        UserEntity user = userRepo.findById(id).orElseThrow();
        String profile =user.getProfileImage();
        getProfileImage(profile);
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
    @Transactional
    public String updateProfileImage(Long userId, MultipartFile file) throws IOException{

        System.out.println("++==============================================inservice imp l=====================================================");
        System.out.println("Starting updateProfileImage for user: " + userId);
        System.out.println("File received: " + file.getOriginalFilename() + ", size: " + file.getSize());


        UserEntity user=userRepo.findById(userId).orElseThrow(()->new RuntimeException("user not found"));
        System.out.println("Found user with ID: " + userId + ", current profileImage: " + user.getProfileImage());

        String uploadPath=getFullPath();
        File folder=new File(uploadPath);
        if(!folder.exists()){
            folder.mkdirs();
        }
        String fileName=System.currentTimeMillis()+"_"+file.getOriginalFilename();
        Path filePath=Path.of(uploadPath,fileName);
        System.out.println("Attempting to write file to: " + filePath);

        Files.write(filePath,file.getBytes());
        // Update database
        user.setProfileImage(fileName);
        userRepo.save(user);

        entityManager.flush();
        return  fileName;

    }


    public long countUsers(){

        List<UserEntity> users= userRepo.findAll();
       long totalusers = users.stream().count();
        System.out.println("Total users : "+totalusers);

        return totalusers;
    }
    @Override
    public void generateFakeUsers(int count) {
        Faker faker = new Faker();
        Gender[] genders = Gender.values();
        Role[] roles = Role.values();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            UserEntity fakeUser = new UserEntity();

            fakeUser.setName(faker.name().fullName());
            fakeUser.setUserName(faker.name().username());
            fakeUser.setEmail(faker.internet().emailAddress());
            fakeUser.setPassword(passwordEncoder.encode("password123"));
            fakeUser.setDob(faker.date().birthday());
            fakeUser.setGender(genders[random.nextInt(genders.length)]);
            fakeUser.setAddress(faker.address().fullAddress());
            fakeUser.setProfileImage("default-profile.png");

            // Ensure 10-digit number
            String contactNumber = faker.number().digits(10);
            fakeUser.setContactNumber(contactNumber);

            // Ensure valid 6-digit pin (within 100000–999999)
            int validPin = 100000 + random.nextInt(900000);
            fakeUser.setPincode(validPin);

            fakeUser.setAccessRole(roles[random.nextInt(roles.length)]);

            // Explicitly set these fields
            fakeUser.setIsActive(true);
            fakeUser.setIsDeleted(false);

            userRepo.save(fakeUser);
        }
    }



    @Override
    public void generateResetToken(String email) {
        System.err.println(email);
        UserEntity user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(30));
        System.out.println("befor save reset token "+token);
        userRepo.save(user);
        System.out.println("after save reset token : "+userRepo.findByEmail(email).get().getResetToken());
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

//        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
//            throw new RuntimeException("Token expired");
//        }

        if (user.getTokenExpiry() == null || user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired or already used");
        }

//        emp.setPassword(newPassword); // Use encoder in real apps
        user.setResetToken(null);
        user.setTokenExpiry(null);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

//    @Override
//    public List<UserProxy> getAllUser() {
//        List<UserEntity> allusers = userRepo.findAll();
//     return    helper.convertList(allusers,UserProxy.class);
//    }





    //download excel file
    @Override
    public  ByteArrayOutputStream downloadUsers() throws IOException {

        List<UserEntity> usrs = userRepo.findAll();

        ByteArrayOutputStream  in =    downloadExcel.downloadUsersExcel(usrs);
        return in;

    }

    public List<UserEntity> getIsDeletedFalseActicveTrue(){
    List<UserEntity> nonDeletedAndActiveUsers= userRepo.findByIsDeletedFalseAndIsActiveTrue();
    return  nonDeletedAndActiveUsers;
    }



    }



