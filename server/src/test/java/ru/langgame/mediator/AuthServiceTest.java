package ru.langgame.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.langgame.config.JwtService;
import ru.langgame.entity.Role;
import ru.langgame.entity.User;
import ru.langgame.foundation.IUserRepository;
import ru.langgame.mediator.dto.AuthResponse;
import ru.langgame.mediator.dto.LoginRequest;
import ru.langgame.mediator.dto.RegisterRequest;
import ru.langgame.mediator.exception.ConflictException;
import ru.langgame.mediator.exception.UnauthorizedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private IUserRepository users;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(users, passwordEncoder, jwtService);
    }

    private static User user(String username, String hash) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(username + "@mail.ru");
        u.setPasswordHash(hash);
        u.setRole(Role.USER);
        return u;
    }

    @Test
    void registerCreatesUserAndReturnsToken() {
        when(users.existsByEmail("a@mail.ru")).thenReturn(false);
        when(users.existsByUsername("aslan")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("hash");
        when(users.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generate(any(User.class))).thenReturn("jwt-token");

        AuthResponse res = service.register(new RegisterRequest("aslan", "a@mail.ru", "pass"));

        assertThat(res.token()).isEqualTo("jwt-token");
        assertThat(res.username()).isEqualTo("aslan");
        assertThat(res.role()).isEqualTo("USER");
    }

    @Test
    void registerRejectsTakenEmail() {
        when(users.existsByEmail("a@mail.ru")).thenReturn(true);
        assertThatThrownBy(() -> service.register(new RegisterRequest("aslan", "a@mail.ru", "pass")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void registerRejectsTakenUsername() {
        when(users.existsByEmail("a@mail.ru")).thenReturn(false);
        when(users.existsByUsername("aslan")).thenReturn(true);
        assertThatThrownBy(() -> service.register(new RegisterRequest("aslan", "a@mail.ru", "pass")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void loginSucceedsWithCorrectPassword() {
        when(users.findByEmail("a@mail.ru")).thenReturn(Optional.of(user("aslan", "hash")));
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);
        when(jwtService.generate(any(User.class))).thenReturn("jwt-token");

        AuthResponse res = service.login(new LoginRequest("a@mail.ru", "pass"));

        assertThat(res.token()).isEqualTo("jwt-token");
        assertThat(res.username()).isEqualTo("aslan");
    }

    @Test
    void loginRejectsWrongPassword() {
        when(users.findByEmail("a@mail.ru")).thenReturn(Optional.of(user("aslan", "hash")));
        when(passwordEncoder.matches("bad", "hash")).thenReturn(false);
        assertThatThrownBy(() -> service.login(new LoginRequest("a@mail.ru", "bad")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void loginRejectsUnknownEmail() {
        when(users.findByEmail("x@mail.ru")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.login(new LoginRequest("x@mail.ru", "pass")))
                .isInstanceOf(UnauthorizedException.class);
    }
}
