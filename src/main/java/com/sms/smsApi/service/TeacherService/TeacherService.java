package com.sms.smsApi.service.TeacherService;

import com.sms.smsApi.dto.requestDto.TeacherRequestFilter;

import java.util.Map;

public interface TeacherService {
    Map<String, Object> getTeachers(TeacherRequestFilter req);
}
