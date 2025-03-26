# Система управления задачами

REST API для управления задачами (Task Management System)

### Технологии:
- Java 21
- Spring (Boot, Security, DATA JPA)
- JWT
- PostgreSQL
- Docker
- Maven
- Swagger (OpenApi)
- Flyway db

### Запуск дев среды
```bash
   docker-compose up
```
### Тестирование

API доступен по адресу: http://localhost:8080

Документация Swagger UI: http://localhost:8080/swagger-ui/index.html

- **Регистрация пользователя:** POST /api/auth/registration
- **Логин и получение токена:** POST /api/auth/login
- **Использовать в header запросов:** Bearer 'полученный токен'
- **Остальные эндпоинты можно посмотреть в документации сваггера**

Роль для админа выставляется в бд вручную ROLE_ADMIN
