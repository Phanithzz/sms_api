package com.sms.smsApi.dto.requestDto;

public record AttendanceSummary(
        long present,
        long absent,
        long late
) {
}
