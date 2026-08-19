package com.sms.smsApi.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassEnrollmentSummary {

    private Integer classId;

    private String classCode;

    private Integer gradeLevel;

    private String shift;

    private Integer maxCapacity;

    private Integer enrolledCount;

    private Integer availableCapacity;

    private Integer sectionCount;
}