package com.TodayTask.Admin.Panel.proxy;

import com.TodayTask.Admin.Panel.enums.Gender;
import com.TodayTask.Admin.Panel.enums.Role;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;



@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserProxy {
    private Long id;
    private String name;
    private Date dob;
    private String userName;
    private Gender gender;
    private String address;
    private  String profileImage;
    private Long contactNumber;
    private Integer pincode;
    private Role accessRole;
    private String password;

}
