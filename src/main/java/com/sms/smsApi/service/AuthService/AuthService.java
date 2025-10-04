package com.sms.smsApi.service.AuthService;

import com.sms.smsApi.dto.LoginDto;
import com.sms.smsApi.dto.RegistrationDto;
import com.sms.smsApi.dto.VerifyUserDto;
import com.sms.smsApi.model.User;

public interface AuthService {
    User signup(RegistrationDto input);
    User authenticate(LoginDto input);
    void verifyUser(VerifyUserDto input);
    void resendVerificationCode(String email);
    void sendVerificationEmail(User user);
}
