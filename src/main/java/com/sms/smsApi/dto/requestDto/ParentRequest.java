package com.sms.smsApi.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParentRequest {

//    @NotNull(message = "User ID is required")
//    private Integer userId;

    // Father
    private String fatherNameKh;

    private String fatherNameEn;

    @Pattern(
            regexp = "^[0-9+\\- ]*$",
            message = "Father phone number must contain only numbers"
    )
    private String fatherPhone;

    private String fatherJob;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fatherDob;

    // Mother
    private String motherNameKh;

    private String motherNameEn;

    @Pattern(
            regexp = "^[0-9+\\- ]*$",
            message = "Mother phone number must contain only numbers"
    )
    private String motherPhone;

    private String motherJob;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate motherDob;

    // Address
    private String currentAddress;

    private String province;


}