package com.cvenhance.doc.dtoApi;

import com.cvenhance.doc.dto.constants.DocType;
import com.cvenhance.doc.dto.request.CompileDocRequest;
import com.cvenhance.common.dto.PageQuery;
import com.cvenhance.doc.dto.request.SaveUserDocRequest;
import com.cvenhance.common.dto.PageResponse;
import com.cvenhance.doc.dto.response.UserDocMetadata;
import com.cvenhance.doc.dto.response.UserDocResponse;
import com.cvenhance.doc.entity.UserDoc;
import com.cvenhance.doc.service.DocTemplateService;
import com.cvenhance.doc.client.SubscriptionServiceClient;
import com.cvenhance.doc.service.UserDocService;
import com.cvenhance.doc.util.AbstractDtoUtil;
import com.cvenhance.common.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDocDtoApi extends AbstractDtoUtil {

    private static final String DEFAULT_SORT = "updatedAt";

    @Autowired
    private UserDocService userDocService;

    @Autowired
    private SubscriptionServiceClient subscriptionServiceClient;

    @Autowired
    private DocTemplateService docTemplateService;

    public UserDocResponse save(String ownerEmail, SaveUserDocRequest request) {
        validate(request);
        UserDoc doc = userDocService.saveTemplateToAccount(ownerEmail, request.getTemplateId());
        return toResponse(doc, subscriptionServiceClient.unlockViewFor(ownerEmail));
    }

    public UserDocResponse openByTemplate(String ownerEmail, String templateCode) {
        UserDoc doc = userDocService.openByTemplateCode(ownerEmail, templateCode);
        return toResponse(doc, subscriptionServiceClient.unlockViewFor(ownerEmail));
    }

    public void claim(String ownerEmail, Long id) {
        userDocService.claim(ownerEmail, id);
    }


    public PageResponse<UserDocMetadata> getUserDocs(String ownerEmail, PageQuery query, DocType type) {
        Pageable pageable = PageUtil.toPageable(query, DEFAULT_SORT);
        Page<UserDoc> result = userDocService.getUserDocs(ownerEmail, query.getKeyword(), type, pageable);
        SubscriptionServiceClient.UnlockView unlockView = subscriptionServiceClient.unlockViewFor(ownerEmail);
        java.util.Set<String> freeCodes = docTemplateService.freeTemplateCodesAmong(
                result.getContent().stream().map(UserDoc::getTemplateCode).toList());
        List<UserDocMetadata> content = result.getContent().stream()
                .map((doc) -> toMetadata(doc, unlockView, freeCodes)).toList();
        return PageUtil.toResponse(result, content);
    }

    public UserDocResponse get(String ownerEmail, Long id) {
        UserDoc doc = userDocService.getOwned(ownerEmail, id);
        return toResponse(doc, subscriptionServiceClient.unlockViewFor(ownerEmail));
    }

    public byte[] compileAndUpdate(String ownerEmail, Long id, CompileDocRequest request) {
        validate(request);
        return userDocService.compileAndUpdate(ownerEmail, id, request.getLatexCode());
    }

    public byte[] unlock(String ownerEmail, Long id) {
        return userDocService.unlockAndCompile(ownerEmail, id);
    }

//-----------------------------------private methods-----------------------------------

    private UserDocMetadata toMetadata(UserDoc doc, SubscriptionServiceClient.UnlockView unlockView,
                                       java.util.Set<String> freeCodes) {
        return UserDocMetadata.builder()
                .id(doc.getId())
                .sourceTemplateId(doc.getSourceTemplateId())
                .templateCode(doc.getTemplateCode())
                .name(doc.getName())
                .type(doc.getType())
                .subscriptionType(doc.getSubscriptionType())
                .description(doc.getDescription())
                .status(doc.getStatus())
                .pdfUrl(doc.getPdfUrl())
                .imageUrl(doc.getImageUrl())
                .errorMessage(doc.getErrorMessage())
                .unlocked(freeCodes.contains(doc.getTemplateCode()) || unlockView.isUnlocked(doc.resumeKey()))
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    private UserDocResponse toResponse(UserDoc doc, SubscriptionServiceClient.UnlockView unlockView) {
        return UserDocResponse.builder()
                .id(doc.getId())
                .sourceTemplateId(doc.getSourceTemplateId())
                .templateCode(doc.getTemplateCode())
                .name(doc.getName())
                .type(doc.getType())
                .subscriptionType(doc.getSubscriptionType())
                .description(doc.getDescription())
                .latexCode(doc.getLatexCode())
                .status(doc.getStatus())
                .pdfUrl(doc.getPdfUrl())
                .imageUrl(doc.getImageUrl())
                .errorMessage(doc.getErrorMessage())
                .unlocked(docTemplateService.isFreeTemplate(doc.getTemplateCode())
                        || unlockView.isUnlocked(doc.resumeKey()))
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
