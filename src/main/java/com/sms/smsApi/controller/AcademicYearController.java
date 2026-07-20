package com.sms.smsApi.controller;



import com.sms.smsApi.dto.AcademicYearResponse;

import com.sms.smsApi.dto.requestDto.AcademicYearRequest;
import com.sms.smsApi.service.AcademicYearService.AcademicYearService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/academic-years")
public class AcademicYearController {

    private final AcademicYearService service;

    public AcademicYearController(AcademicYearService service) {
        this.service = service;
    }

    @GetMapping
    public List<AcademicYearResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/current")
    public AcademicYearResponse findCurrent() {
        return service.findCurrent();
    }

    @GetMapping("/{id}")
    public AcademicYearResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AcademicYearResponse create(@Valid @RequestBody AcademicYearRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public AcademicYearResponse update(@PathVariable Long id, @Valid @RequestBody AcademicYearRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
