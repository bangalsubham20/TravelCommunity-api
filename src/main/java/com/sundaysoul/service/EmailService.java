package com.sundaysoul.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String email, String otp) {
        log.info("Sending verification email to: {}", email);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("SundaySoul - Email Verification");
        message.setText(
            "Welcome to SundaySoul!\n\n" +
            "Your email verification OTP is: " + otp +
            "\n\nThis OTP will expire in 10 minutes."
        );

        mailSender.send(message);
    }
}
