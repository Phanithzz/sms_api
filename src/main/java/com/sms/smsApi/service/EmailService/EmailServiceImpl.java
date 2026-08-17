package com.sms.smsApi.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        emailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(
            String email,
            String resetLink
    ) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Reset Your Password");

        message.setText("""
                Hello,

                We received a request to reset your password.

                Click the link below to reset your password:

                %s

                This link will expire in 15 minutes.

                If you did not request this, you can safely ignore this email.

                Regards,
                SMS System
                """.formatted(resetLink));

        emailSender.send(message);
    }
}
