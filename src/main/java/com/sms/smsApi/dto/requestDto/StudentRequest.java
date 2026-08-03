package com.sms.smsApi.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sms.smsApi.model.enums.StudentStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequest {

    @NotBlank(message = "First name (English) is required")
    private String firstNameEn;

    @NotBlank(message = "Last name (English) is required")
    private String lastNameEn;

    private String firstNameKh;

    private String lastNameKh;

    private String fullNameEn;

    private String fullNameKh;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Gender is required")
    private String gender;

    @Past(message = "Date of birth must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String placeOfBirth;

    private String nationalId;

    @Pattern(
            regexp = "^\\+?[0-9]{8,15}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;

    private String currentAddress;

    private String province;

    @Min(value = 1, message = "Grade level must be greater than 0")
    private Integer gradeLevel;

    private Integer homeroomClassId;

    private LocalDate enrolledDate;

    private LocalDate endDate;

    private StudentStatus status;

    @Digits(integer = 2, fraction = 2, message = "GPA must have at most 2 decimal places")
    @DecimalMin(value = "0.00", message = "GPA cannot be negative")
    @DecimalMax(value = "4.00", message = "GPA cannot exceed 4.00")
    private BigDecimal gpa;

    private String profilePhoto;

    private String emergencyContactName;

    @Pattern(
            regexp = "^\\+?[0-9]{8,15}$",
            message = "Invalid emergency contact phone number"
    )
    private String emergencyContactPhone;
}
