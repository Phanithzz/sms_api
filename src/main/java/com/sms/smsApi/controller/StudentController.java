package com.sms.smsApi.controller;

import com.sms.smsApi.dto.requestDto.StudentRequest;
import com.sms.smsApi.dto.requestDto.StudentRequestFilter;
import com.sms.smsApi.dto.requestDto.StudentResponse;
import com.sms.smsApi.model.Student;
import com.sms.smsApi.model.enums.StudentStatus;
import com.sms.smsApi.reponse.ApiResponse;
import com.sms.smsApi.service.StudentService.StudentService;
import jakarta.validation.Valid;
import org.hibernate.query.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(@PathVariable String id, @ModelAttribute  StudentRequest student) throws IOException {
        Student updatedStudent = studentService.updateStudent(id,student);
        return ResponseEntity.ok(updatedStudent);
    }


    @GetMapping
    public List<StudentResponse> findAll() {
        return studentService.findAll();
    }

    @GetMapping("/{id}")
    public StudentResponse findById(@PathVariable String id) {
        return studentService.findById(id);
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public StudentResponse create(@Valid @RequestBody StudentRequest request) {
//        return service.create(request);
//    }

//    @PatchMapping("/{id}/status")
//    public StudentResponse updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
//        return studentService.updateStatus(id, StudentStatus.valueOf(body.get("status")));
//    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        studentService.delete(id);
    }
}
