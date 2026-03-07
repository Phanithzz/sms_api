package com.sms.smsApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Data
public class UserReponseDto {

    Long userId;
    String username;
    String email;
    boolean verified;
    boolean enabled;
}
