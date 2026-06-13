package com.sms.smsApi.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "teachers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    @Id
    @Column(name = "teacher_id", length = 100)
    private String teacherId;

    @Column(name = "user_id", unique = true, nullable = false)
    private Integer userId;

    @Column(name = "full_name_kh")
    private String fullNameKh;

    @Column(name = "full_name_en")
    private String fullNameEn;

    @Column(name = "first_name_kh")
    private String firstNameKh;

    @Column(name = "last_name_kh")
    private String lastNameKh;

    @Column(name = "first_name_en")
    private String firstNameEn;

    @Column(name = "last_name_en")
    private String lastNameEn;

    @Column(name = "sex")
    private String sex;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "national_id")
    private String nationalId;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "hired_date")
    private LocalDate hiredDate;

    @Column(name = "employment_status")
    private String employmentStatus;

    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
