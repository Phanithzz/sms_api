package com.sms.smsApi.service.StudentService;

import com.sms.smsApi.dto.requestDto.StudentRequestFilter;

import java.util.Map;

public interface StudentService {

    Map<String, Object> getStudents(StudentRequestFilter req);
}
