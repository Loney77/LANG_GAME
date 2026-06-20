package ru.langgame.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.langgame.config.AuthUser;
import ru.langgame.mediator.ISessionService;
import ru.langgame.mediator.dto.SessionDto;

import java.util.List;

/** История игровых сессий текущего пользователя. */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Сессии")
public class SessionController {

    private final ISessionService sessionService;

    @GetMapping("/me")
    @Operation(summary = "Моя история игр")
    public List<SessionDto> myHistory(@AuthenticationPrincipal AuthUser user) {
        return sessionService.historyOf(user.id());
    }
}
