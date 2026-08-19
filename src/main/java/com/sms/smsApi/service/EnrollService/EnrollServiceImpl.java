package com.sms.smsApi.service.EnrollService;

import com.sms.smsApi.dto.EnrollmentResponse;
import com.sms.smsApi.dto.requestDto.*;
import com.sms.smsApi.model.HomeroomClass;
import com.sms.smsApi.model.enums.EnrollmentStatus;
import com.sms.smsApi.repository.AcademicYearRepository;
import com.sms.smsApi.repository.EnrollmentRepository;
import com.sms.smsApi.repository.HomeroomClassRepository;
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
    private final HomeroomClassRepository homeroomClassRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AcademicYearRepository academicYearRepository;
    @Override
    @Transactional(readOnly = true)
    public List<AcademicYearOption> getAcademicYearOptions() {

        return academicYearRepository.findOptions();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomeroomClassOption> getHomeroomClassOptions(
            Integer academicYearId) {

        return homeroomClassRepository
                .findEnrollmentOptions(academicYearId);
    }
    // =========================================================
    // ENROLL STUDENT
    // =========================================================

    @Override
    public EnrollmentResponse enroll(
            EnrollmentRequest request) {

        // 1. Check student

        if (studentRepository.countByStudentId(
                request.getStudentId()) == 0) {

            throw new RuntimeException(
                    "Student not found");
        }


        // 2. Check homeroom class

        HomeroomClass homeroomClass =
                homeroomClassRepository.findById(
                        request.getHomeroomClassId());

        if (homeroomClass == null) {

            throw new RuntimeException(
                    "Homeroom class not found");
        }


        // 3. Check academic year

        if (request.getAcademicYearId() == null) {

            throw new RuntimeException(
                    "Academic year is required");
        }


        // 4. Check if student already has
        //    an enrollment in this academic year

        if (enrollmentRepository.exists(
                request.getStudentId(),
                request.getAcademicYearId())) {

            throw new RuntimeException(
                    "Student is already enrolled in this academic year");
        }


        // 5. Check capacity

        if (homeroomClass.getEnrolledCount()
                >= homeroomClass.getMaxCapacity()) {

            throw new RuntimeException(
                    "Homeroom class is full");
        }


        // 6. Insert enrollment

        Integer enrollmentId =
                enrollmentRepository.insert(request);


        // 7. Increase class enrolled count

        homeroomClassRepository.incrementEnrollment(
                request.getHomeroomClassId());


        // 8. Return response

        EnrollmentResponse response =
                new EnrollmentResponse();

        response.setEnrollmentId(enrollmentId);
        response.setStudentId(request.getStudentId());
        response.setHomeroomClassId(
                request.getHomeroomClassId());
        response.setAcademicYearId(
                request.getAcademicYearId());
        response.setStatus(
                EnrollmentStatus.ACTIVE);

        return response;
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getById(Integer id) {

        return enrollmentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Enrollment not found"));
    }


    // =========================================================
    // SEARCH
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> search(EnrollSearchFilter request) {

        return enrollmentRepository.search(request);
    }


    // =========================================================
    // GET ALL ACTIVE ENROLLMENTS FOR STUDENT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse>
    getActiveEnrollmentsForStudent(
            String studentId) {

        return enrollmentRepository
                .findActiveByStudent(studentId);
    }


    // =========================================================
    // GET ENROLLMENT FOR STUDENT + ACADEMIC YEAR
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getByStudentAndAcademicYear(
            String studentId,
            Integer academicYearId) {

        return enrollmentRepository
                .findByStudentAndAcademicYear(
                        studentId,
                        academicYearId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Enrollment not found"));
    }


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @Override
    public EnrollmentResponse updateStatus(
            Integer id,
            EnrollmentStatusUpdateRequest request) {

        EnrollmentResponse enrollment =
                getById(id);

        EnrollmentStatus oldStatus =
                enrollment.getStatus();

        EnrollmentStatus newStatus =
                request.getStatus();


        // Nothing changed
        if (oldStatus == newStatus) {
            return enrollment;
        }


        enrollmentRepository.updateStatus(
                id,
                newStatus);


        // ACTIVE -> DROPPED
        if (oldStatus == EnrollmentStatus.ACTIVE
                && newStatus == EnrollmentStatus.DROPPED) {

            homeroomClassRepository
                    .decrementEnrollment(
                            enrollment.getHomeroomClassId());
        }


        // DROPPED -> ACTIVE
        if (oldStatus == EnrollmentStatus.DROPPED
                && newStatus == EnrollmentStatus.ACTIVE) {

            HomeroomClass homeroomClass =
                    homeroomClassRepository.findById(
                            enrollment.getHomeroomClassId());

            if (homeroomClass == null) {
                throw new RuntimeException(
                        "Homeroom class not found");
            }

            if (homeroomClass.getEnrolledCount()
                    >= homeroomClass.getMaxCapacity()) {

                throw new RuntimeException(
                        "Homeroom class is full");
            }

            homeroomClassRepository
                    .incrementEnrollment(
                            enrollment.getHomeroomClassId());
        }


        return getById(id);
    }


    // =========================================================
    // DROP ENROLLMENT
    // =========================================================

    @Override
    public EnrollmentResponse drop(Integer id) {

        EnrollmentResponse enrollment =
                getById(id);

        if (enrollment.getStatus()
                == EnrollmentStatus.DROPPED) {

            return enrollment;
        }

        enrollmentRepository.updateStatus(
                id,
                EnrollmentStatus.DROPPED);

        homeroomClassRepository
                .decrementEnrollment(
                        enrollment.getHomeroomClassId());

        return getById(id);
    }


    // =========================================================
    // DELETE ENROLLMENT
    // =========================================================

    @Override
    public void delete(Integer id) {

        EnrollmentResponse enrollment =
                getById(id);


        // Only decrease count if enrollment
        // was currently active

        if (enrollment.getStatus()
                == EnrollmentStatus.ACTIVE) {

            homeroomClassRepository
                    .decrementEnrollment(
                            enrollment.getHomeroomClassId());
        }


        enrollmentRepository.delete(id);
    }


    // =========================================================
    // CHECK STUDENT ENROLLED
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(
            String studentId,
            Integer academicYearId) {

        return enrollmentRepository.exists(
                studentId,
                academicYearId);
    }
}