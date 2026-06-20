package ru.langgame.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.langgame.mediator.IAuthService;
import ru.langgame.mediator.dto.AuthResponse;
import ru.langgame.mediator.dto.LoginRequest;
import ru.langgame.mediator.dto.RegisterRequest;

/** Аутентификация: регистрация и вход (публичные эндпоинты). */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
@SecurityRequirements   // не требует JWT
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового игрока")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Вход и получение JWT")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
