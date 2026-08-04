package com.sms.smsApi.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sms.smsApi.model.enums.StudentStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
    private String studentId;
    private String userId;
    private String firstNameEn;

    private String lastNameEn;

    private String firstNameKh;

    private String lastNameKh;

    private String fullNameEn;

    private String fullNameKh;
    private String email;

    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String placeOfBirth;

    private String nationalId;

    @Pattern(
            regexp = "^\\+?[0-9]{8,15}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;

    private String currentAddress;

    private String province;

    private Integer gradeLevel;

    private Integer homeroomClassId;

    private LocalDate enrolledDate;

    private LocalDate endDate;

    private StudentStatus status;
    private BigDecimal gpa;

    private String profilePhoto;

    private String emergencyContactName;

    private String emergencyContactPhone;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public StudentResponse(String studentId, Integer userId, String studentFirstNameEn,
                           String studentLastNameEn, String studentFirstNameKh, String studentLastNameKh,
                           String fullNameEn, String fullNameKh, String gender, LocalDate dateOfBirth,
                           String placeOfBirth, String nationalId, String phoneNumber, String currentAddress,
                           String province, Integer gradeLevel, Integer homeroomClassId, LocalDate enrolledDate,
                           LocalDate endDate, String status, BigDecimal gpa, String profilePhoto, String emergencyContactName, String emergencyContactPhone, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
    }
}
