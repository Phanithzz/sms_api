package com.sms.smsApi.service.StudentService;

import com.sms.smsApi.dto.requestDto.StudentRequest;
import com.sms.smsApi.dto.requestDto.StudentRequestFilter;
import com.sms.smsApi.dto.requestDto.StudentResponse;
import com.sms.smsApi.model.Student;

import java.util.List;
import java.util.Map;

public interface StudentService {

    Map<String, Object> getStudents(StudentRequestFilter req);
    Student updateStudent(String studentId, StudentRequest student);
    List<StudentResponse> findAll();
    StudentResponse findById(String id);
    void delete(String id);
}
