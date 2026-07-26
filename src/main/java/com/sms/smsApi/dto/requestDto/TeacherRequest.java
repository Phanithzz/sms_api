package com.sms.smsApi.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sms.smsApi.model.enums.TeacherStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.Date;

@Data
public class TeacherRequest {

    @NotBlank(message = "firstName is required")
    private String firstNameEn;

    @NotBlank(message = "lastName is required")
    private String lastNameEn;

    @NotBlank(message = "firstName is required")
    private String firstNameKh;

    @NotBlank(message = "lastName is required")
    private String lastNameKh;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    private String nationalId;
    private TeacherStatus employmentStatus;
    private Integer departmentId;

    private String specialization; //Software Engineering

    private String qualification; //Bachelor of Education (B.Ed.)
    private String sex;
    private String phoneNumber;
    private LocalDate hiredDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dob;
    private BigDecimal salary;
}

