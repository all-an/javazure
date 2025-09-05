# Azure Deployment Configuration

## Required Environment Variables for App Service

Set these environment variables in your Azure App Service Configuration:

### Database Configuration
```
AZURE_MYSQL_HOST=your-server.mysql.database.azure.com
AZURE_MYSQL_DATABASE=javazure
AZURE_MYSQL_USERNAME=your-admin-username
AZURE_MYSQL_PASSWORD=your-secure-password
```

### Spring Profile
```
SPRING_PROFILES_ACTIVE=azure
```

### Optional
```
PORT=8080
```

## Azure MySQL Flexible Server Setup

1. **Create MySQL Flexible Server:**
   - Choose appropriate tier (Burstable B1ms for development)
   - Enable SSL enforcement
   - Set server admin username and password

2. **Network Configuration:**
   - Allow Azure services access
   - Add your App Service's outbound IP addresses to firewall rules

3. **Database Creation:**
   ```sql
   CREATE DATABASE javazure CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

## App Service Configuration

1. **Runtime Settings:**
   - Java 21
   - Maven deployment
   - Set `SPRING_PROFILES_ACTIVE=azure`

2. **Connection String Format:**
   ```
   jdbc:mysql://SERVER_NAME.mysql.database.azure.com:3306/DATABASE_NAME?useSSL=true&requireSSL=true&serverTimezone=UTC
   ```

3. **Deployment Command:**
   ```bash
   mvn clean package -DskipTests
   ```

## Security Considerations

- Use Azure Key Vault for sensitive configuration
- Enable managed identity for App Service
- Configure proper firewall rules for MySQL
- Use SSL connections (already configured)

## Testing the Deployment

1. Check App Service logs for any startup errors
2. Verify database connection in the logs
3. Test the application endpoints
4. Check that Liquibase migrations run successfully

## Troubleshooting

- **Connection Issues:** Check firewall rules and connection string format
- **SSL Errors:** Verify SSL is properly configured on MySQL server
- **Migration Failures:** Check database permissions and Liquibase changelog
- **Startup Errors:** Review App Service logs and environment variables