package com.group3.BilliardManagementSystem.MinhChau.repository;
import com.group3.BilliardManagementSystem.Entity.SessionTimeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionTimeLogRepository
        extends JpaRepository<SessionTimeLog, Long> {

    List<SessionTimeLog> findBySession_IdOrderByEventTimeAsc(Long sessionId);
}