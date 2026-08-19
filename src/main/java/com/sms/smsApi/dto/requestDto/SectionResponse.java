package com.sms.smsApi.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionResponse {

    private Integer sectionId;

    private String sectionCode;

    private Integer subjectId;

    private String homeroomClassId;

    private String teacherId;

    private Integer classroomId;

    private Integer academicYearId;

    private Integer semester;

    private String shift;

    private Integer enrolledCount;

    private Integer maxCapacity;
}