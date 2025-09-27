# Sadhana Tracker MySQL Database Scripts

## Start MySQL Container
```powershell
docker-compose up -d mysql
```

## Stop MySQL Container
```powershell
docker-compose down
```

## View MySQL Logs
```powershell
docker logs sadhana-mysql
```

## Connect to MySQL Database
```powershell
docker exec -it sadhana-mysql mysql -u root -ppassword
```

## Check Running Containers
```powershell
docker ps
```

## Backup Database
```powershell
docker exec sadhana-mysql mysqldump -u root -ppassword sadhana_tracker_db > backup.sql
```

## Restore Database
```powershell
docker exec -i sadhana-mysql mysql -u root -ppassword sadhana_tracker_db < backup.sql
```

## Quick Database Status Check
```powershell
docker exec sadhana-mysql mysql -u root -ppassword -e "SELECT VERSION(); SHOW DATABASES;"
```

## View All Tables
```powershell
docker exec sadhana-mysql mysql -u root -ppassword -e "USE sadhana_tracker_db; SHOW TABLES;"
```

## Development Workflow

1. **Start Development Session**:
   ```powershell
   # Start MySQL container
   docker-compose up -d mysql
   
   # Start Spring Boot application
   .\mvnw.cmd spring-boot:run
   ```

2. **Stop Development Session**:
   ```powershell
   # Stop Spring Boot (Ctrl+C in terminal)
   
   # Optionally stop MySQL (data persists)
   docker-compose down
   ```

3. **Clean Restart** (if needed):
   ```powershell
   # Stop everything
   docker-compose down
   
   # Remove old containers but keep data
   docker-compose rm -f
   
   # Start fresh
   docker-compose up -d mysql
   ```

## Configuration Details

- **Database**: `sadhana_tracker_db`
- **Root Password**: `password`
- **MySQL Port**: `3306`
- **Container Name**: `sadhana-mysql`
- **Data Volume**: `sadhana-tracker_mysql_data`

## Benefits of This Setup

✅ **Local Development**: Spring Boot runs locally for fast development cycles
✅ **Docker Database**: MySQL runs in Docker for consistent database environment  
✅ **Data Persistence**: Database data survives container restarts
✅ **Easy Cleanup**: Can easily reset database without affecting code
✅ **Production-like**: Matches production deployment patterns
