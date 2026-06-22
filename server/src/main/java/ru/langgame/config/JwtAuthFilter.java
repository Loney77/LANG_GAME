package ru.langgame.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.langgame.entity.Role;

import java.io.IOException;
import java.util.List;

/** Фильтр аутентификации по JWT: извлекает токен и наполняет SecurityContext. */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIX = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(PREFIX)) {
            try {
                Claims claims = jwtService.parse(header.substring(PREFIX.length()));
                Long uid = ((Number) claims.get("uid")).longValue();
                Role role = Role.valueOf(claims.get("role", String.class));
                String username = claims.get("username", String.class);

                AuthUser principal = new AuthUser(uid, username, role);
                var authentication = new UsernamePasswordAuthenticationToken(
                        principal, null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ex) {
                // Невалидный токен - оставляем запрос неаутентифицированным.
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
