package com.group3.BilliardManagementSystem.MinhChau.repository;
import com.group3.BilliardManagementSystem.Entity.PlaySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaySessionRepository
        extends JpaRepository<PlaySession, Long> {

    Optional<PlaySession> findFirstByTable_IdAndStatusInOrderByStartTimeDesc(
            Long tableId,
            List<PlaySession.SessionStatus> statuses
    );
}