# Development Environment

Local development environment using Docker Compose with hot reload support for all microservices, nginx reverse proxy, and infrastructure services.

## Quick Start

### Start all services with hot reload

```bash
cd development
docker-compose up --build
```

## Stop Services

```bash
# Stop and remove containers
docker-compose down

# Stop and remove containers + volumes (deletes DB data)
docker-compose down -v
```

## Available Services

### Application Services

| Service | Direct Port | Nginx Path | Hot Reload |
|---------|-------------|------------|------------|
| **UI** | 8080 | http://localhost/ | Yes (Vite HMR) |
| **Server** | 8081 | http://localhost/api/ | Yes (DevTools) |
| **Payment Service** | 8082 | http://localhost/payment-api/ | Yes (DevTools) |
| **Notification Service** | 8083 | http://localhost/notification-api/ | Yes (DevTools) |

### Infrastructure Services

| Service | Port | URL | Purpose |
|---------|------|-----|---------|
| **Nginx** | 80 | http://localhost | Reverse proxy |
| **MySQL** | 3306 | jdbc:mysql://localhost:3306 | Database |
| **Kafka** | 9092 | localhost:9092 | Event streaming |
| **Redis** | 6379 | localhost:6379 | Caching & sessions |

### Request Routing

```
http://localhost/                      → UI (Vite dev server)
http://localhost/api/                  → Main Server
http://localhost/api/auth/             → Main Server (rate limited)
http://localhost/payment-api/          → Payment Service
http://localhost/notification-api/     → Notification Service
http://localhost/swagger-ui/           → Main Server Swagger
http://localhost/health                → Nginx health check
```

### Configuration
- Config file: `nginx/nginx.conf`
- Worker connections: 1024
- Client max body size: 1MB
- Request timeout: 60s
- Rate limit: 5 req/min for `/api/auth/` (burst: 10)

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

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Nginx (Port 80)                      │
│          Reverse Proxy & Rate Limiting                  │
└────┬──────────────┬──────────────┬──────────────┬──────┘
     │              │              │              │
     ▼              ▼              ▼              ▼
┌────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│   UI   │   │  Server  │   │ Payment  │   │Notification│
│ :8080  │   │  :8081   │   │  :8082   │   │   :8083   │
└────────┘   └────┬─────┘   └────┬─────┘   └─────┬─────┘
                  │              │               │
           ┌──────┴──────┬───────┴───────┬───────┘
           │             │               │
           ▼             ▼               ▼
      ┌────────┐    ┌────────┐     ┌────────┐
      │ MySQL  │    │ Kafka  │     │ Redis  │
      │ :3306  │    │ :9092  │     │ :6379  │
      └────────┘    └────────┘     └────────┘
```

## Notes

- Changes to `pom.xml` require rebuild: `docker-compose up --build <service>`
- Maven cache is persisted in a volume for faster builds
- Hot reload works for: Java files, application.properties, templates, and static resources