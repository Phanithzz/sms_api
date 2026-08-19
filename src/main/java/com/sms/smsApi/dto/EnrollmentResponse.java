package com.sms.smsApi.dto;

import com.sms.smsApi.dto.requestDto.SectionResponse;
import com.sms.smsApi.model.enums.EnrollmentStatus;
import lombok.Data;

import java.security.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
public class EnrollmentResponse {

    private Integer enrollmentId;
    private String studentId;
    private EnrollmentStatus status;
    private Integer homeroomClassId;
    private Integer academicYearId;
    private LocalDate enrolledAt;
    private List<SectionResponse> sections;
    // getters setters
}
