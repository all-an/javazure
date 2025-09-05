# Local MySQL Development Setup with Docker

This guide helps you set up a local MySQL container for development before connecting to Azure MySQL Flexible Server.

## Prerequisites

- Docker installed on your machine
- Docker Compose (usually comes with Docker Desktop)

## Quick Start

### Option 1: Using Docker Run Command

```bash
docker run -d \
  --name mysql-local \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=javazure \
  -e MYSQL_USER=appuser \
  -e MYSQL_PASSWORD=apppassword \
  -p 3306:3306 \
  mysql:8.0
```

### Option 2: Using Docker Compose (Recommended)

Create a `docker-compose.yml` file in your project root:

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: mysql-local
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: javazure
      MYSQL_USER: appuser
      MYSQL_PASSWORD: apppassword
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init:/docker-entrypoint-initdb.d
    command: --default-authentication-plugin=mysql_native_password

volumes:
  mysql_data:
```

Then run:
```bash
docker-compose up -d
```

## Connection Details

- **Host:** localhost
- **Port:** 3306
- **Database:** javazure
- **Username:** appuser
- **Password:** apppassword
- **Root Username:** root
- **Root Password:** rootpassword

## Application Configuration

Update your `application.properties` or `application.yml`:

```properties
# For local development
spring.datasource.url=jdbc:mysql://localhost:3306/javazure
spring.datasource.username=appuser
spring.datasource.password=apppassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

## Useful Commands

### Start the container
```bash
docker start mysql-local
# or with compose
docker-compose up -d
```

### Stop the container
```bash
docker stop mysql-local
# or with compose
docker-compose down
```

### Connect to MySQL CLI
```bash
docker exec -it mysql-local mysql -u appuser -p
```

### View container logs
```bash
docker logs mysql-local
```

### Reset database (removes all data)
```bash
docker-compose down -v
docker-compose up -d
```

## Migration to Azure MySQL Flexible Server

When ready to migrate to Azure:

1. Update connection string to Azure MySQL endpoint
2. Use Azure credentials instead of local ones
3. Consider using Azure Key Vault for secrets
4. Update firewall rules to allow your application's IP

Example Azure configuration:
```properties
spring.datasource.url=jdbc:mysql://your-server.mysql.database.azure.com:3306/javazure?useSSL=true&requireSSL=true
spring.datasource.username=your-azure-user@your-server
spring.datasource.password=${AZURE_MYSQL_PASSWORD}
```