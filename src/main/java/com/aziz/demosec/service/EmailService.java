package com.aziz.demosec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Reset your MediCareAI password");
            helper.setText(buildEmailTemplate(resetLink), true); // true = HTML

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    private String buildEmailTemplate(String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8"/>
              <style>
                body {
                  margin: 0;
                  padding: 0;
                  background-color: #f0f4f8;
                  font-family: 'Segoe UI', Arial, sans-serif;
                }
                .container {
                  max-width: 480px;
                  margin: 40px auto;
                  background: #ffffff;
                  border-radius: 16px;
                  overflow: hidden;
                  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
                }
                .header {
                  background: #ffffff;
                  padding: 36px 40px 20px;
                  text-align: center;
                }
                .logo {
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  gap: 10px;
                  margin-bottom: 28px;
                }
                .logo-icon {
                  background: #1a7a6e;
                  border-radius: 10px;
                  width: 42px;
                  height: 42px;
                  display: inline-flex;
                  align-items: center;
                  justify-content: center;
                }
                .logo-text {
                  font-size: 22px;
                  font-weight: 700;
                  color: #1a7a6e;
                }
                .title {
                  font-size: 22px;
                  font-weight: 800;
                  color: #111;
                  margin: 0 0 16px;
                }
                .body {
                  padding: 0 40px 32px;
                  text-align: center;
                }
                .greeting {
                  font-size: 16px;
                  color: #333;
                  margin-bottom: 12px;
                }
                .message {
                  font-size: 14px;
                  color: #555;
                  line-height: 1.6;
                  margin-bottom: 28px;
                }
                .btn {
                  display: inline-block;
                  background: #1a7a6e;
                  color: #ffffff !important;
                  text-decoration: none;
                  padding: 16px 48px;
                  border-radius: 12px;
                  font-size: 16px;
                  font-weight: 600;
                  margin-bottom: 28px;
                }
                .expiry {
                  font-size: 13px;
                  color: #888;
                  margin-bottom: 0;
                }
                .divider {
                  border: none;
                  border-top: 1px solid #eee;
                  margin: 24px 40px;
                }
                .footer {
                  text-align: center;
                  padding: 0 40px 32px;
                }
                .footer-icons {
                  margin-bottom: 12px;
                  font-size: 20px;
                  color: #aaa;
                }
                .footer-text {
                  font-size: 11px;
                  color: #aaa;
                  margin-bottom: 8px;
                }
                .footer-links {
                  font-size: 11px;
                  color: #aaa;
                }
                .footer-links a {
                  color: #aaa;
                  text-decoration: none;
                  margin: 0 8px;
                }
              </style>
            </head>
            <body>
              <div class="container">

                <!-- Header -->
                <div class="header">
                  <div class="logo">
                    <div class="logo-icon">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <polyline points="2,12 6,6 10,14 14,8 18,16 22,12"
                          stroke="white" stroke-width="2"
                          stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </div>
                    <span class="logo-text">MediCareAI</span>
                  </div>
                  <h1 class="title">Reset your password</h1>
                </div>

                <!-- Body -->
                <div class="body">
                  <p class="greeting">Hello,</p>
                  <p class="message">
                    A password reset was requested for your MediCareAI account.
                    If you didn't make this request, please ignore this email.
                  </p>
                  <a href="%s" class="btn">Reset Password</a>
                  <p class="expiry">For security, this link will expire in 1 hour.</p>
                </div>

                <hr class="divider"/>

                <!-- Footer -->
                <div class="footer">
                  <p class="footer-text">© 2024 MEDICAREAI HEALTH SYSTEMS. ALL RIGHTS RESERVED.</p>
                  <div class="footer-links">
                    <a href="#">PRIVACY POLICY</a>
                    <a href="#">TERMS OF SERVICE</a>
                    <a href="#">SUPPORT</a>
                  </div>
                </div>

              </div>
            </body>
            </html>
            """.formatted(resetLink);
    }
}