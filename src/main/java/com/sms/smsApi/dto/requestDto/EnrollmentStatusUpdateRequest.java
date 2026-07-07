package com.sms.smsApi.dto.requestDto;


import com.sms.smsApi.model.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private EnrollmentStatus status;
}
