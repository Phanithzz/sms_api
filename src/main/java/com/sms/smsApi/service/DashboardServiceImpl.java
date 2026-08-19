package com.sms.smsApi.service;

import com.sms.smsApi.dto.requestDto.*;
import com.sms.smsApi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final HomeroomClassRepository homeroomClassRepository;

    public AdminDashboardResponse getAdminDashboard() {

        long activeStudents =
                studentRepository.countActiveStudents();

        long inactiveStudents =
                studentRepository.countInactiveStudents();

        long totalStudents =
                activeStudents + inactiveStudents;

        long totalTeachers =
                teacherRepository.countActiveTeachers();

        long totalParents =
                parentRepository.countParent();

        long totalClasses =
                classRepository.count();

        List<GenderCount> studentGender =
                studentRepository.countStudentGender();

        List<GradeEnrollmentCount> gradeEnrollmentCount =
                enrollmentRepository.countEnrollmentByGrade();

        List<ClassEnrollmentSummary> totalClassEnrollmentSummary =
                homeroomClassRepository.getDashboardSummary();


        return new AdminDashboardResponse(
                totalStudents,
                totalTeachers,
                totalParents,
                totalClasses,
                activeStudents,
                inactiveStudents,
                studentGender,
                gradeEnrollmentCount,
                totalClassEnrollmentSummary
        );
    }
}
