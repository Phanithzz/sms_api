package com.sms.smsApi.dto.requestDto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentRequestFilter {
    // search
    private String keyword;

    // filters
    private String province;
    private String gender;
    private String fullNameKh;
    private Integer gradeLevel;
    private Long classId;

    private LocalDate enrolledFrom;
    private LocalDate enrolledTo;
    // pagination
    private int page = 0;
    private int size = 10;

    // sorting
    private String sortBy = "id";
    private String direction = "asc";
}
