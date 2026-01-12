# Dev Environment - Kafka + MySQL

Local development environment using Docker Compose with Kafka and MySQL.

---

## Commands

Start containers:

```bash
docker-compose up -d
```

Stop containers:

```bash
docker-compose down
```

Check running containers:

```bash
docker ps
```

Connect to MySQL:

```bash
docker exec -it mysql mysql -uroot -123
```