package com.sundaysoul.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final HttpClient httpClient;

    @Value("${mail.api.key:}")
    private String apiKey;

    @Value("${mail.api.from:SundaySoul <onboarding@resend.dev>}")
    private String fromEmail;

    @Value("${spring.mail.username:}")
    private String smtpFromEmail;

    @Autowired
    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public void sendVerificationEmail(String email, String otp) {
        log.info("Sending verification email to: {}", email);

        // 1. If HTTPS REST API Key (Resend / Brevo) is present, send over HTTPS Port 443
        if (apiKey != null && !apiKey.trim().isBlank()) {
            sendEmailViaHttpsApi(email, otp);
            return;
        }

        // 2. Fallback to standard SMTP JavaMailSender if configured
        if (mailSender != null) {
            sendEmailViaSmtp(email, otp);
            return;
        }

        log.warn("No active mail configuration found (Neither RESEND_API_KEY nor SMTP present). OTP for {} is: {}", email, otp);
    }

    private void sendEmailViaHttpsApi(String toEmail, String otp) {
        try {
            log.info("Dispatching OTP email via Resend HTTPS REST API (Port 443) to: {}", toEmail);

            String sender = (fromEmail != null && !fromEmail.isBlank()) ? fromEmail : "SundaySoul <onboarding@resend.dev>";
            String jsonPayload = String.format("""
                {
                  "from": "%s",
                  "to": ["%s"],
                  "subject": "SundaySoul - Email Verification OTP",
                  "html": "<div style='font-family: Arial, sans-serif; padding: 24px; background-color: #0f766e; color: #ffffff; rounded: 16px; border: 1px solid #14b8a6;'><h2 style='margin-top:0; color: #ffffff;'>Welcome to SundaySoul!</h2><p style='color: #ccfbf1;'>Your email verification code is:</p><div style='font-size: 32px; font-weight: bold; letter-spacing: 6px; color: #38bdf8; margin: 20px 0; background-color: #042f2e; padding: 12px; rounded: 8px; text-align: center;'>%s</div><p style='font-size: 12px; color: #99f6e4;'>This OTP will expire in 10 minutes.</p></div>"
                }
                """, sender, toEmail, otp);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Successfully sent email via Resend HTTPS API. Response: {}", response.body());
            } else {
                log.error("Resend HTTPS API returned error status {}: {}", response.statusCode(), response.body());
                throw new RuntimeException("Resend HTTPS API error: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            log.error("Failed to send email via HTTPS REST API: {}", e.getMessage(), e);
            throw new RuntimeException("HTTPS Email API dispatch failed: " + e.getMessage(), e);
        }
    }

    private void sendEmailViaSmtp(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (smtpFromEmail != null && !smtpFromEmail.isBlank()) {
            message.setFrom(smtpFromEmail);
        }
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
