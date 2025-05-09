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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)//==>for mock data
//@SpringBootTest ===>this is used when we are using @Autowire
class UserServiceImplTest {
    @Spy
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


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(); // or inject mocks
    }

    @Test
    void testRegisterUser_success() {
        // Arrange
        UserProxy userProxy = new UserProxy();
        userProxy.setUserName("testUser");
        userProxy.setPassword("password123");
        userProxy.setEmail("test@example.com");
        userProxy.setContactNumber("1234567890");
        userProxy.setGender(Gender.MALE);
        userProxy.setName("Test User");
        userProxy.setAddress("123 Test St");
        userProxy.setDob(new Date());
        userProxy.setPincode(123456);
        userProxy.setAccessRole(Role.USER);

        // Create a mock image file
        MultipartFile image = new MockMultipartFile("file", "test.jpg", "image/jpeg", "dummy".getBytes());

        // Setup mocks
        when(userRepo.findByUserName("testUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // This will mimic saving the image
        doReturn("http://dummy-image-url.com/image.jpg").when(userService).saveImage(any(MultipartFile.class));

        // Mimic conversion returning the same entity
        when(helper.convert(any(UserEntity.class), eq(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Simulate saving user in DB
        when(userRepo.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String result = userService.registerUser(userProxy, image);

        // Assert
        assertEquals("Registration successful!", result);
        verify(userRepo, times(1)).save(any(UserEntity.class));
    }


    @Test
    public void login(){
        LoginRequest req=new LoginRequest("username","password");
        Authentication auth=new UsernamePasswordAuthenticationToken("username","password", List.of(new SimpleGrantedAuthority("ADMIN")));

        when(authmanager.authenticate(any(Authentication.class))).thenReturn(auth);
        when(jwtService.genearteTocken("username")).thenReturn("my-token");

        LoginResponse loginResponse=userService.login(req);

        assertNotNull(loginResponse);
        assertEquals("username",loginResponse.getUserName());
        assertEquals("my-token",loginResponse.getToken());
        assertTrue(loginResponse.getAccessRole().contains(new SimpleGrantedAuthority("ADMIN")));
    }

    @Test
    public void getAllUSers(){

        int page=0;
        int size=2;

        UserEntity user1 = new UserEntity(
                1L, "radhika", new Date(), "radhika123", "radhika@example.com", Gender.FEMALE, "123 Street",
                null, "1234567890", 123456, Role.USER, "password123", null, null, false
        );

        UserEntity user2 = new UserEntity(
                2L, "priti", new Date(), "priti456", "priti@example.com", Gender.MALE, "456 Avenue",
                null, "0987654321", 654321, Role.ADMIN, "password456", null, null, false
        );

        List<UserEntity>entities=List.of(user1,user2);
        Page<UserEntity> mockPage = new PageImpl<>(entities, PageRequest.of(page, size), 2);

        UserProxy proxy1 = new UserProxy(
                1L, "radhika", user1.getDob(), "radhika123", "radhika@example.com", Gender.FEMALE,
                "123 Street", "img1.jpg", "1234567890", 123456, Role.USER,
                "password123", "token1", user1.getTokenExpiry(), true, false
        );

        UserProxy proxy2 = new UserProxy(
                2L, "priti", user2.getDob(), "priti456", "priti@example.com", Gender.MALE,
                "456 Avenue", "img2.jpg", "0987654321", 654321, Role.ADMIN,
                "password456", "token2", user2.getTokenExpiry(), true, false
        );
        List<UserProxy> proxyList = List.of(proxy1, proxy2);

        when(userRepo.findAll(PageRequest.of(page, size))).thenReturn(mockPage);
        when(helper.convertList(entities, UserProxy.class)).thenReturn(proxyList);

        Page<UserProxy> result = userService.getAllUsers(page, size);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("radhika", result.getContent().get(0).getName());
        assertEquals("priti", result.getContent().get(1).getName());
        assertEquals("password123", result.getContent().get(0).getPassword()); // Be careful with sensitive data
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


    @Test
    public void downloadUsers() throws IOException {
        // Setup the mock user data
        UserEntity user1 = new UserEntity(
                1L, "radhika", new Date(), "radhika123", "radhika@example.com", Gender.FEMALE,
                "123 Street", null, "1234567890", 123456, Role.USER, "password123",
                null, null, false
        );

        UserEntity user2 = new UserEntity(
                2L, "priti", new Date(), "priti456", "priti@example.com", Gender.MALE,
                "456 Avenue", null, "0987654321", 654321, Role.ADMIN, "password456",
                null, null, false
        );

        List<UserEntity> users = List.of(user1, user2);

        ByteArrayOutputStream dummyStream = new ByteArrayOutputStream();
        dummyStream.write("Dummy Excel Content".getBytes());

        when(userRepo.findAll()).thenReturn(users);
        when(downloadExcel.downloadUsersExcel(users)).thenReturn(dummyStream);

        ByteArrayOutputStream result = userService.downloadUsers();

        assertNotNull(result);
        assertEquals("Dummy Excel Content", result.toString());
        verify(userRepo, times(1)).findAll();
        verify(downloadExcel, times(1)).downloadUsersExcel(users);
    }



    //short cut->alt+inset
    @AfterEach
    void tearDown() {
        System.out.println("tear down");

    }


    }






