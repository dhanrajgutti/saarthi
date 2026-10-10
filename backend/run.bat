@echo off
echo Starting Backend Server...

REM Find Oracle JDBC driver
set JDBC_JAR=
if exist lib\ojdbc8.jar set JDBC_JAR=lib\ojdbc8.jar
if exist lib\ojdbc8-12.2.0.1.jar set JDBC_JAR=lib\ojdbc8-12.2.0.1.jar
if exist lib\ojdbc11.jar set JDBC_JAR=lib\ojdbc11.jar

if "%JDBC_JAR%"=="" (
    echo Oracle JDBC driver not found in lib directory!
    pause
    exit /b 1
)

echo Using JDBC driver: %JDBC_JAR%

REM Set classpath
set CLASSPATH=bin;%JDBC_JAR%

REM Run the server
java -cp %CLASSPATH% com.vehical.server.MainServer

pause
