package com.sms.smsApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYearResponse {
    private Long id;
    private String yearName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean current;
    private LocalDateTime createdAt;
}
