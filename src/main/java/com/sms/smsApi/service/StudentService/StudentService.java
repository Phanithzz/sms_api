package com.sms.smsApi.service.StudentService;

import com.sms.smsApi.dto.requestDto.StudentRequestFilter;
import com.sms.smsApi.model.Student;

import java.util.Map;

public interface StudentService {

    Map<String, Object> getStudents(StudentRequestFilter req);
    Student updateStudent(Student student);
}
