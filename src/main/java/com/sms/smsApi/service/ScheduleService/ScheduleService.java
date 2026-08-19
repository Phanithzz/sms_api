package com.sms.smsApi.service.ScheduleService;

import com.sms.smsApi.dto.requestDto.ScheduleRequest;
import com.sms.smsApi.dto.requestDto.ScheduleResponse;

import java.util.List;

public interface ScheduleService {

    ScheduleResponse create(ScheduleRequest request);

    ScheduleResponse getById(Integer id);

    List<ScheduleResponse> getByClass(
            Integer homeroomClassId,
            Integer academicYearId
    );

    List<ScheduleResponse> getByTeacher(
            String teacherId,
            Integer academicYearId
    );

    void delete(Integer id);
}