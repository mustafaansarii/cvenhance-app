package com.cvenhance.auth.repo;

import com.cvenhance.auth.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    boolean existsByTokenId(String tokenId);

    Optional<UserSession> findByTokenId(String tokenId);

    void deleteByTokenId(String tokenId);

    List<UserSession> findByUserEmail(String userEmail);
}
