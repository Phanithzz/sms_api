package com.sms.smsApi.service.EmailService;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendVerificationEmail(String to, String subject, String text) throws MessagingException;
    void sendPasswordResetEmail(
            String email,
            String resetLink
    );
}
