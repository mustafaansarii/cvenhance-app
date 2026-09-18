package com.cvenhance.doc.dto.response;

import com.cvenhance.doc.dto.constants.DocTemplateStatus;
import com.cvenhance.doc.dto.constants.DocType;
import com.cvenhance.doc.dto.constants.SubscriptionType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserDocMetadata {

    private Long id;
    private Long sourceTemplateId;
    private String templateCode;
    private String name;
    private DocType type;
    private SubscriptionType subscriptionType;
    private String description;
    private DocTemplateStatus status;
    private String pdfUrl;
    private String imageUrl;
    private String errorMessage;
    private boolean unlocked;
    private Instant createdAt;
    private Instant updatedAt;
}
