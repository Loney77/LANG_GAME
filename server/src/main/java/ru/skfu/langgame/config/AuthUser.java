package ru.skfu.langgame.config;

import ru.skfu.langgame.entity.Role;

/** Аутентифицированный пользователь (principal из JWT). */
public record AuthUser(Long id, String username, Role role) {
}
