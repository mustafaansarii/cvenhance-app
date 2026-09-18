package com.cvenhance.doc.controller;

import com.cvenhance.doc.dto.constants.DocType;
import com.cvenhance.doc.dto.request.CreateDocTemplateRequest;
import com.cvenhance.common.dto.PageQuery;
import com.cvenhance.doc.dto.response.DocTemplateMetadata;
import com.cvenhance.common.dto.PageResponse;
import com.cvenhance.doc.dtoApi.DocTemplateDtoApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/doc-templates")
public class DocTemplateController {

    @Autowired
    private DocTemplateDtoApi docTemplateDtoApi;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<DocTemplateMetadata> create(@RequestBody List<CreateDocTemplateRequest> requests) {
        return docTemplateDtoApi.create(requests);
    }

    @GetMapping("/{id}")
    public DocTemplateMetadata get(@PathVariable Long id) {
        return docTemplateDtoApi.getMetadata(id);
    }

    @GetMapping
    public PageResponse<DocTemplateMetadata> list(PageQuery query,
                                                  @RequestParam(required = false) DocType type) {
        return docTemplateDtoApi.listMetadata(query, type);
    }
}
