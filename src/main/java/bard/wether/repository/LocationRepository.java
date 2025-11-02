package bard.wether.repository;

import bard.wether.entity.Location;
import bard.wether.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class LocationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Location save(Location location) {
        if (location.getId() == null) {
            entityManager.persist(location);
            entityManager.flush();
            return location;
        } else {
            return entityManager.merge(location);
        }
    }

    //дублирую
    public List<Location> findByUser(User user) {
        TypedQuery<Location> query = entityManager.createQuery(
                "SELECT l FROM Location l WHERE l.user = :user", Location.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    public Optional<Location> findByIdAndUser(Long id, User user) {
        try {
            TypedQuery<Location> query = entityManager.createQuery(
                    "SELECT l FROM Location l WHERE l.id = :id AND l.user = :user", Location.class);
            query.setParameter("id", id);
            query.setParameter("user", user);
            List<Location> locations = query.getResultList();
            return locations.isEmpty() ? Optional.empty() : Optional.of(locations.get(0));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean deleteByIdAndUser(Long locationId, User user) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "DELETE FROM Location l WHERE l.id = :id AND l.user = :user", Long.class);
            query.setParameter("id", locationId);
            query.setParameter("user", user);

            int deletedCount = query.executeUpdate();
            return deletedCount > 0;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении локации: " + e.getMessage(), e);
        }
    }

    public void delete(Location location) {
        try {
            // Проверяем, attached ли объект к persistence context
            if (entityManager.contains(location)) {
                entityManager.remove(location);
            } else {
                // Если detached, сначала делаем merge, потом удаляем
                Location attachedLocation = entityManager.merge(location);
                entityManager.remove(attachedLocation);
            }
            // Не вызываем flush() здесь - это сделает транзакция
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении локации: " + e.getMessage(), e);
        }
    }


}
