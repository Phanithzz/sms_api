package com.sms.smsApi.reponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String token;
    private Long expiredAt;

    public LoginResponse(String token, Long expiredAt) {
        this.token = token;
        this.expiredAt = expiredAt;
    }
}
