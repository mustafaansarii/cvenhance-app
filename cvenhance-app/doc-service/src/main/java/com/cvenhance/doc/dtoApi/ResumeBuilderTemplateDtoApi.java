package com.cvenhance.doc.dtoApi;

import com.cvenhance.doc.dto.request.UpsertResumeBuilderTemplateRequest;
import com.cvenhance.doc.dto.response.ResumeBuilderTemplateResponse;
import com.cvenhance.doc.entity.ResumeBuilderTemplate;
import com.cvenhance.doc.service.ResumeBuilderTemplateService;
import com.cvenhance.doc.util.AbstractDtoUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResumeBuilderTemplateDtoApi extends AbstractDtoUtil {

    private final ResumeBuilderTemplateService templateService;

    public ResumeBuilderTemplateDtoApi(ResumeBuilderTemplateService templateService) {
        this.templateService = templateService;
    }

    public List<ResumeBuilderTemplateResponse> list() {
        return templateService.listActive().stream().map(this::toResponse).toList();
    }

    public ResumeBuilderTemplateResponse get(String code) {
        return toResponse(templateService.getActive(code));
    }

    public ResumeBuilderTemplateResponse upsert(UpsertResumeBuilderTemplateRequest request) {
        validate(request);
        return toResponse(templateService.upsert(request));
    }

    private ResumeBuilderTemplateResponse toResponse(ResumeBuilderTemplate template) {
        return ResumeBuilderTemplateResponse.builder()
                .id(template.getId())
                .templateCode(template.getTemplateCode())
                .name(template.getName())
                .description(template.getDescription())
                .imageUrl(template.getImageUrl())
                .active(template.isActive())
                .version(template.getVersion())
                .config(templateService.readConfig(template))
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
