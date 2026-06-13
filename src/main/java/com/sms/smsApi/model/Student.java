package com.sms.smsApi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @Column(name = "student_id", unique = true, nullable = false)
    private String studentId; // e.g., S-2024-0001

    @Column(name = "user_id", unique = true)
    private Integer userId;

    @Column(name = "first_name_en")
    private String studentFirstNameEn;

    @Column(name = "last_name_en")
    private String studentLastNameEn;

    @Column(name = "first_name_kh")
    private String studentFirstNameKh;

    @Column(name = "last_name_kh")
    private String studentLastNameKh;

    @Column(name = "full_name_kh")
    private String fullNameKh;

    @Column(name = "full_name_en")
    private String fullNameEn;

    @Column(length = 10)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "place_of_birth")
    private String placeOfBirth;

    @Column(name = "national_id")
    private String nationalId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "current_address", columnDefinition = "TEXT")
    private String currentAddress;

    private String province;

    @Column(name = "grade_level")
    private Integer gradeLevel;

    @Column(name = "homeroom_class_id")
    private Integer homeroomClassId;

    @Column(name = "enrolled_date")
    private LocalDate enrolledDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(length = 50)
    private String status; // Active, Graduated, Transferred, etc.

    @Column(precision = 4, scale = 2)
    private BigDecimal gpa;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
