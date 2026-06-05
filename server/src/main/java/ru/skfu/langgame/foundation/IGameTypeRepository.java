package ru.skfu.langgame.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skfu.langgame.entity.GameType;

import java.util.Optional;

/** Репозиторий типов игр (Foundation). */
public interface IGameTypeRepository extends JpaRepository<GameType, Long> {

    Optional<GameType> findByCode(String code);
}
