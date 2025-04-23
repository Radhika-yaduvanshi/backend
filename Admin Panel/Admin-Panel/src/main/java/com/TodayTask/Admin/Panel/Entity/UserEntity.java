package com.TodayTask.Admin.Panel.Entity;


import com.TodayTask.Admin.Panel.enums.Gender;
import com.TodayTask.Admin.Panel.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Gender gender;
    private String address;
    private  String profileImage;
    private Long contactNumber;
    private Integer pincode;
    @Enumerated(EnumType.STRING)
    private Role accessRole;
    private String password;
}
