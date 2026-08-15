package com.sms.smsApi.dto.requestDto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ParentSearchFilter {
    // search
    private String keyword;

    // filters
    private String province;
    private String mother_name_en;
    private String father_name_en;

    // pagination
    private int page = 0;
    private int size = 10;

    // sorting
    private String sortBy = "id";
    private String direction = "asc";
}
