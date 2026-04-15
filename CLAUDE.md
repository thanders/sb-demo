# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run the application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.example.demo.DemoApplicationTests"

# Run a single test method
./gradlew test --tests "com.example.demo.DemoApplicationTests.contextLoads"
```

## Architecture

Spring Boot 4.0 REST API using Java 25, backed by an in-memory H2 database (persisted to `data/demo-db.mv.db`).

**Package structure:** `com.example.demo`
- `entity/` — JPA entities (Lombok `@Data`, `@Builder`)
- `order/controller/` — REST controllers
- `order/repository/` — Spring Data JPA repositories (extend `JpaRepository`)
- `order/exception/` — `@ResponseStatus`-annotated exceptions (e.g., `ResourceNotFoundException` → 404)
- `config/` — Spring Security config (currently all requests permitted, CSRF disabled for H2 console access)

**Current domain:** A single `Order` entity (`id`, `productName`, `price`) with full CRUD via `OrderController` at `/orders`.

**Security:** `SecurityConfig` disables CSRF and permits all requests — intentional for local development/demo use with the H2 console at `/h2-console`.

**API docs:** SpringDoc OpenAPI UI available at `/swagger-ui.html` when running.

**Database config:** `application.properties` uses `jdbc:h2:mem:testdb` (in-memory). The `data/` directory holds a file-based H2 database that is not currently wired to the app config.
