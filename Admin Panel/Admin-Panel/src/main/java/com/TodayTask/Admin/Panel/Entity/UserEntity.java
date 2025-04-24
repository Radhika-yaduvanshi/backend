package com.TodayTask.Admin.Panel.Entity;


import com.TodayTask.Admin.Panel.enums.Gender;
import com.TodayTask.Admin.Panel.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Date dob;
    private String userName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String address;
    @Column(nullable = true)
    private  String profileImage;
    private String contactNumber;
    private Integer pincode;
    @Enumerated(EnumType.STRING)
    private Role accessRole;
    private String password;
    @Column(name = "otp")
    private String otp;

    @Column(name = "otp_requested_time")
    private LocalDateTime otpRequestedTime;
}
