package com.sms.smsApi.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcademicYearOption {

    private Integer academicYearId;

    private String name;
}