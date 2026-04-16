# Demo

Spring Boot 4 REST API using Java 25, Oracle DB, Flyway migrations, and Testcontainers.

## Prerequisites

- Java 25
- Docker Desktop

> **Windows users:** Enable the WSL2 backend in Docker Desktop → Settings → General → "Use the WSL 2 based engine".
> **Apple Silicon (M1–M4):** Supported natively, no extra configuration needed.

## Getting Started

```bash
# 1. Clone the repository
git clone <repo-url>
cd demo

# 2. Start Oracle (creates app_user automatically via APP_USER / APP_USER_PASSWORD env vars)
docker-compose up -d

# 3. Start the app — uses the local profile, which connects to the Docker Oracle instance
./gradlew bootRun
```

The app will be available at `http://localhost:8080`.

## Architecture

| Layer | Class | Responsibility |
|-------|-------|----------------|
| Controller | `OrderController` | HTTP routing, request/response mapping |
| Service | `OrderService` | Business logic, validation, transaction management |
| Repository | `OrderRepository` | Database access via Spring Data JPA |
| Entity | `Order` | JPA-mapped domain object |

## Profiles

| Profile | Purpose |
|---------|---------|
| `local` | Connects to the Docker Oracle instance on `localhost:1521` (default) |

Tests use Testcontainers and need no profile — the datasource is wired automatically.

## Recreating the Oracle volume

If you change credentials in `docker-compose.yml`, recreate the volume so Oracle reinitialises with the new user:

```bash
docker-compose down -v
docker-compose up -d
```

## API Reference

Base path: `/orders`

| Method | Path           | Description        |
|--------|----------------|--------------------|
| POST   | `/orders`      | Create an order    |
| GET    | `/orders`      | List all orders    |
| GET    | `/orders/{id}` | Get an order by ID |
| PUT    | `/orders/{id}` | Update an order    |
| DELETE | `/orders/{id}` | Delete an order    |

Interactive API docs: `http://localhost:8080/swagger-ui.html`

## Running Tests

```bash
./gradlew test
```

Testcontainers automatically spins up an isolated Oracle container for tests — no local database required.
