package com.sms.smsApi.model;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN(1),
    TEACHER(2),
    STUDENT(3),
    PARENT(4);

    private final int code;

    UserRole(int code) {
        this.code = code;
    }

}