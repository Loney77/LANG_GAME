package ru.langgame.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.langgame.entity.Theme;

/** Репозиторий тем (Foundation). */
public interface IThemeRepository extends JpaRepository<Theme, Long> {
}
