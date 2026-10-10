#!/bin/bash
echo "Compiling Java Backend..."

# Create bin directory if not exists
mkdir -p bin

# Check for Oracle JDBC driver
JDBC_JAR=""
if [ -f lib/ojdbc8.jar ]; then
    JDBC_JAR="lib/ojdbc8.jar"
elif [ -f lib/ojdbc8-12.2.0.1.jar ]; then
    JDBC_JAR="lib/ojdbc8-12.2.0.1.jar"
elif [ -f lib/ojdbc11.jar ]; then
    JDBC_JAR="lib/ojdbc11.jar"
fi

if [ -z "$JDBC_JAR" ]; then
    echo "WARNING: Oracle JDBC driver not found in lib directory!"
    echo "Compiling without JDBC support (database operations will fail at runtime)"
    echo "To enable database support, place one of these files in backend/lib directory:"
    echo "  - ojdbc8.jar"
    echo "  - ojdbc8-12.2.0.1.jar"
    echo "  - ojdbc11.jar"
    echo "Download from: https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html"
    echo ""
    # Compile without JDBC
    javac -d bin src/com/vehical/*.java src/com/vehical/model/*.java src/com/vehical/dao/*.java src/com/vehical/server/MainServer.java src/com/vehical/server/StaticFileHandler.java
else
    echo "Using JDBC driver: $JDBC_JAR"
    # Compile with JDBC
    javac -d bin -cp $JDBC_JAR src/com/vehical/*.java src/com/vehical/model/*.java src/com/vehical/dao/*.java src/com/vehical/server/MainServer.java src/com/vehical/server/StaticFileHandler.java
fi

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Run with: ./run.sh"
else
    echo "Compilation failed!"
    exit 1
fi
