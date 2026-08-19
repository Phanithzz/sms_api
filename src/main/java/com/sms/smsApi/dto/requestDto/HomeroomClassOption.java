package com.sms.smsApi.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeroomClassOption {

    private Integer classId;

    private String classCode;

    private Integer gradeLevel;

    private String shift;

    private String homeroomTeacherId;

    private Integer classroomId;

    private Integer maxCapacity;

    private Integer enrolledCount;

    private Integer availableCapacity;
}
