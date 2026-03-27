package com.aziz.demosec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendCode(String email, String code) {

        // 🔥 LOG IMPORTANT
        System.out.println("📧 Sending email to: " + email);
        System.out.println("🔐 Verification code: " + code);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Code de vérification");
            message.setText("Votre code de vérification est : " + code);

            mailSender.send(message);

            System.out.println("✅ Email envoyé avec succès");

        } catch (Exception e) {
            System.out.println("❌ ERREUR EMAIL : " + e.getMessage());
            e.printStackTrace();
        }
    }
}