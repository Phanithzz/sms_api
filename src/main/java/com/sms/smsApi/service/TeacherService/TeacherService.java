package com.sms.smsApi.service.TeacherService;

import com.sms.smsApi.dto.requestDto.TeacherRequest;
import com.sms.smsApi.dto.requestDto.TeacherRequestFilter;
import com.sms.smsApi.dto.requestDto.TeacherResponse;
import com.sms.smsApi.model.enums.TeacherStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface TeacherService {
    Map<String, Object> getTeachers(TeacherRequestFilter req);
    TeacherResponse findById( String id);
    TeacherResponse create( TeacherRequest request);
    TeacherResponse update( String id, TeacherRequest request);
    TeacherResponse updateStatus( String id, TeacherStatus status);
    void delete( String id);
}
