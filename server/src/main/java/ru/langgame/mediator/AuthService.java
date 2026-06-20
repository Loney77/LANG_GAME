package ru.langgame.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.langgame.config.JwtService;
import ru.langgame.entity.Role;
import ru.langgame.entity.User;
import ru.langgame.foundation.IUserRepository;
import ru.langgame.mediator.dto.AuthResponse;
import ru.langgame.mediator.dto.LoginRequest;
import ru.langgame.mediator.dto.RegisterRequest;
import ru.langgame.mediator.exception.ConflictException;
import ru.langgame.mediator.exception.UnauthorizedException;

/** Аутентификация: регистрация, вход, выдача JWT. */
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final IUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (users.existsByEmail(request.email())) {
            throw new ConflictException("Email уже занят");
        }
        if (users.existsByUsername(request.username())) {
            throw new ConflictException("Имя пользователя уже занято");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user = users.save(user);
        return new AuthResponse(jwtService.generate(user), user.getUsername(), user.getRole().name());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = users.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Неверный email или пароль"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Неверный email или пароль");
        }
        return new AuthResponse(jwtService.generate(user), user.getUsername(), user.getRole().name());
    }
}
