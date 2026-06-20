package ru.langgame.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.langgame.entity.GameType;

import java.util.Optional;

/** Репозиторий типов игр (Foundation). */
public interface IGameTypeRepository extends JpaRepository<GameType, Long> {

    Optional<GameType> findByCode(String code);
}
