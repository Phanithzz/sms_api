package com.sms.smsApi.service.ParentService;

import com.sms.smsApi.dto.requestDto.*;
import com.sms.smsApi.model.Parent;


import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ParentService {

    Map<String, Object> getParent(ParentSearchFilter req);
    Parent updateParent(String parentId, ParentRequest student) throws IOException;
    List<ParentResponse> findAll();
    ParentResponse findById(String id);
    void delete(String id);
}
