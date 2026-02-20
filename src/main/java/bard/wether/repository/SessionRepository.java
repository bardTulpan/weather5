package bard.wether.repository;

import bard.wether.entity.Session;
import bard.wether.exceptions.DataAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Repository
public class SessionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Session save(Session session) {
        try {
            if (session.getId() == null) {
                entityManager.persist(session);
                return session;
            } else {
                return entityManager.merge(session);
            }
        } catch (Exception e) {
            log.error("Ошибка при сохранении сессии: {}", session.getId(), e);
            throw new DataAccessException("Ошибка при сохранении сессии", e);
        }
    }

    public Session findById(UUID id) {
        try {
            return entityManager.find(Session.class, id);
        } catch (Exception e) {
            log.error("Ошибка при поиске сессии: {}", id, e);
            throw new DataAccessException("Ошибка при поиске сессии", e);
        }
    }

    @Transactional
    public void deleteById(UUID id) {
        try {
            Session session = findById(id);
            if (session != null) {
                entityManager.remove(session);
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении сессии: {}", id, e);
            throw new DataAccessException("Ошибка при удалении сессии", e);
        }
    }


    @Transactional
    public void deleteExpired() {
        try {
            int deletedCount = entityManager.createQuery("DELETE FROM Session s WHERE s.expiresAt < :now")
                    .setParameter("now", LocalDateTime.now())
                    .executeUpdate();
            log.info("Удалено просроченных сессий: {}", deletedCount);
        } catch (Exception e) {
            log.error("Ошибка при удалении просроченных сессий", e);
            throw new DataAccessException("Ошибка при удалении просроченных сессий", e);
        }
    }
}
