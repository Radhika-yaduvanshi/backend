package com.TodayTask.Admin.Panel.proxy;

import com.TodayTask.Admin.Panel.enums.Gender;
import com.TodayTask.Admin.Panel.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;



@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserProxy {
    private Long id;
    private String name;
    private Date dob;
    private String userName;
    private String email;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String address;

    private  String profileImage;
    private String contactNumber;
    private Integer pincode;

    private Role accessRole;
    private String password;
    private String resetToken;
    private LocalDateTime tokenExpiry;

}
