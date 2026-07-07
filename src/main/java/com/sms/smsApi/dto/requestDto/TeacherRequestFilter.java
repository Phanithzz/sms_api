package com.sms.smsApi.dto.requestDto;

import com.sms.smsApi.model.Department;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TeacherRequestFilter {
    // search
    private String keyword;

    // filters
    private Integer departmentId;
    private String sex;

    private LocalDate hiredDate;
    // pagination
    private int page = 0;
    private int size = 10;

    // sorting
    private String sortBy = "teacher_id";
    private String direction = "asc";
}
