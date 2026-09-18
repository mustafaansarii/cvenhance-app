package com.cvenhance.auth.controller;

import com.cvenhance.auth.dto.MailRequest;
import com.cvenhance.auth.dto.ContactRequest;
import com.cvenhance.common.dto.MessageResponse;
import com.cvenhance.auth.dtoApi.ContactDtoApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactDtoApi contactDtoApi;

    @PostMapping
    public MessageResponse submit(@RequestBody ContactRequest request) {
        return contactDtoApi.submit(request);
    }

    @PostMapping("/admin/send")
    public MessageResponse adminSend(@RequestBody MailRequest request) {
        return contactDtoApi.sendAdminMail(request);
    }
}
