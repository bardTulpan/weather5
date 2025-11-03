package bard.wether.service;

import bard.wether.entity.Session;
import bard.wether.entity.User;
import bard.wether.exceptions.NotFoundException;
import bard.wether.exceptions.SessionExpiredException;
import bard.wether.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session createSession(User user) {
        Session session = new Session(user, LocalDateTime.now().plusHours(2));
        return sessionRepository.save(session);
    }

    public User validateSession(UUID sessionId) {
        Session session = sessionRepository.findById(sessionId);
        if (session == null || session.getExpiresAt().isBefore(LocalDateTime.now())) { //добавить ошибку для ession.getExpiresAt().isBefore(LocalDateTime.now()
            throw new NotFoundException("session not found");
        } else if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new SessionExpiredException("session expired");
        }


        session.setExpiresAt(LocalDateTime.now().plusHours(2));
        sessionRepository.save(session);

        return session.getUser();
    }

    public void destroySession(UUID sessionId) {
        sessionRepository.deleteById(sessionId);
    }


}
