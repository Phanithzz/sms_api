package com.sms.smsApi.service.AcademicYearService;

import com.sms.smsApi.dto.AcademicYearResponse;
import com.sms.smsApi.dto.requestDto.AcademicYearRequest;

import java.util.List;

public interface AcademicYearService {
    List<AcademicYearResponse> findAll();

    AcademicYearResponse findById(Long id);

    AcademicYearResponse findCurrent();

    AcademicYearResponse create(AcademicYearRequest request);

    AcademicYearResponse update(Long id, AcademicYearRequest request);

    void delete(Long id);
}
