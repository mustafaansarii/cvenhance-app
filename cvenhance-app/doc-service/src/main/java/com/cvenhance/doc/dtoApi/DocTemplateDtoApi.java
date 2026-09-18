package com.cvenhance.doc.dtoApi;

import com.cvenhance.doc.dto.request.CreateDocTemplateRequest;
import com.cvenhance.common.dto.PageQuery;
import com.cvenhance.doc.dto.response.DocTemplateMetadata;
import com.cvenhance.common.dto.PageResponse;
import com.cvenhance.doc.entity.DocTemplate;
import com.cvenhance.common.exception.ApiException;
import com.cvenhance.doc.service.DocTemplateService;
import com.cvenhance.doc.util.AbstractDtoUtil;
import com.cvenhance.common.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class DocTemplateDtoApi extends AbstractDtoUtil {

    private static final int MAX_BATCH = 50;
    private static final String DEFAULT_SORT = "createdAt";

    @Autowired
    private DocTemplateService docTemplateService;

    public List<DocTemplateMetadata> create(List<CreateDocTemplateRequest> requests) {
        if (Objects.isNull(requests) || requests.isEmpty()) {
            throw ApiException.badData("At least one template is required");
        }
        if (requests.size() > MAX_BATCH) {
            throw ApiException.badData("At most " + MAX_BATCH + " templates can be uploaded at once");
        }
        requests.forEach(AbstractDtoUtil::validate);
        return docTemplateService.createAll(requests).stream()
                .map(this::toMetadata)
                .toList();
    }

    public DocTemplateMetadata getMetadata(Long id) {
        return toMetadata(docTemplateService.getById(id));
    }

    public PageResponse<DocTemplateMetadata> listMetadata(PageQuery query,
                                                          com.cvenhance.doc.dto.constants.DocType type) {
        Pageable pageable = PageUtil.toPageable(query, DEFAULT_SORT);
        Page<DocTemplate> result = docTemplateService.list(query.getKeyword(), type, pageable);
        List<DocTemplateMetadata> content = result.getContent().stream().map(this::toMetadata).toList();
        return PageUtil.toResponse(result, content);
    }

    private DocTemplateMetadata toMetadata(DocTemplate template) {
        return DocTemplateMetadata.builder()
                .id(template.getId())
                .templateCode(template.getTemplateCode())
                .name(template.getName())
                .type(template.getType())
                .subscriptionType(template.getSubscriptionType())
                .description(template.getDescription())
                .status(template.getStatus())
                .imageUrl(template.getImageUrl())
                .errorMessage(template.getErrorMessage())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
