package bard.wether.repository;

import bard.wether.entity.Location;
import bard.wether.entity.User;
import bard.wether.exceptions.DataAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class LocationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Location save(Location location) {
        try {
            if (location.getId() == null) {
                entityManager.persist(location);
                return location;
            } else {
                return entityManager.merge(location);
            }
        } catch (Exception e) {
            log.error("Ошибка при сохранении локации: {}", location, e);
            throw new DataAccessException("Ошибка при сохранении локации", e);
        }
    }

    public List<Location> findByUser(User user) {
        try {
            TypedQuery<Location> query = entityManager.createQuery(
                    "SELECT l FROM Location l WHERE l.user = :user ORDER BY l.id", Location.class);
            query.setParameter("user", user);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Ошибка при поиске локаций пользователя: {}", user.getId(), e);
            throw new DataAccessException("Ошибка при поиске локаций пользователя", e);
        }
    }

    public Optional<Location> findByIdAndUser(Long id, User user) {
        try {
            TypedQuery<Location> query = entityManager.createQuery(
                    "SELECT l FROM Location l WHERE l.id = :id AND l.user = :user", Location.class);
            query.setParameter("id", id);
            query.setParameter("user", user);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }  catch (Exception e) {
            log.error("ОШИБКА БД при поиске location. ID: {}, User: {}", id, user.getId(), e);
            throw new DataAccessException("Ошибка при поиске location.", e);
        }
    }

    @Transactional
    public void delete(Location location) {
        try {
            if (entityManager.contains(location)) {
                entityManager.remove(location);
            } else {
                Location attachedLocation = entityManager.merge(location);
                entityManager.remove(attachedLocation);
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении локации. ID: {}", location.getId(), e);
            throw new DataAccessException("Ошибка при удалении локации", e);
        }
    }


    public List<Location> findByUserWithPagination(User user, int size, int offset) {
        try {
            String hql = "FROM Location l WHERE l.user = :user ORDER BY l.id";

            return entityManager.createQuery(hql, Location.class)
                    .setParameter("user", user)
                    .setFirstResult(offset)
                    .setMaxResults(size)
                    .getResultList();
        } catch (Exception e) {
            log.error("Ошибка при пагинации локаций пользователя: {}", user.getId(), e);
            throw new DataAccessException("Error while getting locations with pagination", e);
        }
    }

    public long countByUser(User user) {
        try {
            String hql = "SELECT COUNT(l) FROM Location l WHERE l.user = :user";

            return entityManager.createQuery(hql, Long.class)
                    .setParameter("user", user)
                    .getSingleResult();
        } catch (Exception e) {
            log.error("Ошибка при подсчете локаций пользователя: {}", user.getId(), e);
            throw new DataAccessException("Ошибка при подсчете локаций пользователя", e);
        }
    }
}
