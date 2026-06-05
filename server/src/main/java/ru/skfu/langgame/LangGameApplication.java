package ru.skfu.langgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа серверного приложения.
 *
 * <p>Сервер реализует слои Control/Mediator/Entity/Foundation паттерна PCMEF
 * и предоставляет REST API для мобильного клиента (Presentation).</p>
 */
@SpringBootApplication
public class LangGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(LangGameApplication.class, args);
    }
}
