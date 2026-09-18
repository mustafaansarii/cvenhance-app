package com.cvenhance.doc.repo;

import com.cvenhance.doc.entity.ResumeBuilderTemplate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeBuilderTemplateRepository extends JpaRepository<ResumeBuilderTemplate, Long> {

    Optional<ResumeBuilderTemplate> findByTemplateCode(String templateCode);

    List<ResumeBuilderTemplate> findAllByActiveTrue(Sort sort);
}
