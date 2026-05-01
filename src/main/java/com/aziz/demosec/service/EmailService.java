package com.aziz.demosec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Password Reset ────────────────────────────────────────────────────────
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        sendHtml(toEmail, "Reset your MediCareAI password", buildPasswordResetTemplate(resetLink));
    }

    // ── Order Confirmation (patient creates order) ────────────────────────────
    public void sendOrderConfirmation(String toEmail, String patientName, Long orderId,
                                      String pharmacyName, BigDecimal total, Integer estimatedMin) {
        String subject = "Order Confirmation #" + orderId + " — MediCareAI";
        String etaLine = (estimatedMin != null && estimatedMin > 0)
                ? "<p class=\"message\">Estimated delivery time: <strong>" + estimatedMin + " min</strong></p>"
                : "";
        String body = baseTemplate(
                "Order Confirmed ✅",
                "Hello " + patientName + ",",
                "Your pharmacy order <strong>#" + orderId + "</strong> has been placed successfully at "
                        + "<strong>" + pharmacyName + "</strong>.<br/>"
                        + "Total amount: <strong>" + total + " TND</strong><br/>"
                        + "Our pharmacist will review your order shortly.",
                etaLine,
                "#10b981"
        );
        sendHtml(toEmail, subject, body);
    }

    // ── Order Status Update (validated / rejected / out-for-delivery / delivered) ─
    public void sendOrderStatusUpdate(String toEmail, String patientName, Long orderId,
                                      String status, String note) {
        String subject = "Order #" + orderId + " — " + formatStatus(status) + " — MediCareAI";
        String color = statusColor(status);
        String noteHtml = (note != null && !note.isBlank())
                ? "<p class=\"message\">Note: <em>" + note + "</em></p>"
                : "";
        String body = baseTemplate(
                "Order " + formatStatus(status),
                "Hello " + patientName + ",",
                "Your order <strong>#" + orderId + "</strong> status has been updated to "
                        + "<strong>" + formatStatus(status) + "</strong>.",
                noteHtml,
                color
        );
        sendHtml(toEmail, subject, body);
    }

    // ── Home Care Assigned ────────────────────────────────────────────────────
    public void sendHomeCareAssigned(String toEmail, String patientName, Long requestId,
                                     String providerName, String serviceName, LocalDateTime scheduledTime) {
        String subject = "Home Care Confirmed #" + requestId + " — MediCareAI";
        String dateStr = scheduledTime != null ? scheduledTime.format(DATE_FMT) : "TBD";
        String body = baseTemplate(
                "Care Request Confirmed 🏥",
                "Hello " + patientName + ",",
                "Your home care request <strong>#" + requestId + "</strong> for <strong>"
                        + serviceName + "</strong> has been confirmed.<br/>"
                        + "Provider: <strong>" + providerName + "</strong><br/>"
                        + "Scheduled: <strong>" + dateStr + "</strong>",
                "",
                "#4f46e5"
        );
        sendHtml(toEmail, subject, body);
    }

    // ── Core send helper ──────────────────────────────────────────────────────
    private void sendHtml(String toEmail, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + toEmail + ": " + e.getMessage(), e);
        }
    }

    // ── Template builders ─────────────────────────────────────────────────────
    private String baseTemplate(String title, String greeting, String mainText,
                                String extraHtml, String accentColor) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8"/>
              <style>
                body { margin:0; padding:0; background:#f0f4f8; font-family:'Segoe UI',Arial,sans-serif; }
                .container { max-width:480px; margin:40px auto; background:#fff;
                             border-radius:16px; overflow:hidden;
                             box-shadow:0 4px 20px rgba(0,0,0,0.08); }
                .header { background:#fff; padding:36px 40px 20px; text-align:center; }
                .logo { display:flex; align-items:center; justify-content:center; gap:10px; margin-bottom:28px; }
                .logo-icon { background:%s; border-radius:10px; width:42px; height:42px;
                             display:inline-flex; align-items:center; justify-content:center; }
                .logo-text { font-size:22px; font-weight:700; color:%s; }
                .title { font-size:22px; font-weight:800; color:#111; margin:0 0 16px; }
                .body { padding:0 40px 32px; text-align:center; }
                .greeting { font-size:16px; color:#333; margin-bottom:12px; }
                .message { font-size:14px; color:#555; line-height:1.7; margin-bottom:20px; }
                .divider { border:none; border-top:1px solid #eee; margin:24px 40px; }
                .footer { text-align:center; padding:0 40px 32px; }
                .footer-text { font-size:11px; color:#aaa; margin-bottom:8px; }
                .footer-links a { color:#aaa; text-decoration:none; margin:0 8px; font-size:11px; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <div class="logo">
                    <div class="logo-icon">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <polyline points="2,12 6,6 10,14 14,8 18,16 22,12"
                          stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </div>
                    <span class="logo-text">MediCareAI</span>
                  </div>
                  <h1 class="title">%s</h1>
                </div>
                <div class="body">
                  <p class="greeting">%s</p>
                  <p class="message">%s</p>
                  %s
                </div>
                <hr class="divider"/>
                <div class="footer">
                  <p class="footer-text">© 2026 MEDICAREAI HEALTH SYSTEMS. ALL RIGHTS RESERVED.</p>
                  <div class="footer-links">
                    <a href="#">PRIVACY POLICY</a>
                    <a href="#">SUPPORT</a>
                  </div>
                </div>
              </div>
            </body>
            </html>
            """.formatted(accentColor, accentColor, title, greeting, mainText, extraHtml);
    }

    private String buildPasswordResetTemplate(String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8"/>
              <style>
                body { margin:0; padding:0; background:#f0f4f8; font-family:'Segoe UI',Arial,sans-serif; }
                .container { max-width:480px; margin:40px auto; background:#fff;
                             border-radius:16px; overflow:hidden; box-shadow:0 4px 20px rgba(0,0,0,0.08); }
                .header { background:#fff; padding:36px 40px 20px; text-align:center; }
                .logo { display:flex; align-items:center; justify-content:center; gap:10px; margin-bottom:28px; }
                .logo-icon { background:#1a7a6e; border-radius:10px; width:42px; height:42px;
                             display:inline-flex; align-items:center; justify-content:center; }
                .logo-text { font-size:22px; font-weight:700; color:#1a7a6e; }
                .title { font-size:22px; font-weight:800; color:#111; margin:0 0 16px; }
                .body { padding:0 40px 32px; text-align:center; }
                .greeting { font-size:16px; color:#333; margin-bottom:12px; }
                .message { font-size:14px; color:#555; line-height:1.6; margin-bottom:28px; }
                .btn { display:inline-block; background:#1a7a6e; color:#fff !important;
                       text-decoration:none; padding:16px 48px; border-radius:12px;
                       font-size:16px; font-weight:600; margin-bottom:28px; }
                .expiry { font-size:13px; color:#888; }
                .divider { border:none; border-top:1px solid #eee; margin:24px 40px; }
                .footer { text-align:center; padding:0 40px 32px; }
                .footer-text { font-size:11px; color:#aaa; margin-bottom:8px; }
                .footer-links a { color:#aaa; text-decoration:none; margin:0 8px; font-size:11px; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <div class="logo">
                    <div class="logo-icon">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <polyline points="2,12 6,6 10,14 14,8 18,16 22,12"
                          stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </div>
                    <span class="logo-text">MediCareAI</span>
                  </div>
                  <h1 class="title">Reset your password</h1>
                </div>
                <div class="body">
                  <p class="greeting">Hello,</p>
                  <p class="message">A password reset was requested for your MediCareAI account.
                    If you didn't make this request, please ignore this email.</p>
                  <a href="%s" class="btn">Reset Password</a>
                  <p class="expiry">For security, this link will expire in 1 hour.</p>
                </div>
                <hr class="divider"/>
                <div class="footer">
                  <p class="footer-text">© 2026 MEDICAREAI HEALTH SYSTEMS. ALL RIGHTS RESERVED.</p>
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

    private String formatStatus(String status) {
        return switch (status.toUpperCase()) {
            case "VALIDATED"        -> "Validated ✅";
            case "REJECTED"         -> "Rejected ❌";
            case "OUT_FOR_DELIVERY" -> "Out for Delivery 🚚";
            case "DELIVERED"        -> "Delivered ✅";
            case "CANCELLED"        -> "Cancelled ❌";
            case "PAID"             -> "Payment Confirmed 💳";
            default -> status;
        };
    }

    private String statusColor(String status) {
        return switch (status.toUpperCase()) {
            case "VALIDATED", "DELIVERED", "PAID" -> "#10b981";
            case "REJECTED", "CANCELLED"           -> "#ef4444";
            case "OUT_FOR_DELIVERY"                -> "#4f46e5";
            default                                -> "#1a7a6e";
        };
    }
}
