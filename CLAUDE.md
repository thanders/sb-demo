# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Start Oracle (required before running the app)
docker-compose up -d

# Build
./gradlew build

# Run the application — uses the local profile (connects to Docker Oracle on localhost:1521)
./gradlew bootRun

# Run all tests (Testcontainers starts an isolated Oracle container automatically, no profile needed)
./gradlew test

# Run a single test class
./gradlew test --tests "com.example.demo.DemoApplicationTests"

# Run a single test method
./gradlew test --tests "com.example.demo.DemoApplicationTests.contextLoads"

# Recreate the Oracle volume (required if docker-compose.yml credentials change)
docker-compose down -v && docker-compose up -d
```

## Architecture

Spring Boot 4.0 REST API using Java 25, backed by Oracle DB (via Docker). Flyway owns the schema; Hibernate is set to `ddl-auto=validate`.

**Package structure:** `com.example.demo`
- `entity/` — JPA entities (Lombok `@Data`, `@Builder`)
- `order/controller/` — REST controllers; delegate entirely to the service layer, no repository access or exception handling here
- `order/service/` — business logic and validation; `@Transactional` annotations live here (readOnly on reads)
- `order/repository/` — Spring Data JPA repositories (extend `JpaRepository`)
- `order/exception/` — `@ResponseStatus`-annotated exceptions (e.g., `ResourceNotFoundException` → 404)
- `config/` — Spring Security config (all requests permitted, CSRF disabled)

**Current domain:** A single `Order` entity (`id`, `productName`, `price` as `BigDecimal`) with full CRUD via `OrderController` at `/orders`. The primary key uses `ORDER_SEQ` via `GenerationType.SEQUENCE`.

**Service layer:** `OrderService` owns all business logic. It validates that `productName` is non-null/non-blank and `price` is non-null, positive; throws `IllegalArgumentException` for violations and `ResourceNotFoundException` for missing entities.

**DTO pattern:** `OrderRequestDto` (record) is used for inbound create/update requests — it has no `id` field since the database generates that via `ORDER_SEQ`. `OrderResponseDto` (record) is used for all outbound responses. Mapping between DTOs and the `Order` entity is handled exclusively in the service layer; the controller never touches the entity.

**Profiles:**
- `local` — active by default (`application.properties`); connects to the Docker Oracle instance on `localhost:1521` as `app_user`
- Tests — no profile needed; Testcontainers overrides the datasource via `@ServiceConnection`

**Database:** Oracle Free (`gvenzl/oracle-free`) running in Docker, connecting to `FREEPDB1` on port `1521`. `APP_USER` / `APP_USER_PASSWORD` env vars in `docker-compose.yml` tell the image to create the application user natively — no init scripts needed. If these credentials are changed, the Docker volume must be recreated (`docker-compose down -v`) so Oracle reinitialises with the new user.

**Flyway migrations:** Located in `src/main/resources/db/migration/`. Add new scripts as `V2__...sql`, `V3__...sql`, etc. Spring Boot 4.0 split auto-configurations into component JARs — Flyway requires `spring-boot-flyway` explicitly in `build.gradle` (not just `flyway-core`) to activate `FlywayAutoConfiguration`.

**Testcontainers 2.x:** Artifact IDs use a `testcontainers-` prefix (e.g. `testcontainers-oracle-free`, `testcontainers-junit-jupiter`) unlike 1.x. Minimum Docker API version also raised — resolves 400 errors from Docker Engine 29.x. `OracleContainer` is configured with `.withUsername` / `.withPassword` so `@ServiceConnection` wires the datasource and Flyway automatically.

**Tests:**
- `OrderServiceTest` — plain unit test using `@ExtendWith(MockitoExtension.class)`; no Spring context, no Testcontainers. `OrderRepository` is mocked with `@Mock`.
- `OrderRepositoryTest` — integration test; `TestcontainersConfiguration` starts a fresh `OracleContainer` and uses `@ServiceConnection` to auto-configure the datasource. Uses `@Transactional` for per-test rollback isolation.
- `DemoApplicationTests` — smoke test that loads the full Spring context and asserts Flyway migrations were applied.

**API docs:** SpringDoc OpenAPI UI available at `/swagger-ui.html` when running.
