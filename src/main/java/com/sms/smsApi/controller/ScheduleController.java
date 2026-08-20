package com.sms.smsApi.controller;

import com.sms.smsApi.dto.requestDto.ScheduleRequest;
import com.sms.smsApi.service.ScheduleService.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;


    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody ScheduleRequest request) {

        return ResponseEntity.ok(
                scheduleService.create(request)
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                scheduleService.getById(id)
        );
    }


    @GetMapping("/class/{classId}")
    public ResponseEntity<?> getByClass(
            @PathVariable Integer classId,
            @RequestParam Integer academicYearId) {

        return ResponseEntity.ok(
                scheduleService.getByClass(
                        classId,
                        academicYearId
                )
        );
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentSchedule(
            @PathVariable String studentId,
            @RequestParam Integer academicYearId
    ) {

        return ResponseEntity.ok(
                scheduleService.getStudentSchedule(
                        studentId,
                        academicYearId
                )
        );
    }
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<?> getByTeacher(
            @PathVariable String teacherId,
            @RequestParam Integer academicYearId) {

        return ResponseEntity.ok(
                scheduleService.getByTeacher(
                        teacherId,
                        academicYearId
                )
        );
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAllSchedules(
            @RequestParam Integer academicYearId) {

        return ResponseEntity.ok(
                scheduleService.getAllSchedules(academicYearId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Integer id) {

        scheduleService.delete(id);

        return ResponseEntity.ok(
                "Schedule deleted successfully"
        );
    }
}