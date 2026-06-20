package ru.langgame.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.langgame.foundation.IThemeRepository;
import ru.langgame.mediator.dto.ThemeDto;

import java.util.List;

/** Чтение списка тем. */
@Service
@RequiredArgsConstructor
public class ThemeService {

    private final IThemeRepository themes;

    public List<ThemeDto> findAll() {
        return themes.findAll().stream()
                .map(t -> new ThemeDto(t.getId(), t.getName(), t.getDescription()))
                .toList();
    }
}
