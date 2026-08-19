package com.sms.smsApi.controller;

import com.sms.smsApi.dto.EnrollmentResponse;
import com.sms.smsApi.dto.requestDto.EnrollmentRequest;
import com.sms.smsApi.dto.requestDto.EnrollmentStatusUpdateRequest;
import com.sms.smsApi.model.enums.EnrollmentStatus;
import com.sms.smsApi.service.EnrollService.EnrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollService enrollService;


    // =========================================================
    // CREATE ENROLLMENT
    // POST /api/v1/enrollments
    // =========================================================

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(
            @Valid @RequestBody EnrollmentRequest request) {

        EnrollmentResponse response =
                enrollService.enroll(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET BY ID
    // GET /api/v1/enrollments/{id}
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                enrollService.getById(id));
    }


    // =========================================================
    // SEARCH
    // GET /api/v1/enrollments/search
    // =========================================================

    @GetMapping("/search")
    public ResponseEntity<Page<EnrollmentResponse>> search(

            @RequestParam(required = false)
            String studentId,

            @RequestParam(required = false)
            Integer homeroomClassId,

            @RequestParam(required = false)
            Integer academicYearId,

            @RequestParam(required = false)
            EnrollmentStatus status,

            @PageableDefault(
                    size = 10,
                    sort = "enrollment_id",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                enrollService.search(
                        studentId,
                        homeroomClassId,
                        academicYearId,
                        status,
                        pageable));
    }


    // =========================================================
    // GET ACTIVE ENROLLMENTS FOR STUDENT
    // GET /api/v1/enrollments/student/{studentId}
    // =========================================================

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponse>>
    getStudentEnrollments(
            @PathVariable String studentId) {

        return ResponseEntity.ok(
                enrollService
                        .getActiveEnrollmentsForStudent(
                                studentId));
    }


    // =========================================================
    // GET STUDENT ENROLLMENT FOR ACADEMIC YEAR
    //
    // GET
    // /api/v1/enrollments/student/{studentId}/academic-year/{academicYearId}
    // =========================================================

    @GetMapping(
            "/student/{studentId}/academic-year/{academicYearId}")
    public ResponseEntity<EnrollmentResponse>
    getByStudentAndAcademicYear(

            @PathVariable String studentId,

            @PathVariable Integer academicYearId) {

        return ResponseEntity.ok(
                enrollService
                        .getByStudentAndAcademicYear(
                                studentId,
                                academicYearId));
    }


    // =========================================================
    // CHECK IF STUDENT IS ENROLLED
    //
    // GET
    // /api/v1/enrollments/check
    // =========================================================

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkEnrollment(

            @RequestParam String studentId,

            @RequestParam Integer academicYearId) {

        return ResponseEntity.ok(
                enrollService.isStudentEnrolled(
                        studentId,
                        academicYearId));
    }


    // =========================================================
    // UPDATE STATUS
    //
    // PATCH /api/v1/enrollments/{id}/status
    // =========================================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<EnrollmentResponse>
    updateStatus(

            @PathVariable Integer id,

            @Valid @RequestBody
            EnrollmentStatusUpdateRequest request) {

        return ResponseEntity.ok(
                enrollService.updateStatus(
                        id,
                        request));
    }


    // =========================================================
    // DROP ENROLLMENT
    //
    // PATCH /api/v1/enrollments/{id}/drop
    // =========================================================

    @PatchMapping("/{id}/drop")
    public ResponseEntity<EnrollmentResponse>
    drop(@PathVariable Integer id) {

        return ResponseEntity.ok(
                enrollService.drop(id));
    }


    // =========================================================
    // DELETE
    //
    // DELETE /api/v1/enrollments/{id}
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id) {

        enrollService.delete(id);

        return ResponseEntity.noContent().build();
    }
}