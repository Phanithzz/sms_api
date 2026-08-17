package com.sms.smsApi.service;

import com.sms.smsApi.dto.requestDto.AdminDashboardResponse;
import com.sms.smsApi.dto.requestDto.AttendanceSummary;
import com.sms.smsApi.dto.requestDto.GenderCount;
import com.sms.smsApi.repository.ClassRepository;
import com.sms.smsApi.repository.ParentRepository;
import com.sms.smsApi.repository.StudentRepository;
import com.sms.smsApi.repository.TeacherRepository;
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
//    private final AttendanceRepository attendanceRepository;

    public AdminDashboardResponse getAdminDashboard() {

        long activeStudents =
                studentRepository.countActiveStudents();

        long inactiveStudents =
                studentRepository.countInactiveStudents();

        long totalTeachers =
                teacherRepository.countActiveTeachers();

        long totalParents =
                parentRepository.countParent();

        long totalClasses =
                classRepository.count();

        List<GenderCount> totalStudentGender =
                studentRepository.countStudentGender();

//        AttendanceSummary attendance =
//                attendanceRepository.getTodaySummary();

        return new AdminDashboardResponse(
                activeStudents + inactiveStudents,
                totalTeachers,
                totalParents,
                totalClasses,
                activeStudents,
                inactiveStudents,
                totalStudentGender,
                null//attendance
        );
    }
}
