package com.sms.smsApi.dto.requestDto;

import java.util.List;

public record AdminDashboardResponse(
        long totalStudents,
        long totalTeachers,
        long totalParents,
        long totalClasses,
        long activeStudents,
        long inactiveStudents,
        List<GenderCount> studentGender,
        AttendanceSummary attendance
) {
}
