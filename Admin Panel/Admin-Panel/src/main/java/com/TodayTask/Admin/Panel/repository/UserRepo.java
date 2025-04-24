package com.TodayTask.Admin.Panel.repository;

import com.TodayTask.Admin.Panel.Entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity,Long> {
//    public UserEntity findByUserName(String name);
//    public UserEntity findByEmail(String email);
Optional<UserEntity> findByUserName(String name);
    List<UserEntity> findByNameContainingIgnoreCaseOrUserNameContainingIgnoreCase(String name, String userName);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByResetToken(String resetToken);


}
