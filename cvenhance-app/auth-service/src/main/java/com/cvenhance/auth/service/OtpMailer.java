package com.cvenhance.auth.service;

import com.cvenhance.auth.util.EmailBodies;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpMailer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtpMailer.class);
    private static final String SUBJECT = "Your verification code";

    private final MailService mailService;

    public void send(String toEmail, String otp) {
        String text = "Your verification code is " + otp + ". It expires in 5 minutes.";
        String html = mailService.renderEmail(EmailBodies.otp(otp));
        if (!mailService.sendHtml(toEmail, SUBJECT, text, html)) {
            LOGGER.warn("Mail not sent — OTP for {} is {}", toEmail, otp);
        }
    }
}
