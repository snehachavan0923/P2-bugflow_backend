package com.bugflow.service;

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
            java.time.Duration dur = java.time.Duration.between(java.time.LocalDateTime.now(), invitation.getExpiresAt());
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
