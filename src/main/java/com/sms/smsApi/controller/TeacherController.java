package com.sms.smsApi.controller;

import com.sms.smsApi.dto.requestDto.StudentRequestFilter;
import com.sms.smsApi.dto.requestDto.TeacherRequest;
import com.sms.smsApi.dto.requestDto.TeacherRequestFilter;
import com.sms.smsApi.dto.requestDto.TeacherResponse;
import com.sms.smsApi.model.enums.TeacherStatus;
import com.sms.smsApi.service.TeacherService.TeacherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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


    @GetMapping("/{id}")
    public TeacherResponse findById(@PathVariable String id) {
        return teacherService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherResponse create(@Valid @RequestBody TeacherRequest request) {
        return teacherService.create(request);
    }

    @PutMapping("/{id}")
    public TeacherResponse update(@PathVariable String id, @Valid @RequestBody TeacherRequest request) {
        return teacherService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public TeacherResponse updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        return teacherService.updateStatus(id, TeacherStatus.valueOf(body.get("status")));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        teacherService.delete(id);
    }
}
