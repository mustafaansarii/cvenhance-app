package com.cvenhance.common.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

    Page<AuditEvent> findAllByActorEmailOrderByCreatedAtDesc(String actorEmail, Pageable pageable);

    Page<AuditEvent> findAllByActionOrderByCreatedAtDesc(AuditAction action, Pageable pageable);

    @Query("SELECT e FROM AuditEvent e WHERE "
            + "(:keyword IS NULL OR LOWER(e.actorEmail) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(e.targetType) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(e.targetId) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
            + "AND (:action IS NULL OR e.action = :action) "
            + "ORDER BY e.createdAt DESC")
    Page<AuditEvent> search(@Param("keyword") String keyword, @Param("action") AuditAction action, Pageable pageable);
}

