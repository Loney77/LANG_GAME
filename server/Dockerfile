# Сборка исполняемого jar
FROM eclipse-temurin:17-jdk AS build
WORKDIR /build
COPY . .
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew bootJar --no-daemon -x test

# Запуск
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/build/libs/*.jar app.jar
EXPOSE 8137
ENTRYPOINT ["java", "-jar", "app.jar"]
