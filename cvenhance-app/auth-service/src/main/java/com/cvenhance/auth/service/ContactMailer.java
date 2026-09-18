package com.cvenhance.auth.service;

import com.cvenhance.auth.config.AppProperties;
import com.cvenhance.auth.dto.ContactRequest;
import com.cvenhance.auth.util.EmailBodies;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ContactMailer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactMailer.class);

    private final MailService mailService;
    private final AppProperties appProperties;

    private static final DateTimeFormatter SUBJECT_TIME =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss");
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    public boolean send(ContactRequest request) {
        String subject = "New contact query from " + request.getName()
                + " (" + request.getEmail() + ") · " + LocalDateTime.now(IST).format(SUBJECT_TIME);
        String text = "From: " + request.getName() + " (" + request.getEmail() + ")\n\n" + request.getMessage();
        String html = mailService.renderEmail(
                EmailBodies.contact(request.getName(), request.getEmail(), request.getMessage()));

        boolean sent = mailService.sendHtml(appProperties.getMailSupportAddress(), subject, text, html, request.getEmail());
        if (!sent) {
            LOGGER.warn("Contact query from {} could not be delivered to support", request.getEmail());
        }
        return sent;
    }
}
