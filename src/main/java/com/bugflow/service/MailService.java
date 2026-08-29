package com.bugflow.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.bugflow.entity.Invitation;
import com.bugflow.entity.Organization;
import com.bugflow.entity.Project;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.ProjectRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final OrganizationRepository organizationRepository;
    private final ProjectRepository projectRepository;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${mail.from:}")
    private String mailFrom;

    public MailService(JavaMailSender mailSender,
                       OrganizationRepository organizationRepository,
                       ProjectRepository projectRepository) {
        this.mailSender = mailSender;
        this.organizationRepository = organizationRepository;
        this.projectRepository = projectRepository;
    }

    public void sendPasswordResetEmail(String toEmail, String resetUrl, int expirationMinutes) {
        String subject = "Reset your BugFlow password";
        String body = "<html>"
                + "<body style=\"margin:0;padding:0;background:#f5faff;font-family:Arial,sans-serif;color:#0f172a;\">"
                + "<div style=\"max-width:560px;margin:32px auto;background:#ffffff;border:1px solid #dbeafe;border-radius:12px;overflow:hidden;\">"
                + "<div style=\"padding:24px 24px 10px 24px;\">"
                + "<div style=\"font-size:12px;font-weight:700;letter-spacing:1.2px;text-transform:uppercase;color:#2563eb;\">BugFlow</div>"
                + "</div>"
                + "<div style=\"padding:0 24px 20px 24px;\">"
                + "<h2 style=\"margin:0 0 12px 0;font-size:28px;line-height:1.3;color:#0f172a;\">Reset your password</h2>"
                + "<p style=\"margin:0 0 18px 0;font-size:16px;line-height:1.6;color:#475569;\">Hi,</p>"
                + "<p style=\"margin:0 0 18px 0;font-size:16px;line-height:1.6;color:#475569;\">We received a request to reset your BugFlow password.</p>"
                + "<p style=\"margin:0 0 20px 0;font-size:16px;line-height:1.6;color:#475569;\">Click the button below to choose a new password.</p>"
                + "<p style=\"margin:0 0 20px 0;\"><a href=\"" + resetUrl + "\" style=\"display:inline-block;padding:9px 16px;background:#2563eb;color:#ffffff;text-decoration:none;border-radius:7px;font-size:14px;font-weight:600;line-height:1.4;\">Reset Password</a></p>"
                + "<p style=\"margin:0 0 10px 0;font-size:14px;line-height:1.6;color:#475569;\">This link will expire in " + expirationMinutes + " minutes.</p>"
                + "<p style=\"margin:0 0 18px 0;font-size:14px;line-height:1.6;color:#475569;\">If you did not request a password reset, you can safely ignore this email.</p>"
                + "<p style=\"margin:0;font-size:14px;line-height:1.6;color:#475569;\">Thanks,<br/>The BugFlow Team</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            String from = mailFrom != null && !mailFrom.isBlank() ? mailFrom : null;
            if (from != null) {
                helper.setFrom(from);
            }

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to send password reset email", ex);
        }
    }

    public void sendInvitationEmail(Invitation invitation) throws MessagingException {

        Organization org = organizationRepository.findById(invitation.getOrganizationId()).orElse(null);
        Project project = projectRepository.findById(invitation.getProjectId()).orElse(null);

        String orgName = org != null ? org.getName() : "your organization";
        String projectName = project != null ? project.getName() : "your project";

        String subject = String.format("Invitation to join %s — %s on BugFlow", orgName, projectName);

        String acceptUrl = String.format("%s/invitations/accept?token=%s", frontendBaseUrl, invitation.getToken());

        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
        String createdAtStr = invitation.getCreatedAt() != null ? invitation.getCreatedAt().format(fmt) : "";
        String expiresAtStr = invitation.getExpiresAt() != null ? invitation.getExpiresAt().format(fmt) : "";

        // Relative expiry
        String relative = "";
        if (invitation.getExpiresAt() != null) {
            Duration dur = Duration.between(java.time.LocalDateTime.now(), invitation.getExpiresAt());
            long days = dur.toDays();
            if (dur.isNegative()) {
                relative = "(Expired)";
            } else if (days > 1) {
                relative = String.format("(Expires in %d days)", days);
            } else if (days == 1) {
                relative = "(Expires tomorrow)";
            } else {
                long hours = dur.toHours();
                if (hours >= 1) relative = String.format("(Expires in %d hours)", hours);
                else relative = "(Expires today)";
            }
        }

        String body = buildEmailBody(invitation.getEmail(), orgName, projectName, invitation.getRole().name(), createdAtStr, expiresAtStr, relative, acceptUrl);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        String from = mailFrom != null && !mailFrom.isBlank() ? mailFrom : null;
        if (from != null) {
            helper.setFrom(from);
        }

        helper.setTo(invitation.getEmail());
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }

    private String buildEmailBody(String email, String orgName, String projectName, String role, String createdAt, String expiresAt, String relativeExpiry, String acceptUrl) {
        return "<html><body>"
                + "<p>Hi,</p>"
                + "<p>You have been invited to join <strong>" + orgName + "</strong> as <strong>" + role + "</strong> on the project <strong>" + projectName + "</strong>.</p>"
                + (createdAt != null && !createdAt.isBlank() ? "<p>Created: <strong>" + createdAt + "</strong></p>" : "")
                + (expiresAt != null && !expiresAt.isBlank() ? "<p>Invitation expires: <strong>" + expiresAt + "</strong> " + (relativeExpiry != null ? "<span>" + relativeExpiry + "</span>" : "") + "</p>" : "")
                + "<p>To accept the invitation and create your account, click the link below:</p>"
                + "<p><a href=\"" + acceptUrl + "\">Accept Invitation</a></p>"
                + "<p>If you did not expect this invitation, you can ignore this email.</p>"
                + "<p>Thanks,<br/>The BugFlow Team</p>"
                + "</body></html>";
    }
}
