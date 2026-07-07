package com.sms.smsApi.service.EnrollService;

import com.sms.smsApi.dto.EnrollmentResponse;
import com.sms.smsApi.dto.requestDto.EnrollmentRequest;
import com.sms.smsApi.dto.requestDto.EnrollmentStatusUpdateRequest;
import com.sms.smsApi.model.enums.EnrollmentStatus;
import com.sms.smsApi.repository.EnrollmentRepository;
import com.sms.smsApi.repository.SectionRepository;
import com.sms.smsApi.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollServiceImpl implements EnrollService {

    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public EnrollmentResponse enroll(
            EnrollmentRequest request) {

        // 1 Check Student

        if(studentRepository.countByStudentId(request.getStudentId()) == 0){
            throw new RuntimeException("Student not found");
        }

        // 2 Check Section

        var section =
                sectionRepository.findById(request.getSectionId());

        if(section == null){
            throw new RuntimeException("Section not found");
        }

        // 3 Already enrolled?

        if(enrollmentRepository.exists(
                request.getStudentId(),
                request.getSectionId())){

            throw new RuntimeException(
                    "Student already enrolled.");
        }

        // 4 Capacity

        if(section.getEnrolledCount()
                >= section.getMaxCapacity()){

            throw new RuntimeException(
                    "Section Full");
        }

        // 5 Save enrollment

        Integer enrollmentId =
                enrollmentRepository.insert(request);

        // 6 Update class count

        sectionRepository.incrementEnrollment(
                request.getSectionId());

        EnrollmentResponse response =
                new EnrollmentResponse();

        response.setEnrollmentId(enrollmentId);
        response.setStudentId(request.getStudentId());
        response.setSectionId(request.getSectionId());
        response.setStatus(EnrollmentStatus.ACTIVE);

        return response;
    }


    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getById(Integer id) {

        return enrollmentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Enrollment not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> search(
            String studentId,
            Integer sectionId,
            EnrollmentStatus status,
            Pageable pageable) {

        return enrollmentRepository.search(
                studentId,
                sectionId,
                status,
                pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getActiveEnrollmentsForStudent(
            String studentId) {

        return enrollmentRepository.findActiveByStudent(studentId);
    }

    @Override
    public EnrollmentResponse updateStatus(
            Integer id,
            EnrollmentStatusUpdateRequest request) {

        EnrollmentResponse enrollment = getById(id);

        enrollmentRepository.updateStatus(
                id,
                request.getStatus());

        return getById(id);
    }

    @Override
    public void drop(Integer id) {

        EnrollmentResponse enrollment = getById(id);

        if (enrollment.getStatus() == EnrollmentStatus.DROPPED) {
            return;
        }

        enrollmentRepository.updateStatus(
                id,
                EnrollmentStatus.DROPPED);

        enrollmentRepository.decrementSectionCount(
                enrollment.getSectionId());
    }

    @Override
    public void delete(Integer id) {

        EnrollmentResponse enrollment = getById(id);

        enrollmentRepository.delete(id);

        enrollmentRepository.decrementSectionCount(
                enrollment.getSectionId());
    }
}
