package com.sms.smsApi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDto {

    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String password;

}
