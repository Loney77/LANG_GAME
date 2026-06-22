package ru.langgame.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** Конфигурация Spring Security: stateless + JWT, ролевой доступ. */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC = {
            "/api/auth/**",
            "/swagger-ui.html", "/swagger-ui/**",
            "/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml"
    };

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC).permitAll()
                        // Управление словарём/темами - только ADMIN.
                        .requestMatchers(HttpMethod.POST, "/api/words/**", "/api/themes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/words/**", "/api/themes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/words/**", "/api/themes/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
