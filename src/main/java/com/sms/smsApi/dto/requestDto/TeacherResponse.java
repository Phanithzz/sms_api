package com.sms.smsApi.dto.requestDto;

import com.sms.smsApi.model.enums.TeacherStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponse {
    private String teacherId;
    private Integer userId;

    private String fullNameKh;
    private String fullNameEn;
    private String firstNameEn;
    private String lastNameEn;
    private String firstNameKh;
    private String lastNameKh;
    private String sex;
    private Date dateOfBirth;

    private String phoneNumber;
    private String email;
    private String nationalId;

    private Integer departmentId;

    private String specialization;
    private String qualification;

    private LocalDate hiredDate;
    private TeacherStatus employmentStatus;

    private BigDecimal salary;
    private String profilePhoto;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
