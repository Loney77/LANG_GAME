package ru.skfu.langgame.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skfu.langgame.entity.Theme;

/** Репозиторий тем (Foundation). */
public interface IThemeRepository extends JpaRepository<Theme, Long> {
}
