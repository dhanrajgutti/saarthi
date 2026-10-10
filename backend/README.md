# Simple Java Backend - Vehicle Authentication System

A lightweight Java backend without frameworks using plain JDBC and the built-in HTTP server.

## Features

- **No Frameworks**: Pure Java with standard libraries
- **JDBC**: Direct database connectivity using Oracle JDBC
- **HTTP Server**: Built-in com.sun.net.httpserver with static file serving
- **REST API**: JSON-based API endpoints
- **Oracle Database**: Supports Oracle Database 11g+
- **CORS Enabled**: Cross-origin requests supported for frontend integration

## Project Structure

```
backend/
├── src/
│   └── com/
│       └── vehical/
│           ├── DatabaseConfig.java       # Database configuration
│           ├── model/
│           │   ├── User.java              # User model
│           │   └── Vehicle.java          # Vehicle model
│           ├── dao/
│           │   ├── UserDAO.java          # User data access
│           │   └── VehicleDAO.java       # Vehicle data access
│           └── server/
│               ├── MainServer.java       # HTTP server and API handlers
│               └── StaticFileHandler.java # Static file serving
├── lib/
│   └── ojdbc8.jar                        # Oracle JDBC driver (download required)
├── bin/                                  # Compiled classes (auto-generated)
├── database.properties                   # Database configuration
├── compile.bat                           # Windows compile script
├── run.bat                               # Windows run script
├── compile.sh                            # Linux/Mac compile script
├── run.sh                                # Linux/Mac run script
└── README.md
```

## Prerequisites

1. **Java Development Kit (JDK) 8 or higher**
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Verify installation: `java -version`

2. **Oracle Database**
   - Oracle Database 11g or later
   - Schema must be created using `database/schema_oracle.sql`

3. **Oracle JDBC Driver**
   - Download from: https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html
   - Place `ojdbc8.jar` in the `backend/lib` directory

## Setup Instructions

### 1. Configure Database

Edit `backend/database.properties`:

```properties
oracle.driver=oracle.jdbc.OracleDriver
oracle.url=jdbc:oracle:thin:@//localhost:1521/ORCL
oracle.username=your_oracle_username
oracle.password=your_oracle_password
```

### 2. Download Oracle JDBC Driver

1. Visit: https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html
2. Download one of these JDBC drivers:
   - `ojdbc8.jar` (Oracle Database 12c Release 2)
   - `ojdbc8-12.2.0.1.jar`
   - `ojdbc11.jar` (Oracle Database 21c)
3. Place it in `backend/lib/` directory

The build scripts will automatically detect and use any of these files.

### 3. Create Database Schema

Run the Oracle schema script:

```bash
# Using SQL*Plus
sqlplus your_username/your_password@localhost:1521/ORCL @../database/schema_oracle.sql

# Or using SQL Developer
# Open database/schema_oracle.sql and execute as script
```

### 4. Compile the Backend

**Windows:**
```bash
cd backend
compile.bat
```

**Linux/Mac:**
```bash
cd backend
mkdir -p bin
javac -d bin -cp lib/ojdbc8.jar src/com/vehical/*.java src/com/vehical/model/*.java src/com/vehical/dao/*.java src/com/vehical/server/*.java
```

### 5. Run the Server

**Windows:**
```bash
cd backend
run.bat
```

**Linux/Mac:**
```bash
cd backend
java -cp bin:lib/ojdbc8.jar com.vehical.server.MainServer
```

The server will start on port **8081** and serve the frontend from `http://localhost:8081`.

## API Endpoints

### 1. Login
```
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "police_admin",
  "password": "password123",
  "role": "POLICE"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "token_1234567890_1",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userId": 1,
    "username": "police_admin",
    "fullName": "Rajesh Kumar",
    "email": "rajesh.kumar@police.gov.in",
    "phone": "+91-98765-43210",
    "role": "POLICE",
    "department": "Traffic Police Department - Mumbai",
    "centerName": null,
    "agencyName": null,
    "licenseNumber": null,
    "loginTime": "2026-10-04T10:30:00.000Z"
  }
}
```

### 2. Get User Info
```
GET http://localhost:8081/api/auth/me?userId=1
```

### 3. Vehicle Lookup
```
GET http://localhost:8081/api/vehicle/lookup?vehicleNo=MH-04-AB-1234
```

**Response:**
```json
{
  "success": true,
  "data": {
    "vehicleNo": "MH-04 AB 1234",
    "vehicleType": "Car / Jeep",
    "make": "Maruti Suzuki",
    "model": "Alto 800 LXi",
    "fuelType": "Petrol",
    "registrationDate": "2020-05-15",
    "registrationState": "Maharashtra",
    "insuranceStatus": "Valid",
    "pucStatus": "Expired",
    "rcStatus": "Active",
    "engineNumber": "EN37FE881234",
    "chassisNumber": "CH12345678MSA1",
    "blacklisted": false,
    "ownerName": "Amit Sharma",
    "ownerMobile": "9820011234",
    "ownerEmail": "amit.sharma@email.com"
  }
}
```

## Demo Credentials

Use these credentials from the database schema:

| Username | Password | Role |
|----------|----------|------|
| police_admin | password123 | POLICE |
| police_inspector | password123 | POLICE |
| puc_mumbai_01 | password123 | PUC_CENTER |
| puc_delhi_01 | password123 | PUC_CENTER |
| insurance_lic_01 | password123 | INSURANCE_AGENCY |
| insurance_hdfc_01 | password123 | INSURANCE_AGENCY |

## Frontend Integration

The frontend is already configured to connect to this backend:

- **API Base URL**: `http://localhost:8081/api`
- **Login Endpoint**: `/api/auth/login`
- **User Info**: `/api/auth/me`
- **Vehicle Lookup**: `/api/vehicle/lookup`

Update `frontend/js/login.js` if you change the port:

```javascript
const API_BASE_URL = 'http://localhost:8081/api';
```

## Troubleshooting

### Issue: "Oracle JDBC Driver not found"
**Solution**: Download `ojdbc8.jar` and place it in `backend/lib/` directory.

### Issue: "ORA-12541: TNS:no listener"
**Solution**: Ensure Oracle listener is running:
```bash
lsnrctl status
lsnrctl start
```

### Issue: "Connection refused"
**Solution**: Check Oracle database is running and credentials are correct in `database.properties`.

### Issue: Port 8081 already in use
**Solution**: Change the port in `HttpServer.java`:
```java
private static final int PORT = 8082; // Change to available port
```

### Issue: Compilation errors
**Solution**: Ensure JDK 8+ is installed and in PATH:
```bash
java -version
javac -version
```

## Security Notes

⚠️ **This is a simple demonstration backend. For production use:**

1. **Password Hashing**: Implement proper password hashing (bcrypt, Argon2)
2. **Authentication**: Use JWT or session-based authentication
3. **HTTPS**: Enable SSL/TLS for secure connections
4. **Input Validation**: Add comprehensive input validation
5. **SQL Injection**: Use parameterized queries (already implemented)
6. **CORS**: Configure CORS properly for production domains
7. **Rate Limiting**: Add rate limiting to prevent abuse
8. **Logging**: Implement proper logging and monitoring

## Adding New Endpoints

To add a new API endpoint:

1. Create a handler class implementing `HttpHandler`:
```java
static class NewEndpointHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Your logic here
    }
}
```

2. Register the endpoint in `main()`:
```java
server.createContext("/api/new-endpoint", new NewEndpointHandler());
```

3. Add corresponding DAO methods if database access is needed

## License

This is a demonstration project for educational purposes.
