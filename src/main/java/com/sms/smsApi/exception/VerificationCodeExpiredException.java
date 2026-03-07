package com.sms.smsApi.exception;

public class VerificationCodeExpiredException extends Exception{
    public VerificationCodeExpiredException(String message) {
        super(message);
    }
}
