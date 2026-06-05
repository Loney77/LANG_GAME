package ru.skfu.langgame.mediator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.langgame.config.JwtService;
import ru.skfu.langgame.entity.Role;
import ru.skfu.langgame.entity.User;
import ru.skfu.langgame.foundation.IUserRepository;
import ru.skfu.langgame.mediator.dto.AuthResponse;
import ru.skfu.langgame.mediator.dto.LoginRequest;
import ru.skfu.langgame.mediator.dto.RegisterRequest;
import ru.skfu.langgame.mediator.exception.ConflictException;
import ru.skfu.langgame.mediator.exception.UnauthorizedException;

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
