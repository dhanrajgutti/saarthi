# Quick Start Guide - Vehicle Authentication System

This guide will help you quickly set up and run the Vehicle Authentication System with Oracle Database.

## Prerequisites Checklist

- [ ] Java JDK 8+ installed
- [ ] Oracle Database 11g+ installed and running
- [ ] Oracle JDBC driver (ojdbc8.jar) downloaded

## Step 1: Setup Oracle Database

### 1.1 Create Database User

Connect to Oracle as SYSDBA and run:

```sql
-- Create user
CREATE USER vehical_user IDENTIFIED BY your_password
DEFAULT TABLESPACE USERS
QUOTA UNLIMITED ON USERS;

-- Grant privileges
GRANT CONNECT, RESOURCE TO vehical_user;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE TRIGGER TO vehical_user;
GRANT UNLIMITED TABLESPACE TO vehical_user;

COMMIT;
```

### 1.2 Run Schema Script

```bash
# Using SQL*Plus
sqlplus vehical_user/your_password@localhost:1521/ORCL @database/schema_oracle.sql
```

## Step 2: Setup Backend

### 2.1 Download Oracle JDBC Driver

1. Download one of these from: https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html
   - `ojdbc8.jar`
   - `ojdbc8-12.2.0.1.jar`
   - `ojdbc11.jar`
2. Place it in `backend/lib/` directory

### 2.2 Configure Database Connection

Edit `backend/database.properties`:

```properties
oracle.driver=oracle.jdbc.OracleDriver
oracle.url=jdbc:oracle:thin:@//localhost:1521/ORCL
oracle.username=vehical_user
oracle.password=your_password
```

### 2.3 Compile Backend

**Windows:**
```bash
cd backend
compile.bat
```

**Linux/Mac:**
```bash
cd backend
chmod +x compile.sh run.sh
./compile.sh
```

### 2.4 Run Backend Server

**Windows:**
```bash
cd backend
run.bat
```

**Linux/Mac:**
```bash
cd backend
./run.sh
```

The server will start on port 8081 and serve the frontend at:
- **Frontend**: http://localhost:8081
- **API**: http://localhost:8081/api

## Step 3: Test the Application

### 3.1 Open Frontend

Open your browser and navigate to:
```
http://localhost:8081
```

### 3.2 Login with Demo Credentials

Use any of these demo accounts:

| Username | Password | Role |
|----------|----------|------|
| police_admin | password123 | Police |
| puc_mumbai_01 | password123 | PUC Center |
| insurance_lic_01 | password123 | Insurance Agency |

### 3.3 Test API Endpoints

**Login:**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"police_admin","password":"password123","role":"POLICE"}'
```

**Vehicle Lookup:**
```bash
curl "http://localhost:8081/api/vehicle/lookup?vehicleNo=MH-04-AB-1234"
```

## Troubleshooting

### Oracle JDBC Driver Not Found
```
Error: Oracle JDBC Driver not found
```
**Solution**: Download `ojdbc8.jar` and place it in `backend/lib/`

### Database Connection Failed
```
Error: ORA-12541: TNS:no listener
```
**Solution**: Start Oracle listener
```bash
lsnrctl start
```

### Port Already in Use
```
Error: Address already in use
```
**Solution**: Change port in `backend/src/com/vehical/server/MainServer.java`
```java
private static final int PORT = 8082; // Change to available port
```

### Compilation Errors
```
Error: package com.sun.net.httpserver does not exist
```
**Solution**: Ensure you're using JDK 8 or higher (includes HTTP server)

## Project Structure

```
vehical/
├── database/
│   ├── schema.sql           # MySQL schema (original)
│   ├── schema_oracle.sql    # Oracle schema (converted)
│   └── ORACLE_SETUP_GUIDE.md
├── backend/
│   ├── src/
│   │   └── com/vehical/
│   │       ├── DatabaseConfig.java
│   │       ├── model/
│   │       ├── dao/
│   │       └── server/
│   │           ├── MainServer.java
│   │           └── StaticFileHandler.java
│   ├── lib/
│   │   └── ojdbc8.jar       # Download this
│   ├── database.properties  # Database config
│   ├── compile.bat          # Windows compile
│   ├── run.bat              # Windows run
│   ├── compile.sh           # Linux/Mac compile
│   ├── run.sh               # Linux/Mac run
│   └── README.md
└── frontend/
    ├── index.html
    ├── login.html
    ├── css/
    ├── js/
    └── pages/
```

## Next Steps

1. **Customize Configuration**: Update `backend/database.properties` with your Oracle credentials
2. **Add More Endpoints**: See `backend/README.md` for adding new API endpoints
3. **Deploy**: For production, consider using a proper application server (Tomcat, Jetty)
4. **Security**: Implement proper authentication (JWT), HTTPS, and input validation

## Support

For detailed documentation:
- Backend: `backend/README.md`
- Oracle Setup: `database/ORACLE_SETUP_GUIDE.md`

## License

Educational demonstration project.
