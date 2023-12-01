# Description

A microservice that, via REST API, allows you to register, log in, log out, and download a list
your items and adding your own item.

# Getting Started

## Recommended requirements

* Database MongoDB 7.0.4
* Java 17
* Maven 3.8.3

## Usage

Update application.properties file.

Edit properties.
```
db.host={DATABASE_HOST}
db.port={DATABASE_PORT}
db.name={DATABASE_NAME}

server.port={SERVER_PORT}

jwt.secret={JWT_SECRET}
jwt.algorithm={JWT_ALGORITHM}
jwt.expires.in.seconds={TOKEN_EXPIRITION_TIME}
```

### Build project
```bash
mvn package
```
or

### Run project
```bash
mvn exec:java
```

### Run project with Docker

Comprehensive project launch with one command.

```bash
docker compose up
```

You can edit docker-compose.yaml configuration.