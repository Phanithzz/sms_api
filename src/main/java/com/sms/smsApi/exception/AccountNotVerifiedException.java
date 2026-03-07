package com.sms.smsApi.exception;

public class AccountNotVerifiedException extends Exception{
    public AccountNotVerifiedException() {
    }

    public AccountNotVerifiedException(String message) {
        super(message);
    }

    public AccountNotVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountNotVerifiedException(Throwable cause) {
        super(cause);
    }

    public AccountNotVerifiedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
