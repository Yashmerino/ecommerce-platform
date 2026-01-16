[![Java CI with Maven](https://github.com/Yashmerino/ecommerce-platform/actions/workflows/maven.yml/badge.svg)](https://github.com/Yashmerino/ecommerce-platform/actions/workflows/maven.yml) [![Node.js CI](https://github.com/Yashmerino/ecommerce-platform/actions/workflows/node.js.yml/badge.svg)](https://github.com/Yashmerino/ecommerce-platform/actions/workflows/node.js.yml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Yashmerino_ecommerce-platform&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Yashmerino_ecommerce-platform)

<h1 align="center"><strong><em>Ecommerce Platform</strong></em></h1>

<p align="center"><img src="https://static.vecteezy.com/system/resources/previews/009/848/288/original/verified-shop-online-store-3d-illustration-for-ecommerce-icon-free-png.png" alt="sms logo" height=225 width=225></p>

Ecommerce Platform is a pet project built with a microservices architecture using Spring Boot and React. The platform includes multiple services for handling core functionality, payments, and notifications. It uses MySQL for data persistence, Kafka for event streaming, and Redis for caching. The platform features JWT-based authentication and supports 3 languages:
* **English**
* **Romanian**
* **Russian**

## Architecture

The platform consists of the following microservices:
- **Main Server**: Core ecommerce functionality (products, users, orders)
- **Payment Service**: Handles payment processing
- **Notification Service**: Manages notifications and messaging
- **UI**: React-based frontend with Vite and hot module replacement

Infrastructure components:
- **Nginx**: Reverse proxy with rate limiting
- **MySQL**: Primary database
- **Kafka**: Event streaming for inter-service communication
- **Redis**: Caching and session management

## Prerequisites

* Docker
* Docker Compose

## Quick Start - Development Environment

The development environment includes hot reload support for all services:

### Configuration

Before starting, configure your environment variables in `development/docker-compose.yml`:

- **JWT_SECRET**: Your secret key for JWT token generation
- **STRIPE_API_KEY**: Your Stripe API key for payment processing
- **Mail Properties**: SMTP configuration for email notifications
  - `MAIL_HOST`
  - `MAIL_PORT`
  - `MAIL_USERNAME`
  - `MAIL_PASSWORD`

### Start Services

```bash
cd development
docker-compose up --build
```

Access the application at `http://localhost`

### Development Features

- **Hot Reload**: All Java services use Spring Boot DevTools for automatic reloading (~8-10s)
- **Vite HMR**: Frontend updates instantly (~1s)
- **Remote Debugging**: Port 5005 exposed for IDE debugging
- **Volume Mounts**: Source code changes reflected immediately

For more details, see [development/README.md](development/README.md)

### Stopping Services

```bash
# Stop and remove containers
docker-compose down

# Stop and remove containers + volumes (deletes DB data)
docker-compose down -v
```

## Selenium Integration Tests

Ecommerce Platform includes a separate module for Selenium integration tests located in `ecommerce-platform/ecommerce-platform-it`.

To run integration tests:
1. Make sure the development environment is running (`docker-compose up`)
2. Configure `ecommerce-platform-it\src\test\resources\it-test.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/ecommerce-platform
db.username=root
db.password=1234
```

<b>⚠️ WARNING</b>: Integration tests will clear all existing data from your database.

#### Feel free to create issues and pull requests :)
