package com.sms.smsApi.dto.requestDto;

import lombok.Data;

@Data
public class EnrollmentRequest {

    private String studentId;
    private Integer sectionId;

}