package com.cvenhance.doc.controller;

import com.cvenhance.doc.dto.constants.DocType;
import com.cvenhance.doc.dto.request.CompileDocRequest;
import com.cvenhance.common.dto.PageQuery;
import com.cvenhance.doc.dto.request.SaveUserDocRequest;
import com.cvenhance.common.dto.PageResponse;
import com.cvenhance.doc.dto.response.UserDocMetadata;
import com.cvenhance.doc.dto.response.UserDocResponse;
import com.cvenhance.doc.dtoApi.UserDocDtoApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-docs")
public class UserDocController {

    @Autowired
    private UserDocDtoApi userDocDtoApi;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDocResponse save(Authentication authentication, @RequestBody SaveUserDocRequest request) {
        return userDocDtoApi.save(authentication.getName(), request);
    }

    @GetMapping
    public PageResponse<UserDocMetadata> list(Authentication authentication, PageQuery query,
                                              @RequestParam(required = false) DocType type) {
        return userDocDtoApi.getUserDocs(authentication.getName(), query, type);
    }

    @GetMapping("/{id}")
    public UserDocResponse get(Authentication authentication, @PathVariable Long id) {
        return userDocDtoApi.get(authentication.getName(), id);
    }

    @PatchMapping(value = "/{id}/compile", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] compile(Authentication authentication, @PathVariable Long id,
                          @RequestBody CompileDocRequest request) {
        return userDocDtoApi.compileAndUpdate(authentication.getName(), id, request);
    }

    @PostMapping(value = "/{id}/unlock", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] unlock(Authentication authentication, @PathVariable Long id) {
        return userDocDtoApi.unlock(authentication.getName(), id);
    }

    @PostMapping("/by-template/{code}")
    public UserDocResponse openByTemplate(Authentication authentication, @PathVariable String code) {
        return userDocDtoApi.openByTemplate(authentication.getName(), code);
    }

    @PostMapping("/{id}/claim")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void claim(Authentication authentication, @PathVariable Long id) {
        userDocDtoApi.claim(authentication.getName(), id);
    }

}
