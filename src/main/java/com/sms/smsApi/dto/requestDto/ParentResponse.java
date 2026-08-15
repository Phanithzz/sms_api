package com.sms.smsApi.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParentResponse {

    private String parentId;

    private Integer userId;

    // Father
    private String fatherNameKh;

    private String fatherNameEn;

    private String fatherPhone;

    private String fatherJob;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fatherDob;

    // Mother
    private String motherNameKh;

    private String motherNameEn;

    private String motherPhone;

    private String motherJob;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate motherDob;

    // Address
    private String currentAddress;

    private String province;

    // Parent's own name
    private String firstNameEn;

    private String lastNameEn;

    private String firstNameKh;

    private String lastNameKh;

    // Audit
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}