package com.cvenhance.auth.dtoApi;

import com.cvenhance.auth.dto.MailRequest;
import com.cvenhance.auth.dto.ContactRequest;
import com.cvenhance.common.dto.MessageResponse;
import com.cvenhance.auth.service.ContactMailer;
import com.cvenhance.auth.service.MailService;
import com.cvenhance.auth.util.AbstractDtoUtil;
import com.cvenhance.auth.util.EmailBodies;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContactDtoApi extends AbstractDtoUtil {

    private final ContactMailer contactMailer;
    private final MailService mailService;

    @com.cvenhance.common.audit.Auditable(
            action = com.cvenhance.common.audit.AuditAction.CONTACT_SUBMITTED,
            actor = "#request.email")
    public MessageResponse submit(ContactRequest request) {
        validate(request);
        boolean ok = contactMailer.send(request);
        return MessageResponse.of(ok
                ? "Your message has been sent. We'll get back to you soon."
                : "Could not send your message right now. Please email support directly.");
    }

    public MessageResponse sendAdminMail(MailRequest request) {
        validate(request);
        String html = mailService.renderEmail(EmailBodies.message(request.getSubject(), request.getMessage()));
        boolean ok = mailService.sendHtml(request.getTo(), request.getSubject(), request.getMessage(), html);
        return MessageResponse.of(ok
                ? "Email sent to " + request.getTo()
                : "Could not send the email (mail not configured?).");
    }
}
