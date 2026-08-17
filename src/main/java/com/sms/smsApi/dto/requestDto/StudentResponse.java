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
    private Integer userId;
    private String firstNameEn;

    private String lastNameEn;

    private String firstNameKh;

    private String lastNameKh;

    private String fullNameEn;

    private String fullNameKh;
    //private String email;

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

    private String status;
    private BigDecimal gpa;

    private String profilePhoto;

    private String emergencyContactName;

    private String emergencyContactPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;


}
