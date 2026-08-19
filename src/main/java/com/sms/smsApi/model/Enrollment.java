package com.sms.smsApi.model;

import com.sms.smsApi.model.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    private Integer enrollmentId;
    private String studentId;
    private Integer sectionId;
    private LocalDate enrolledDate;
    private EnrollmentStatus status;
}