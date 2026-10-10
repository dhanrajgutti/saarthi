# Oracle Database Setup Guide

This guide explains how to set up and configure Oracle Database for the Vehicle Authentication System.

## Prerequisites

1. **Oracle Database** installed (Oracle Database 11g or later recommended)
2. **Oracle JDBC Driver** (ojdbc8.jar or later)
3. SQL client tool (SQL Developer, SQL*Plus, or DBeaver)

## Step 1: Create Oracle Database User

Connect to your Oracle database as a privileged user (SYS or SYSTEM) and create a new user:

```sql
-- Connect as SYSDBA
sqlplus / as sysdba

-- Create tablespace (optional, recommended for production)
CREATE TABLESPACE vehical_ts
DATAFILE 'vehical_ts.dbf' SIZE 100M AUTOEXTEND ON NEXT 10M MAXSIZE UNLIMITED;

-- Create user
CREATE USER vehical_user IDENTIFIED BY your_password
DEFAULT TABLESPACE vehical_ts
QUOTA UNLIMITED ON vehical_ts;

-- Grant necessary privileges
GRANT CONNECT, RESOURCE TO vehical_user;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE TRIGGER, CREATE VIEW TO vehical_user;
GRANT UNLIMITED TABLESPACE TO vehical_user;

-- Commit
COMMIT;
```

## Step 2: Run the Schema Script

Connect as the newly created user and execute the schema script:

```bash
# Using SQL*Plus
sqlplus vehical_user/your_password@localhost:1521/ORCL @database/schema_oracle.sql

# Or using SQL Developer
# Open schema_oracle.sql and execute as script
```

## Step 3: Verify Tables

Run this query to verify all tables were created:

```sql
SELECT table_name FROM user_tables ORDER BY table_name;
```

Expected tables:
- USERS
- VIOLATION_MASTER
- OWNER
- VEHICLE
- POLICE_OFFICER
- CHALLAN
- EVIDENCE
- PUC_CERTIFICATE

## Step 4: Configure Application

### For Java/Spring Boot Applications

1. Add Oracle JDBC dependency to `pom.xml`:

```xml
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>21.9.0.0</version>
</dependency>
```

2. Update `application.properties`:

```properties
# Oracle Database Configuration
spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/ORCL
spring.datasource.username=vehical_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.Oracle12cDialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
```

### For Python Applications

1. Install Oracle driver:

```bash
pip install cx_Oracle
```

2. Or use `oracledb` (newer driver):

```bash
pip install oracledb
```

3. Update your database configuration:

```python
import cx_Oracle

# Connection string format: username/password@host:port/service_name
dsn = cx_Oracle.makedsn('localhost', 1521, service_name='ORCL')
connection = cx_Oracle.connect('vehical_user', 'your_password', dsn)
```

### For Node.js Applications

1. Install Oracle driver:

```bash
npm install oracledb
```

2. Update your database configuration:

```javascript
const oracledb = require('oracledb');

const config = {
  user: 'vehical_user',
  password: 'your_password',
  connectString: 'localhost:1521/ORCL'
};

const connection = await oracledb.getConnection(config);
```

## Step 5: Test Connection

Run a simple test query to verify connectivity:

```sql
-- Test query
SELECT COUNT(*) FROM users;
SELECT * FROM users WHERE rownum <= 5;
```

## Common Issues and Solutions

### Issue: ORA-12541: TNS:no listener
**Solution:** Ensure Oracle listener is running:
```bash
lsnrctl status
lsnrctl start
```

### Issue: ORA-01017: invalid username/password
**Solution:** Verify username and password. Check if user is unlocked:
```sql
ALTER USER vehical_user ACCOUNT UNLOCK;
```

### Issue: Cannot find Oracle JDBC driver
**Solution:** Download from Oracle website or use Maven Central:
```xml
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>21.9.0.0</version>
</dependency>
```

### Issue: Connection timeout
**Solution:** Check firewall settings and ensure port 1521 is open.

## Oracle vs MySQL Key Differences

| Feature | MySQL | Oracle |
|---------|-------|--------|
| Auto-increment | AUTO_INCREMENT | SEQUENCE + TRIGGER |
| Boolean | BOOLEAN | NUMBER(1) with CHECK |
| Large text | TEXT | CLOB |
| String type | VARCHAR | VARCHAR2 |
| Integer | INT | NUMBER(10) |
| Big integer | BIGINT | NUMBER(19) |
| Date/Time | DATETIME | TIMESTAMP |
| Enum | ENUM | VARCHAR with CHECK |
| Limit clause | LIMIT | ROWNUM or FETCH FIRST |

## Additional Resources

- [Oracle Database Documentation](https://docs.oracle.com/en/database/)
- [Oracle JDBC Driver Downloads](https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html)
- [Oracle SQL Developer](https://www.oracle.com/tools/sql-developer/)
- [Python cx_Oracle Documentation](https://cx-oracle.readthedocs.io/)
- [Node.js oracledb Documentation](https://node-oracledb.readthedocs.io/)

## Support

For issues specific to this schema conversion, check:
- Ensure all triggers are created successfully
- Verify sequences are starting from the correct values
- Check foreign key constraints are properly defined
