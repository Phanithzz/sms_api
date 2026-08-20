package com.sms.smsApi.reponse;

import com.sms.smsApi.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
    private String accessToken;
    private boolean verificationRequired;
    private User user;

    private String studentId;
    private String teacherId;
    private String parentId;
    public LoginResponse() {

    }

    // Success response with tokens
    public static LoginResponse success(
            String token,
            User user,
            String studentId,
            String teacherId,
            String parentId
    ) {
        LoginResponse response = new LoginResponse();

        response.success = true;
        response.accessToken = token;
        response.verificationRequired = false;
        response.user = user;

        response.studentId = studentId;
        response.teacherId = teacherId;
        response.parentId = parentId;

        return response;
    }


    // Verification required response
    public static LoginResponse verificationRequired(String message, String email) {
        LoginResponse response = new LoginResponse();
        response.success = false;
        response.message = message;
        response.verificationRequired = true;
        response.user = new User();
        response.user.setEmail(email);
        return response;
    }

}