package com.TodayTask.Admin.Panel.service;

import com.TodayTask.Admin.Panel.Entity.LoginRequest;
import com.TodayTask.Admin.Panel.Entity.LoginResponse;
import com.TodayTask.Admin.Panel.Entity.UserEntity;
import com.TodayTask.Admin.Panel.proxy.UserProxy;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    public String registerUser(UserProxy userProxy, MultipartFile image);
    public LoginResponse login(LoginRequest loginRequest);
//    public List<UserProxy> getAllUsers();
    public UserProxy getUserById(Long id);
    public Long getUserIdByName(String name);

    public String deleteUser(Long id);
    public String updateUser(UserProxy userProxy,Long id);

    public byte[] getProfileImage(String imageName) throws IOException;
    public void generateFakeUsers(int count);
    public List<UserEntity> searchUsers(String keyword);
    public boolean validateToken(String token);
    public void generateResetToken(String email);
    public void resetPassword(String token, String newPassword);

    public Page<UserProxy> getAllUsers(int page , int size);


}
