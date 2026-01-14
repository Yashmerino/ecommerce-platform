# Development Environment

Local development environment using Docker Compose with hot reload support for all microservices.

## Quick Start

### Start all services with hot reload

```bash
cd development
docker-compose up --build
```

### Start specific services only

```bash
# Infrastructure only
docker-compose up kafka mysql

# Single service with dependencies
docker-compose up kafka mysql ecommerce-server
```

## Stop Services

```bash
# Stop and remove containers
docker-compose down

# Stop and remove containers + volumes (deletes DB data)
docker-compose down -v
```

## Available Services

| Service | Port | URL | Hot Reload |
|---------|------|-----|------------|
| UI | 8080 | http://localhost:8080 | Yes (Vite HMR) |
| Server | 8081 | http://localhost:8081 | Yes (DevTools) |
| Payment Service | 8082 | http://localhost:8082 | Yes (DevTools) |
| Notification Service | 8083 | http://localhost:8083 | Yes (DevTools) |
| MySQL | 3306 | jdbc:mysql://localhost:3306 | - |
| Kafka | 9092 | localhost:9092 | - |

## How Hot Reload Works

**Java Services:**
- Modify Java file in your IDE
- Maven detects changes via volume mount
- Spring Boot DevTools reloads application automatically
- Changes applied in ~8-10 seconds

**UI (React):**
- Modify .tsx/.ts/.css file
- Vite HMR detects changes instantly
- Browser updates without full refresh
- Changes applied in ~1 second

## Debugging

Remote debug ports are exposed for all Java services on port 5005. Configure your IDE to attach to `localhost:5005`.

## Notes

- Changes to `pom.xml` require rebuild: `docker-compose up --build <service>`
- Maven cache is persisted in a volume for faster builds
- Hot reload works for: Java files, application.properties, templates, and static resources