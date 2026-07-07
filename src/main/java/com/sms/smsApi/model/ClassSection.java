package com.sms.smsApi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassSection {

    private Integer sectionId;
    private String sectionCode;
    private Integer subjectId;
    private Integer homeroomClassId;
    private Integer teacherId;
    private Integer classroomId;
    private Integer academicYearId;
    private Integer semester;
    private String shift;
    private Integer enrolledCount;
    private Integer maxCapacity;
}