#!/bin/bash
echo "Starting Backend Server..."

# Find Oracle JDBC driver
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
    echo "Server will start but database operations will fail."
    CLASSPATH="bin"
else
    echo "Using JDBC driver: $JDBC_JAR"
    CLASSPATH="bin:$JDBC_JAR"
fi

# Run the server
java -cp $CLASSPATH com.vehical.server.MainServer
