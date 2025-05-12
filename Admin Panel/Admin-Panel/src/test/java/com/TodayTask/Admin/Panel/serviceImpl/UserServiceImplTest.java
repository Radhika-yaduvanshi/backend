package com.TodayTask.Admin.Panel.serviceImpl;

import com.TodayTask.Admin.Panel.Entity.LoginRequest;
import com.TodayTask.Admin.Panel.Entity.LoginResponse;
import com.TodayTask.Admin.Panel.Entity.UserEntity;
import com.TodayTask.Admin.Panel.authConfig.JwtService;
import com.TodayTask.Admin.Panel.enums.Gender;
import com.TodayTask.Admin.Panel.enums.Role;
import com.TodayTask.Admin.Panel.proxy.UserProxy;
import com.TodayTask.Admin.Panel.repository.UserRepo;
import com.TodayTask.Admin.Panel.util.Helper;
import com.TodayTask.Admin.Panel.util.downloadExcel;
import org.apache.catalina.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)//==>for mock data
//@SpringBootTest ===>this is used when we are using @Autowire
class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepo userRepo;
    @Mock
    MultipartFile image;

    @Mock
    private AuthenticationManager authmanager;

    @Mock
    private downloadExcel downloadExcel;

    @Mock
    JwtService jwtService;

    @Mock
    Helper helper;

    private MockMultipartFile image1;

    private UserProxy userProxy;


    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void testLogin() {

        LoginRequest request = new LoginRequest("testuser", "testpass");

        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "testpass", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(authmanager.authenticate(any())).thenReturn(authentication);
        when(jwtService.genearteTocken("testuser")).thenReturn("dummy-token");

        LoginResponse response = userService.login(request);
        assertNotNull(response);
        assertEquals("testuser", response.getUserName());
        assertEquals("dummy-token", response.getToken());
        assertEquals(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")), response.getAccessRole());
    }


    @Test
    void testGetAllUsers() {
        // Arrange
        int page = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size);

        List<UserEntity> userEntities = List.of(
                new UserEntity(1L, "radhika", new Date(), "radhika123", "radhika@example.com", Gender.FEMALE,
                        "123 Street", "img1.jpg", "1234567890", 123456, Role.USER, "pass123", null, null, false),
                new UserEntity(2L, "miska", new Date(), "bob321", "miska@example.com", Gender.MALE,
                        "456 Avenue", "img2.jpg", "0987654321", 654321, Role.ADMIN, "pass321", null, null, false)
        );
        Page<UserEntity> userEntityPage = new PageImpl<>(userEntities, pageable, userEntities.size());

        List<UserProxy> userProxies = List.of(
                new UserProxy(1L, "radhika", new Date(), "radhika123", "radhika@example.com", Gender.FEMALE,
                        "123 Street", "img1.jpg", "1234567890", 123456, Role.USER, "pass123", null, null, null, false),
                new UserProxy(2L, "miska", new Date(), "bob321", "miska@example.com", Gender.MALE,
                        "456 Avenue", "img2.jpg", "0987654321", 654321, Role.ADMIN, "pass321", null, null, null, false)
        );

        when(userRepo.findAll(pageable)).thenReturn(userEntityPage);
        when(helper.convertList(userEntities, UserProxy.class)).thenReturn(userProxies);

        Page<UserProxy> result = userService.getAllUsers(page, size);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("radhika", result.getContent().get(0).getName());
        assertEquals("miska", result.getContent().get(1).getName());

        verify(userRepo, times(1)).findAll(pageable);
        verify(helper, times(1)).convertList(userEntities, UserProxy.class);
    }

@Test
void generateToken(){
        String email="radhika@gmail.com";
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail(email);
        user.setUserName("radhika");

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepo.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0)); // mock save behavior
        userService.generateResetToken(email);

    assertNotNull(user.getResetToken());
    assertNotNull(user.getTokenExpiry());
    assertTrue(user.getTokenExpiry().isAfter(LocalDateTime.now()));

}

//get user by id
@Test
void testGetUserById_Success() {
    // Arrange
    Long userId = 1L;
    UserEntity userEntity = new UserEntity(
            userId, "Test User", new Date(), "testuser", "test@example.com", Gender.MALE,
            "123 Test St", "profile.jpg", "1234567890", 123456, Role.USER,
            "password", null, null, false
    );

    UserProxy userProxy = new UserProxy(
            userId, "Test User", new Date(), "testuser", "test@example.com", Gender.MALE,
            "123 Test St", "profile.jpg", "1234567890", 123456, Role.USER,
            "password", null, null, null, false
    );

    when(userRepo.findById(userId)).thenReturn(java.util.Optional.of(userEntity));
    when(helper.convert(userEntity, UserProxy.class)).thenReturn(userProxy);

    UserProxy result = userService.getUserById(userId);

    assertNotNull(result);
    assertEquals("Test User", result.getName());
    assertEquals("testuser", result.getUserName());
    verify(userRepo, times(1)).findById(userId);
    verify(helper, times(1)).convert(userEntity, UserProxy.class);
}

@Test
public void testUserByName(){

        String userName="Radhika";

            UserEntity userEntity = new UserEntity(
            1L, "Test User", new Date(), userName, "test@example.com", Gender.MALE,
            "123 Test St", "profile.jpg", "1234567890", 123456, Role.USER,
            "password", null, null, false
    );

    UserProxy userProxy = new UserProxy(
            1L, "Test User", new Date(), userName, "test@example.com", Gender.MALE,
            "123 Test St", "profile.jpg", "1234567890", 123456, Role.USER,
            "password", null, null, null, false
    );

    when(userRepo.findByUserName(userName)).thenReturn(java.util.Optional.of(userEntity));
    when((helper.convert(userEntity,UserProxy.class))).thenReturn(userProxy);

    UserProxy result = userService.getuserByUserName(userName);
    assertNotNull(result);
    assertEquals(userName,result.getUserName());

}

@Test
public void testDeleteUser(){

        Long userId =1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUserName("Radhika");
        userEntity.setEmail("radhikay628@gmail.com");

        userEntity.setIsDeleted(true);
        when(userRepo.findById(userId)).thenReturn(Optional.of(userEntity));
        String result= userService.deleteUser(userId);

        assertEquals("User marked as deleted successfully!",result);
        assertTrue(userEntity.getIsDeleted());

}

    @Test
    void testRegisterUser() {
        UserProxy userProxy = new UserProxy(null, "radhika", new Date(90, 0, 1), "radhika", "radhika@example.com",
                Gender.MALE, "123 Main St", "profile.jpg", "1234567890", 123456, Role.USER, "password123", null, null, true, false);
        MockMultipartFile image = new MockMultipartFile("file", "profile.jpg", "image/jpeg", new byte[0]);

        when(userRepo.findByUserName("radhika")).thenReturn(Optional.empty());//Optional.empty for check user not exist yet.
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(helper.convert(any(UserEntity.class), eq(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = userService.registerUser(userProxy, image);

        assertEquals("Registration successful!", result);
        verify(userRepo).save(any(UserEntity.class));
    }


    @Test
    void testUpdateUser() {
        // Arrange
        Long userId = 1L;
        UserEntity existingUser = new UserEntity();
        existingUser.setId(userId);
        existingUser.setUserName("old_name");
        existingUser.setEmail("old@example.com");

        UserProxy updatedProxy = new UserProxy();
        updatedProxy.setUserName("new_name");
        updatedProxy.setEmail("new@example.com");

        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepo.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        String result = userService.updateUser(updatedProxy, userId);

        assertEquals("User updated successfully.", result);
        verify(userRepo).save(existingUser);
        assertEquals("new_name", existingUser.getUserName());
        assertEquals("new@example.com", existingUser.getEmail());
    }




}






