package bard.wether.repository;

import bard.wether.entity.Location;
import bard.wether.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> findAll() {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    public User findByLogin(String login) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.login = :login", User.class);
        query.setParameter("login", login);
        List<User> users = query.getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    public User save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            entityManager.flush(); // Принудительно сохраняем чтобы получить ID
            return user;
        } else {
            return entityManager.merge(user);
        }
    }

    public List<Location> findByUser(User user) {
        TypedQuery<Location> query = entityManager.createQuery(
                "SELECT l FROM Location l WHERE l.user = :user", Location.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

}
