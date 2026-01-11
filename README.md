[![Java CI with Maven](https://github.com/Yashmerino/ecommerce-platform/actions/workflows/maven.yml/badge.svg)](https://github.com/Yashmerino/ecommerce-platform/actions/workflows/maven.yml) [![Node.js CI](https://github.com/Yashmerino/ecommerce-platform/actions/workflows/node.js.yml/badge.svg)](https://github.com/Yashmerino/ecommerce-platform/actions/workflows/node.js.yml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Yashmerino_ecommerce-platform&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Yashmerino_ecommerce-platform)

<h1 align="center"><strong><em>Ecommerce Platform</strong></em></h1>

<p align="center"><img src="https://static.vecteezy.com/system/resources/previews/009/848/288/original/verified-shop-online-store-3d-illustration-for-ecommerce-icon-free-png.png" alt="sms logo" height=225 width=225></p>

Ecommerce Platform is a pet project made using Spring Boot and React. It uses a MySQL database to store the user, seller and products data. Ecommerce Platform uses JWT for the authorization system and supports 3 languages:
* English
* Romanian
* Russian

## Prerequisites:
* Java 17+
* Node.js 21+
* Maven
* MySQL Database

## To run the Spring server application:

* Execute the `mvn clean install` command in the `ecommerce-platform\ecommerce-platform-server` directory.
* Modify the `ecommerce-platform-server\src\main\resources\application.properties` file.
```properties
server.port=8081

# Database properties
spring.jpa.hibernate.ddl-auto=create
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce-platform
spring.datasource.username=root
spring.datasource.password=1234
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Swagger properties
springdoc.swagger-ui.tagsSorter=alpha

# JWT
jwt.secret=YOUR_JWT_SECRET
```
* Execute the `mvn spring-boot:run` command in the `ecommerce-platform\ecommerce-platform-server` directory.
<br>

# To run the React client application:

* Execute the `npm install` command in the `ecommerce-platform\ecommerce-platform-ui` directory.
* Modify the `ecommerce-platform-ui\src\env-config.ts` file.
```ts
export const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080';
```
* Execute the `npm start` command in the `ecommerce-platform\ecommerce-platform-ui` directory..
<br>

# To run the app using Docker Compose:

* Make sure to have Docker and Docker-Compose installed.
* Create a `init-db.sql` file.
```sql
CREATE DATABASE IF NOT EXISTS ecommerce-platform;
```
* Create a `docker-compose.yml` file.
```yaml
services:
  # Database service
  db:
    image: mysql:latest
    container_name: ecommerce-platform-mysql
    networks:
      - ecommerce-platform-network
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: 1234
    volumes:
      - ./init-db.sql:/docker-entrypoint-initdb.d/init-db.sql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-p1234"]
      interval: 10s
      retries: 5
      start_period: 60s

  # Server service
  server:
    image: yashmerino/ecommerce-platform-server:latest
    container_name: ecommerce-platform-server
    networks:
      - ecommerce-platform-network
    ports:
      - "8081:8081"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/ecommerce-platform
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: 1234
      jwt.secret: YOUR_JWT_SECRET
    depends_on:
      db:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "--fail", "http://localhost:8081/actuator/health"]
      interval: 10s
      retries: 5
      start_period: 60s

  # UI service
  ui:
    image: yashmerino/ecommerce-platform-ui:latest
    container_name: ecommerce-platform-ui
    networks:
      - ecommerce-platform-network
    ports:
      - "8080:8080"
    environment:
      API_BASE_URL: http://ecommerce-platform-server:8081
    depends_on:
      server:
        condition: service_healthy
    healthcheck:
      test: ["CMD-SHELL", "curl --fail http://localhost:8080 || exit 1"]
      interval: 10s
      retries: 5
      start_period: 60s
networks:
  ecommerce-platform-network:
    driver: bridge
```
* Execute the `docker-compose up -d` command.
* Access the app using `http://localhost:8080`.

<br>

# Selenium Integration Tests

* Ecommerce Platform also has a separate module for Selenium integration tests. It resides here: `ecommerce-platform/ecommerce-platform-it`
* Modify the `ecommerce-platform-it\src\test\resources\it-test.properties` file.
```properties
db.url=jdbc:mysql://localhost:3306/ecommerce-platform
db.username=root
db.password=1234
```
* Make sure to turn on the Spring server and React client to run the integration tests.

<b>NOTE</b>: If you run any integration test, it will dump all the existing data from your database.
  
<br>

#### Feel free to create issues and pull requests :)
