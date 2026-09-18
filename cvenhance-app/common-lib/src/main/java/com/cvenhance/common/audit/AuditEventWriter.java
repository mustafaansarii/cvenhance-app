package com.cvenhance.common.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AuditEventWriter {

    private static final Logger logger = LoggerFactory.getLogger(AuditEventWriter.class);

    private final AuditEventRepository auditEventRepository;

    public AuditEventWriter(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Async
    public void write(AuditEvent event) {
        try {
            auditEventRepository.save(event);
        } catch (Exception e) {
            logger.error("Failed to persist audit event {}", event.getAction(), e);
        }
    }
}

