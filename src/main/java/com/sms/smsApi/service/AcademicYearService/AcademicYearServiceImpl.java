package com.sms.smsApi.service.AcademicYearService;


import com.sms.smsApi.dto.AcademicYearResponse;
import com.sms.smsApi.dto.requestDto.AcademicYearRequest;
import com.sms.smsApi.exception.DuplicateResourceException;
import com.sms.smsApi.exception.ResourceNotFoundException;
import com.sms.smsApi.model.AcademicYear;
import com.sms.smsApi.repository.AcademicYearRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AcademicYearServiceImpl implements AcademicYearService {

    private final AcademicYearRepository repository;

    public AcademicYearServiceImpl(AcademicYearRepository repository) {
        this.repository = repository;
    }

    public List<AcademicYearResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AcademicYearResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public AcademicYearResponse findCurrent() {
        return repository.findCurrent()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No academic year is currently marked as current"));
    }

    @Transactional
    public AcademicYearResponse create(AcademicYearRequest request) {
        if (repository.existsByYearName(request.getYearName())) {
            throw new DuplicateResourceException("Academic year already exists: " + request.getYearName());
        }
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new IllegalArgumentException("endDate must be after startDate");
        }
        if (request.isCurrent()) {
            repository.clearCurrentFlag();
        }
        AcademicYear ay = new AcademicYear();
        ay.setYearName(request.getYearName());
        ay.setStartDate(request.getStartDate());
        ay.setEndDate(request.getEndDate());
        ay.setIsCurrent(request.isCurrent());
        return toResponse(repository.save(ay));
    }

    @Transactional
    public AcademicYearResponse update(Long id, AcademicYearRequest request) {
        AcademicYear existing = getOrThrow(id);
        if (request.isCurrent() && !existing.getIsCurrent()) {
            repository.clearCurrentFlag();
        }
        existing.setYearName(request.getYearName());
        existing.setStartDate(request.getStartDate());
        existing.setEndDate(request.getEndDate());
        existing.setIsCurrent(request.isCurrent());
        repository.update(id, existing);
        return toResponse(existing);
    }

    public void delete(Long id) {
        getOrThrow(id);
        repository.deleteById(id);
    }

    private AcademicYear getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("AcademicYear", id));
    }

    private AcademicYearResponse toResponse(AcademicYear ay) {
        return new AcademicYearResponse(ay.getAcademicYearId(), ay.getYearName(), ay.getStartDate(), ay.getEndDate(),
                ay.getIsCurrent(), ay.getCreatedAt());
    }
}

