package ru.langgame.config;

import ru.langgame.entity.Role;

/** Аутентифицированный пользователь (principal из JWT). */
public record AuthUser(Long id, String username, Role role) {
}
