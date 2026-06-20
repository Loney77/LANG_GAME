package ru.langgame.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.langgame.entity.User;

import java.util.Optional;

/** Репозиторий пользователей (Foundation). */
public interface IUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
