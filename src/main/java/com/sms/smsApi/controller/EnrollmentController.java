package com.sms.smsApi.controller;

import com.sms.smsApi.dto.EnrollmentResponse;
import com.sms.smsApi.dto.requestDto.EnrollmentRequest;
import com.sms.smsApi.dto.requestDto.EnrollmentStatusUpdateRequest;
import com.sms.smsApi.model.enums.EnrollmentStatus;
import com.sms.smsApi.service.EnrollService.EnrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollService enrollmentService;

    // POST /api/v1/enrollments
    @PostMapping
    public ResponseEntity<?> enroll(
            @Valid @RequestBody EnrollmentRequest request) {

        try {
            EnrollmentResponse created = enrollmentService.enroll(request);
            return ResponseEntity.ok(created);

        } catch (DataAccessException e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage(),
                            "cause", e.getMostSpecificCause().getMessage()
                    ));
        }
    }

    // GET /api/v1/enrollments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(enrollmentService.getById(id));
    }

    // GET /api/v1/enrollments?studentId=&sectionId=&status=&page=&size=&sort=
    @GetMapping
    public ResponseEntity<Page<EnrollmentResponse>> search(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) Integer sectionId,
            @RequestParam(required = false) EnrollmentStatus status,
            @PageableDefault(size = 20, sort = "enrollmentId") Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.search(studentId, sectionId, status, pageable));
    }

    // GET /api/v1/enrollments/students/{studentId}/active
    @GetMapping("/students/{studentId}/active")
    public ResponseEntity<List<EnrollmentResponse>> getActiveForStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(enrollmentService.getActiveEnrollmentsForStudent(studentId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EnrollmentResponse> updateStatus(
            @PathVariable Integer id,
            @Valid @RequestBody EnrollmentStatusUpdateRequest request) {
        return ResponseEntity.ok(enrollmentService.updateStatus(id, request));
    }

    // POST /api/v1/enrollments/{id}/drop  (convenience shortcut)
    @PostMapping("/{id}/drop")
    public ResponseEntity<Void> drop(@PathVariable Integer id) {
        enrollmentService.drop(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/enrollments/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        enrollmentService.delete(id);
    }
}

