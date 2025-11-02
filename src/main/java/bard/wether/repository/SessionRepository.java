package bard.wether.repository;

import bard.wether.entity.Session;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@Transactional
public class SessionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Session save(Session session) {
        if (session.getId() == null) {
            entityManager.persist(session);
            return session;
        } else {
            return entityManager.merge(session);
        }
    }

    public Session findById(UUID id) {
        return entityManager.find(Session.class, id);
    }

    public void deleteById(UUID id) {
        Session session = findById(id);
        if (session != null) entityManager.remove(session);
    }

    public void deleteExpired() {
        entityManager.createQuery("DELETE FROM Session s WHERE s.expiresAt < :now")
                .setParameter("now", LocalDateTime.now())
                .executeUpdate();
    }
}
