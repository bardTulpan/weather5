package bard.wether.service;

import bard.wether.entity.Session;
import bard.wether.entity.User;
import bard.wether.exceptions.NotFoundException;
import bard.wether.exceptions.SessionExpiredException;
import bard.wether.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class SessionService {

    @Value("${session.timeout.hours}")
    private int SESSION_TIMEOUT_HOURS;

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session createSession(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        Session session = new Session(user, LocalDateTime.now().plusHours(SESSION_TIMEOUT_HOURS));
        return sessionRepository.save(session);
    }

    public User getUserBySession(UUID sessionId) {
        Session session = sessionRepository.findById(sessionId);
        if (session == null) {
            throw new NotFoundException("session not found");
        } else if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new SessionExpiredException("session expired");
        }

        session.setExpiresAt(LocalDateTime.now().plusHours(SESSION_TIMEOUT_HOURS));
        sessionRepository.save(session);

        return session.getUser();
    }

    public void destroySession(UUID sessionId) {
        sessionRepository.deleteById(sessionId);
    }


}
