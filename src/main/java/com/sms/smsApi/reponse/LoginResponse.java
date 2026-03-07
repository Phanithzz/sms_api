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

    public LoginResponse() {

    }

    // Success response with tokens
    public static LoginResponse success(String token, User user) {
        LoginResponse response = new LoginResponse();
        response.success = true;
        response.accessToken = token;
        response.verificationRequired = false;
        response.user = user;
        return response;
    }

    // Verification required response
    public static LoginResponse verificationRequired(String message) {
        LoginResponse response = new LoginResponse();
        response.success = false;
        response.message = message;
        response.verificationRequired = true;
        return response;
    }

}