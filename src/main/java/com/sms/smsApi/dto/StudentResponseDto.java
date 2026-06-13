package com.sms.smsApi.dto;

import com.sms.smsApi.model.HomeroomClass;
import com.sms.smsApi.model.enums.StudentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

public class StudentResponseDto {
    private String studentId;
    private Long userId;
    private String studentFirstNameEn;
    private String studentLastNameEn;
    private String studentFullNameEn;
    private String studentFirstNameKh;
    private String studentLastNameKh;
    private String studentFullNameKh;
    private Date dob;
    private String gender;
    @Column(name = "national_id", unique = true)
    private String nationalId;

    private String phoneNumber;

    private String currentAddress;

    private String province;

    private Integer gradeLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homeroom_class_id")
    private HomeroomClass homeroomClass;

    private LocalDate enrolledDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @Column(precision = 5, scale = 2)
    private BigDecimal averageScore;

    private String profilePhoto;

    private String emergencyContactName;

    private String emergencyContactPhone;

    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
}
