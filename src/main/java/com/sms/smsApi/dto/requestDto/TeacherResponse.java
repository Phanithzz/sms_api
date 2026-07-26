package com.sms.smsApi.dto.requestDto;

import com.sms.smsApi.model.enums.TeacherStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponse {
    private String teacherId;
    //private String employeeNumber;
    private String firstNameEn;
    private String lastNameEn;
    private String email;
    private String phoneNumber;
    private LocalDate hiredDate;
    private TeacherStatus employmentStatus;
    private LocalDateTime createdAt;
}
