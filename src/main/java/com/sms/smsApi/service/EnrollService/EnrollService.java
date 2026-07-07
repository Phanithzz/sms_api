package com.sms.smsApi.service.EnrollService;

import com.sms.smsApi.dto.EnrollmentResponse;
import com.sms.smsApi.dto.requestDto.EnrollmentRequest;
import com.sms.smsApi.dto.requestDto.EnrollmentStatusUpdateRequest;
import com.sms.smsApi.model.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EnrollService {
    EnrollmentResponse enroll(EnrollmentRequest request);

    EnrollmentResponse getById(Integer id);

    Page<EnrollmentResponse> search(
            String studentId,
            Integer sectionId,
            EnrollmentStatus status,
            Pageable pageable);

    List<EnrollmentResponse> getActiveEnrollmentsForStudent(String studentId);

    EnrollmentResponse updateStatus(
            Integer id,
            EnrollmentStatusUpdateRequest request);

    void drop(Integer id);

    void delete(Integer id);

}
