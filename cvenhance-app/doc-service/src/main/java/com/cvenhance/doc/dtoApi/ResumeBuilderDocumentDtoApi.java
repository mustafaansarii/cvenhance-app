package com.cvenhance.doc.dtoApi;

import com.cvenhance.doc.dto.request.SaveResumeBuilderDocumentRequest;
import com.cvenhance.doc.dto.response.ResumeBuilderDocumentResponse;
import com.cvenhance.doc.entity.DocTemplate;
import com.cvenhance.doc.entity.ResumeBuilderDocument;
import com.cvenhance.doc.service.ResumeBuilderDocumentService;

import com.cvenhance.doc.service.UserDocService;
import com.cvenhance.doc.service.DocTemplateService;

import com.cvenhance.doc.util.AbstractDtoUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResumeBuilderDocumentDtoApi extends AbstractDtoUtil {

    private final ResumeBuilderDocumentService resumeBuilderDocumentService;
    private final UserDocService userDocService;
    private final DocTemplateService documentService;

    public ResumeBuilderDocumentDtoApi(ResumeBuilderDocumentService resumeBuilderDocumentService, UserDocService userDocService, DocTemplateService documentService) {
        this.resumeBuilderDocumentService = resumeBuilderDocumentService;
        this.userDocService = userDocService;
        this.documentService = documentService;
    }

    public ResumeBuilderDocumentResponse open(String ownerEmail, String templateCode) {
        ResumeBuilderDocument document = resumeBuilderDocumentService.open(ownerEmail, templateCode);
        documentService.findTemplate(templateCode)
                .ifPresent(docTemplate -> userDocService.findOrCreateForTemplate(ownerEmail, docTemplate));
        return buildResumeResponse(ownerEmail, document);
    }

    public ResumeBuilderDocumentResponse get(String ownerEmail, Long id) {
        return buildResumeResponse(ownerEmail, resumeBuilderDocumentService.getOwned(ownerEmail, id));
    }

    public List<ResumeBuilderDocumentResponse> list(String ownerEmail) {
        return resumeBuilderDocumentService.listOwned(ownerEmail).stream().map(document -> buildResumeResponse(ownerEmail, document)).toList();
    }

    public ResumeBuilderDocumentResponse save(String ownerEmail, Long id, SaveResumeBuilderDocumentRequest request) {
        validate(request);
        ResumeBuilderDocument document =  resumeBuilderDocumentService.save(ownerEmail, id, request);
        return buildResumeResponse(ownerEmail, document);
    }

    public ResumeBuilderDocumentResponse claim(String ownerEmail, Long id) {
        ResumeBuilderDocument document = resumeBuilderDocumentService.getOwned(ownerEmail, id);
        DocTemplate docTemplate = documentService.getTemplate(document.getTemplateCode());
        userDocService.findOrCreateForTemplate(ownerEmail, docTemplate);
        resumeBuilderDocumentService.claim(ownerEmail, id);
        return buildResumeResponse(ownerEmail, document);
    }

//---------------------Helper Methods-------------------------------

    private ResumeBuilderDocumentResponse buildResumeResponse(String ownerEmail, ResumeBuilderDocument document) {
        return ResumeBuilderDocumentResponse.builder()
                .id(document.getId())
                .templateCode(document.getTemplateCode())
                .templateVersion(document.getTemplateVersion())
                .name(document.getName())
                .sectionOrder(resumeBuilderDocumentService.readJson(document.getSectionOrderJson()))
                .editorSettings(resumeBuilderDocumentService.readJson(document.getEditorSettingsJson()))
                .unlocked(resumeBuilderDocumentService.isUnlocked(ownerEmail, document))
                .subscriptionType(documentService.findTemplate(document.getTemplateCode())
                        .map(DocTemplate::getSubscriptionType)
                        .orElse(com.cvenhance.doc.dto.constants.SubscriptionType.PAID))
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
