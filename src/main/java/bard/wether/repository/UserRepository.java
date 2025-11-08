package bard.wether.repository;

import bard.wether.entity.Location;
import bard.wether.entity.User;
import bard.wether.exceptions.DataAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

//    public Optional<User> findByLogin(String login) {
//        try {
//            TypedQuery<User> query = entityManager.createQuery(
//                    "SELECT u FROM User u WHERE u.login = :login", User.class);
//            query.setParameter("login", login);
//
//            return query.getResultStream().findFirst();
//
//        } catch (Exception e) {
//            log.error("Ошибка при поиске по пользователя по логину: {}", login, e);
//            throw new DataAccessException("Ошибка при поиске по пользователя по логину", e);
//        }
//    }

    public Optional<User> findByLogin(String login) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.login = :login", User.class);
            query.setParameter("login", login);
            query.setMaxResults(1);

            List<User> users = query.getResultList();
            return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));

        } catch (Exception e) {
            log.error("Ошибка при поиске пользователя по логину: {}", login, e);
            throw new DataAccessException("Ошибка при поиске пользователя по логину", e);
        }
    }

    @Transactional
    public User save(User user) {
        try {
            if (user.getId() == null) {
                entityManager.persist(user);
                return user;
            } else {
                return entityManager.merge(user);
            }
        } catch (Exception e) {
            log.error("Ошибка при сохранении полозователя. User: {}", user.getId(), e);
            throw new DataAccessException("Ошибка при сохранении полозователя", e);
        }
    }

}
