package ru.langgame.mediator;

import ru.langgame.mediator.dto.AuthResponse;
import ru.langgame.mediator.dto.LoginRequest;
import ru.langgame.mediator.dto.RegisterRequest;

/** Контракт аутентификации (Control → Mediator). */
public interface IAuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
