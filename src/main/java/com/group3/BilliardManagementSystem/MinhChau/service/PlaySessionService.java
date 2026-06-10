package com.group3.BilliardManagementSystem.MinhChau.service;

import com.group3.BilliardManagementSystem.Entity.BilliardTable;
import com.group3.BilliardManagementSystem.Entity.PlaySession;
import com.group3.BilliardManagementSystem.Entity.SessionTimeLog;
import com.group3.BilliardManagementSystem.Entity.User;
import com.group3.BilliardManagementSystem.MinhChau.Realtime.TableRealtimeService;
import com.group3.BilliardManagementSystem.MinhChau.repository.*;
import com.group3.BilliardManagementSystem.ThaiDuyVu.Repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaySessionService {

    private final BilliardTableRepository tableRepository;
    private final PlaySessionRepository sessionRepository;
    private final SessionTimeLogRepository timeLogRepository;
    private final TableRealtimeService realtimeService;
    private final UserRepository userRepository;

    public PlaySession openSession(Long tableId) {
        BilliardTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn"));

        if (table.getStatus() != BilliardTable.TableStatus.AVAILABLE) {
            throw new IllegalArgumentException("Bàn đang không trống");
        }

        LocalDateTime now = LocalDateTime.now();

        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user đăng nhập"));

        PlaySession session = new PlaySession();
        session.setTable(table);
        session.setOpenedBy(user);
        session.setStartTime(now);
        session.setStatus(PlaySession.SessionStatus.ACTIVE);
        session.setCreatedAt(now);
        session.setUpdatedAt(now);

        PlaySession savedSession = sessionRepository.save(session);

        SessionTimeLog log = new SessionTimeLog();
        log.setSession(savedSession);
        log.setEventType(SessionTimeLog.EventType.SESSION_START);
        log.setEventTime(now);
        log.setCreatedAt(now);
        log.setUpdatedAt(now);
        timeLogRepository.save(log);

        table.setStatus(BilliardTable.TableStatus.OCCUPIED);
        table.setUpdatedAt(now);
        tableRepository.save(table);

        realtimeService.sendTableUpdate(table);

        return savedSession;
    }

    public PlaySession pauseSession(Long sessionId) {
        PlaySession session = getSession(sessionId);

        if (session.getStatus() != PlaySession.SessionStatus.ACTIVE) {
            throw new IllegalArgumentException("Chỉ phiên ACTIVE mới được tạm dừng");
        }

        LocalDateTime now = LocalDateTime.now();

        SessionTimeLog log = new SessionTimeLog();
        log.setSession(session);
        log.setEventType(SessionTimeLog.EventType.PAUSE);
        log.setEventTime(now);
        log.setCreatedAt(now);
        log.setUpdatedAt(now);
        timeLogRepository.save(log);

        session.setStatus(PlaySession.SessionStatus.PAUSED);
        session.setUpdatedAt(now);

        BilliardTable table = session.getTable();
        table.setStatus(BilliardTable.TableStatus.PAUSED);
        table.setUpdatedAt(now);

        PlaySession savedSession = sessionRepository.save(session);
        tableRepository.save(table);

        realtimeService.sendTableUpdate(table);

        return savedSession;
    }

    public PlaySession resumeSession(Long sessionId) {
        PlaySession session = getSession(sessionId);

        if (session.getStatus() != PlaySession.SessionStatus.PAUSED) {
            throw new IllegalArgumentException("Chỉ phiên PAUSED mới được tiếp tục");
        }

        LocalDateTime now = LocalDateTime.now();

        SessionTimeLog log = new SessionTimeLog();
        log.setSession(session);
        log.setEventType(SessionTimeLog.EventType.RESUME);
        log.setEventTime(now);
        log.setCreatedAt(now);
        log.setUpdatedAt(now);
        timeLogRepository.save(log);

        session.setStatus(PlaySession.SessionStatus.ACTIVE);
        session.setUpdatedAt(now);

        BilliardTable table = session.getTable();
        table.setStatus(BilliardTable.TableStatus.OCCUPIED);
        table.setUpdatedAt(now);

        PlaySession savedSession = sessionRepository.save(session);
        tableRepository.save(table);

        realtimeService.sendTableUpdate(table);

        return savedSession;
    }

    public PlaySession endSession(Long sessionId) {
        PlaySession session = getSession(sessionId);

        if (session.getStatus() == PlaySession.SessionStatus.ENDED ||
                session.getStatus() == PlaySession.SessionStatus.INVOICED) {
            throw new IllegalArgumentException("Phiên chơi đã kết thúc");
        }

        LocalDateTime now = LocalDateTime.now();

        long playingSeconds = getPlayingSeconds(sessionId);
        int playingMinutes = (int) Math.ceil(playingSeconds / 60.0);

        session.setEndTime(now);
        session.setActualPlayMinutes(playingMinutes);
        session.setTotalPlayAmount(
                session.getTable()
                        .getRatePerMinute()
                        .multiply(BigDecimal.valueOf(playingMinutes))
        );
        session.setStatus(PlaySession.SessionStatus.ENDED);
        session.setUpdatedAt(now);

        BilliardTable table = session.getTable();
        table.setStatus(BilliardTable.TableStatus.AVAILABLE);
        table.setUpdatedAt(now);

        SessionTimeLog log = new SessionTimeLog();
        log.setSession(session);
        log.setEventType(SessionTimeLog.EventType.SESSION_END);
        log.setEventTime(now);
        log.setSegmentSeconds(playingSeconds);
        log.setCreatedAt(now);
        log.setUpdatedAt(now);
        timeLogRepository.save(log);

        PlaySession savedSession = sessionRepository.save(session);
        tableRepository.save(table);

        realtimeService.sendTableUpdate(table);

        return savedSession;
    }

    public long getPlayingSeconds(Long sessionId) {
        PlaySession session = getSession(sessionId);

        LocalDateTime endTime = session.getEndTime() != null
                ? session.getEndTime()
                : LocalDateTime.now();

        List<SessionTimeLog> logs =
                timeLogRepository.findBySession_IdOrderByEventTimeAsc(sessionId);

        long activeSeconds = 0;
        LocalDateTime activeStart = session.getStartTime();

        for (SessionTimeLog log : logs) {
            if (log.getEventType() == SessionTimeLog.EventType.PAUSE) {
                if (activeStart != null) {
                    activeSeconds += Duration.between(activeStart, log.getEventTime()).getSeconds();
                    activeStart = null;
                }
            }

            if (log.getEventType() == SessionTimeLog.EventType.RESUME) {
                activeStart = log.getEventTime();
            }
        }

        if (activeStart != null && session.getStatus() != PlaySession.SessionStatus.PAUSED) {
            activeSeconds += Duration.between(activeStart, endTime).getSeconds();
        }

        return Math.max(activeSeconds, 0);
    }

    public PlaySession getActiveSessionByTable(Long tableId) {
        return sessionRepository
                .findFirstByTable_IdAndStatusInOrderByStartTimeDesc(
                        tableId,
                        List.of(
                                PlaySession.SessionStatus.ACTIVE,
                                PlaySession.SessionStatus.PAUSED
                        )
                )
                .orElse(null);
    }

    private PlaySession getSession(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiên chơi"));
    }
}