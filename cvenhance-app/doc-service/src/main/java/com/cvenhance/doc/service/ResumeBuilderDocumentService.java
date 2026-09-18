package com.cvenhance.doc.service;

import com.cvenhance.doc.dto.request.SaveResumeBuilderDocumentRequest;

import com.cvenhance.doc.entity.ResumeBuilderDocument;
import com.cvenhance.doc.entity.ResumeBuilderTemplate;
import com.cvenhance.common.exception.ApiException;
import com.cvenhance.doc.repo.ResumeBuilderDocumentRepository;
import com.cvenhance.doc.client.SubscriptionServiceClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResumeBuilderDocumentService {

    @Autowired
    private ResumeBuilderDocumentRepository documentRepository;

    @Autowired
    private ResumeBuilderTemplateService templateService;

    @Autowired
    private SubscriptionServiceClient subscriptionServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DocTemplateService docTemplateService;

    @Transactional
    public ResumeBuilderDocument open(String ownerEmail, String templateCode) {
        ResumeBuilderTemplate template = templateService.getActive(templateCode);
        return documentRepository.findFirstByOwnerEmailAndTemplateCodeOrderByUpdatedAtDesc(ownerEmail, template.getTemplateCode())
                .orElseGet(() -> {
                    ResumeBuilderDocument document = new ResumeBuilderDocument();
                    document.setOwnerEmail(ownerEmail);
                    document.setTemplateCode(template.getTemplateCode());
                    document.setTemplateVersion(template.getVersion());
                    document.setName(template.getName() + " resume");
                    return documentRepository.save(document);
                });
    }

    @Transactional(readOnly = true)
    public ResumeBuilderDocument getOwned(String ownerEmail, Long id) {
        return documentRepository.findByIdAndOwnerEmail(id, ownerEmail)
                .orElseThrow(() -> ApiException.notFound("Resume builder document not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ResumeBuilderDocument> listOwned(String ownerEmail) {
        return documentRepository.findAllByOwnerEmailOrderByUpdatedAtDesc(ownerEmail);
    }

    @Transactional
    public ResumeBuilderDocument save(String ownerEmail, Long id, SaveResumeBuilderDocumentRequest request) {
        validateDocument(request);
        ResumeBuilderDocument document = getOwned(ownerEmail, id);
        if (request.getName() != null && !request.getName().isBlank()) {
            document.setName(request.getName().trim());
        }
        // Only config is persisted per document; resume content lives in the user's profile.
        document.setSectionOrderJson(writeJson(request.getSectionOrder()));
        document.setEditorSettingsJson(writeJson(request.getEditorSettings()));
        return documentRepository.save(document);
    }

    @Transactional
    @com.cvenhance.common.audit.Auditable(
            action = com.cvenhance.common.audit.AuditAction.TEMPLATE_CLAIMED,
            actor = "#ownerEmail", targetType = "RESUME_BUILDER_DOCUMENT", targetId = "#id",
            detail = "'template=' + #result.templateCode")
    public ResumeBuilderDocument claim(String ownerEmail, Long id) {
        ResumeBuilderDocument document = getOwned(ownerEmail, id);
        
        boolean free = docTemplateService.isFreeTemplate(document.getTemplateCode());
        boolean wasUnlocked = free || subscriptionServiceClient.isUnlocked(ownerEmail, document.resumeKey());
        if (!free && !subscriptionServiceClient.unlock(ownerEmail, document.resumeKey())) {
            throw ApiException.paymentRequired("Upgrade your plan to download this resume");
        }
        if (!wasUnlocked) {
            
        }
        return document;
    }

    public boolean isUnlocked(String ownerEmail, ResumeBuilderDocument document) {
        return docTemplateService.isFreeTemplate(document.getTemplateCode())
                || subscriptionServiceClient.isUnlocked(ownerEmail, document.resumeKey());
    }

    public JsonNode readJson(String value) {
        try {
            return objectMapper.readTree(value);
        } catch (Exception exception) {
            throw ApiException.badData("Stored resume builder document is invalid");
        }
    }

    private void validateDocument(SaveResumeBuilderDocumentRequest request) {
        if (!request.getSectionOrder().isArray()) {
            throw ApiException.badData("sectionOrder must be an array");
        }
        if (!request.getEditorSettings().isObject()) {
            throw ApiException.badData("editorSettings must be an object");
        }
    }

    private String writeJson(JsonNode value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw ApiException.badData("Invalid resume builder document data");
        }
    }
}
