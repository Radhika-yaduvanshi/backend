package com.TodayTask.Admin.Panel.serviceImpl;

import com.TodayTask.Admin.Panel.Entity.UserEntity;
import com.TodayTask.Admin.Panel.proxy.UserProxy;
import com.TodayTask.Admin.Panel.repository.UserRepo;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepo userRepo;
    @Mock
    MultipartFile image;


    @Test
    void registerUser(){
        System.out.println("First test ");
        UserEntity user = new UserEntity();
        UserProxy userProxy=new UserProxy();
        userService.registerUser(userProxy, image);
    }

}