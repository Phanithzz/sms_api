package com.sms.smsApi.controller;

import com.sms.smsApi.dto.requestDto.StudentRequestFilter;
import com.sms.smsApi.model.Student;
import com.sms.smsApi.reponse.ApiResponse;
import com.sms.smsApi.service.StudentService.StudentService;
import org.hibernate.query.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody StudentRequestFilter request) {
        return ResponseEntity.ok(studentService.getStudents(request));
    }

//    @PostMapping("/create")
//    public ResponseEntity<?> create(@RequestBody StudentCreateDto dto) {
//
//    }
}
