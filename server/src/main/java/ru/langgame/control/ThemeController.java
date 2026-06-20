package ru.langgame.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.langgame.mediator.ThemeService;
import ru.langgame.mediator.dto.ThemeDto;

import java.util.List;

/** Темы слов. */
@RestController
@RequestMapping("/api/themes")
@RequiredArgsConstructor
@Tag(name = "Темы")
public class ThemeController {

    private final ThemeService themeService;

    @GetMapping
    @Operation(summary = "Список тем")
    public List<ThemeDto> findAll() {
        return themeService.findAll();
    }
}
