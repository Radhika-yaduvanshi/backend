package com.TodayTask.Admin.Panel.repository;

import com.TodayTask.Admin.Panel.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity,Long> {
//    public UserEntity findByUserName(String name);
//    public UserEntity findByEmail(String email);
Optional<UserEntity> findByUserName(String name);


}
