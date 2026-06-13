package com.sms.smsApi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "parents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parent {


    @Id
    @Column(name = "parent_id", unique = true, nullable = false)
    private String parentId;

    @Column(name = "user_id", unique = true, nullable = false)
    private Integer userId;

    @Column(name = "father_name_kh")
    private String fatherNameKh;

    @Column(name = "father_name_en")
    private String fatherNameEn;

    @Column(name = "father_phone")
    private String fatherPhone;

    @Column(name = "father_job")
    private String fatherJob;

    @Column(name = "father_dob")
    private LocalDate fatherDob;

    @Column(name = "mother_name_kh")
    private String motherNameKh;

    @Column(name = "mother_name_en")
    private String motherNameEn;

    @Column(name = "mother_phone")
    private String motherPhone;

    @Column(name = "mother_job")
    private String motherJob;

    @Column(name = "mother_dob")
    private LocalDate motherDob;

    @Column(name = "current_address")
    private String currentAddress;

    @Column(name = "province")
    private String province;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "first_name_kh")
    private String firstNameKh;

    @Column(name = "last_name_kh")
    private String lastNameKh;

    @Column(name = "first_name_en")
    private String firstNameEn;

    @Column(name = "last_name_en")
    private String lastNameEn;
}