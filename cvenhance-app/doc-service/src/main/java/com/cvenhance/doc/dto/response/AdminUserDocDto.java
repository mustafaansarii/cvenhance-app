package com.cvenhance.doc.dto.response;

import com.cvenhance.doc.dto.constants.DocTemplateStatus;
import com.cvenhance.doc.dto.constants.DocType;
import com.cvenhance.doc.dto.constants.SubscriptionType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AdminUserDocDto {
    private Long id;
    private String ownerEmail;
    private String templateCode;
    private String name;
    private DocType type;
    private SubscriptionType subscriptionType;
    private DocTemplateStatus status;
    private String pdfUrl;
    private String imageUrl;
    private Instant createdAt;
    private Instant updatedAt;
}
