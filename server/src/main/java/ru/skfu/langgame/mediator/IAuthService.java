package ru.skfu.langgame.mediator;

import ru.skfu.langgame.mediator.dto.AuthResponse;
import ru.skfu.langgame.mediator.dto.LoginRequest;
import ru.skfu.langgame.mediator.dto.RegisterRequest;

/** Контракт аутентификации (Control → Mediator). */
public interface IAuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
