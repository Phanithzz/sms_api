package com.sms.smsApi.service.ScheduleService;

import com.sms.smsApi.dto.requestDto.ScheduleRequest;
import com.sms.smsApi.dto.requestDto.ScheduleResponse;
import com.sms.smsApi.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    public List<ScheduleResponse> getAllSchedules(Integer academicYearId) {
        return scheduleRepository.getAllSchedules(academicYearId);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getStudentSchedule(
            String studentId,
            Integer academicYearId
    ) {

        return scheduleRepository.findByStudent(
                studentId,
                academicYearId
        );
    }
    @Override
    public ScheduleResponse create(
            ScheduleRequest request) {

        // Check schedule conflict
        if (scheduleRepository.existsConflict(
                request.getHomeroomClassId(),
                request.getClassroomId(),
                request.getSectionId(),
                request.getAcademicYearId(),
                request.getDayOfWeek(),
                request.getPeriodNumber()
        )) {
            throw new RuntimeException(
                    "Schedule conflict: class, teacher, or classroom is already occupied."
            );
        }

        Integer id =
                scheduleRepository.insert(request);

        return getById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public ScheduleResponse getById(Integer id) {

        return scheduleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Schedule not found"
                        ));
    }


    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getByClass(
            Integer homeroomClassId,
            Integer academicYearId) {

        return scheduleRepository.findByClass(
                homeroomClassId,
                academicYearId
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getByTeacher(
            String teacherId,
            Integer academicYearId) {

        return scheduleRepository.findByTeacher(
                teacherId,
                academicYearId
        );
    }


    @Override
    public void delete(Integer id) {

        // Make sure it exists first
        getById(id);

        scheduleRepository.delete(id);
    }
}