package com.sms.smsApi.dto;

import com.sms.smsApi.model.enums.EnrollmentStatus;
import lombok.Data;

@Data
public class EnrollmentResponse {

    private Integer enrollmentId;
    private String studentId;
    private Integer sectionId;
    private EnrollmentStatus status;

    // getters setters
}
