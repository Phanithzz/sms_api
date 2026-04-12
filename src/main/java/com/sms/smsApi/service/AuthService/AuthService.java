package com.sms.smsApi.service.AuthService;

import com.sms.smsApi.dto.LoginDto;
import com.sms.smsApi.dto.RegistrationDto;
import com.sms.smsApi.dto.VerifyUserDto;
import com.sms.smsApi.exception.AccountDisabledException;
import com.sms.smsApi.exception.AccountNotVerifiedException;
import com.sms.smsApi.exception.InvalidCredentialsException;
import com.sms.smsApi.exception.VerificationCodeExpiredException;
import com.sms.smsApi.model.User;
import com.sms.smsApi.reponse.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface AuthService {
//    User signup(RegistrationDto input);
    LoginResponse authenticate(LoginDto input, HttpServletResponse response) throws InvalidCredentialsException, AccountDisabledException, AccountNotVerifiedException;
    LoginResponse verifyAndLogin(VerifyUserDto input, HttpServletResponse response) throws
            InvalidCredentialsException,
            VerificationCodeExpiredException;
    void resendVerificationCode(String email);
    void sendVerificationEmail(User user);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
    boolean logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication);
}

