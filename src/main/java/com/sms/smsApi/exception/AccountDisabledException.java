package com.sms.smsApi.exception;

public class AccountDisabledException extends Exception{

    public AccountDisabledException(String message) {
        super(message);
    }

    public AccountDisabledException(Throwable cause) {
        super(cause);
    }
}
