package com.sms.smsApi.dto.requestDto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequest {

    @NotNull
    private Integer sectionId;

    @NotNull
    private Integer homeroomClassId;

    @NotNull
    private Integer classroomId;

    @NotNull
    private Integer academicYearId;

    @NotBlank
    private String dayOfWeek;

    @NotNull
    @Positive
    private Integer periodNumber;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;
}
