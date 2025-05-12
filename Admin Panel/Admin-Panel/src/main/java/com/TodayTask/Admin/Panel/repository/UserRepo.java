package com.TodayTask.Admin.Panel.repository;

import com.TodayTask.Admin.Panel.Entity.UserEntity;
import com.TodayTask.Admin.Panel.enums.Role;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity,Long> {
//    public UserEntity findByUserName(String name);
//    public UserEntity findByEmail(String email);
Optional<UserEntity> findByUserName(String name);
    List<UserEntity> findByNameContainingIgnoreCaseOrUserNameContainingIgnoreCase(String name, String userName);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByResetToken(String resetToken);
    // This query will now work after renaming 'isdeleted' to 'isDeleted'
//    List<UserEntity> findByIsDeletedFalseAndIsActiveTrue();
    List<UserEntity> findByIsDeletedTrue();
//    List<UserEntity>findByIsDeletedFalse();
Page<UserEntity> findByIsDeletedFalse(Pageable pageable);
    Page<UserEntity> findByIsDeletedTrue(Pageable pageable);

    Page<UserEntity> findByAccessRole(Pageable pageable, Role role);



        @Query("SELECT u FROM UserEntity u WHERE u.isDeleted=false AND( " +
                "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "LOWER(u.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "STR(u.gender) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "STR(u.dob) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        List<UserEntity> searchUsers(@Param("keyword") String keyword);







}
