package com.sms.smsApi.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {

    private Integer scheduleId;

    private Integer sectionId;
    private String sectionCode;

    private Integer subjectId;
    private String subjectCode;
    private String subjectNameEn;
    private String subjectNameKh;

    private Integer homeroomClassId;
    private String classCode;
    private Integer gradeLevel;

    private String teacherId;
    private String teacherName;

    private Integer classroomId;
    private String classroomName;

    private Integer academicYearId;

    private String dayOfWeek;
    private Integer periodNumber;

    private LocalTime startTime;
    private LocalTime endTime;
}