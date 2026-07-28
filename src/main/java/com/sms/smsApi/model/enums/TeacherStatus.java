package com.sms.smsApi.model.enums;

public enum TeacherStatus {
    ACTIVE, INACTIVE, ON_LEAVE;

    public static TeacherStatus fromDatabase(String value) {
        return switch (value) {
            case "0" -> ACTIVE;
            case "1" -> INACTIVE;
            case "2" -> ON_LEAVE;
            default -> valueOf(value);
        };
    }
}
