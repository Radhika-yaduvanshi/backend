package com.TodayTask.Admin.Panel.Entity;


import com.TodayTask.Admin.Panel.enums.Gender;
import com.TodayTask.Admin.Panel.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    private Date  dob;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotBlank(message = "Address is required")
    private String address;

    @Column(nullable = true)
    private String profileImage;

    @NotBlank(message = "Contact number is mandatory")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    private String contactNumber;

    @NotNull(message = "Pincode is required")
    @Min(value = 100000, message = "Pincode must be at least 6 digits")
    @Max(value = 999999, message = "Pincode must be at most 6 digits")
    private Integer pincode;

    @NotNull(message = "Access role is required")
    @Enumerated(EnumType.STRING)
    private Role accessRole;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 3, message = "Password must be at least 6 characters")
    private String password;

    private String resetToken;

    private LocalDateTime tokenExpiry;

//    @Column(name = "is_active")
//    private Boolean isActive =true;



    @Column(name = "is_deleted")
    private Boolean isDeleted=false ;



    // @Column(name = "otp")
    // private String otp;
    //
    // @Column(name = "otp_requested_time")
    // private LocalDateTime otpRequestedTime;
}
