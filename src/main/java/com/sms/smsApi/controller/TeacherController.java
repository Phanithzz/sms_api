package com.sms.smsApi.controller;

import com.sms.smsApi.dto.requestDto.StudentRequestFilter;
import com.sms.smsApi.dto.requestDto.TeacherRequestFilter;
import com.sms.smsApi.service.TeacherService.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherController {
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody TeacherRequestFilter request) {
        return ResponseEntity.ok(teacherService.getTeachers(request));
    }
}
