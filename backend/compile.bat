@echo off
echo Compiling Java Backend...

REM Create bin directory if not exists
if not exist bin mkdir bin

REM Check for Oracle JDBC driver
set JDBC_JAR=
if exist lib\ojdbc8.jar set JDBC_JAR=lib\ojdbc8.jar
if exist lib\ojdbc8-12.2.0.1.jar set JDBC_JAR=lib\ojdbc8-12.2.0.1.jar
if exist lib\ojdbc11.jar set JDBC_JAR=lib\ojdbc11.jar

if "%JDBC_JAR%"=="" (
    echo WARNING: Oracle JDBC driver not found in lib directory!
    echo Compiling without JDBC support (database operations will fail at runtime)
    echo To enable database support, place one of these files in backend\lib directory:
    echo   - ojdbc8.jar
    echo   - ojdbc8-12.2.0.1.jar
    echo   - ojdbc11.jar
    echo Download from: https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html
    echo.
    set CLASSPATH=bin
) else (
    echo Using JDBC driver: %JDBC_JAR%
    set CLASSPATH=bin;%JDBC_JAR%
)

REM Compile all Java files
if "%JDBC_JAR%"=="" (
    javac -d bin src\com\vehical\*.java src\com\vehical\model\*.java src\com\vehical\dao\*.java src\com\vehical\server\MainServer.java src\com\vehical\server\StaticFileHandler.java
) else (
    javac -d bin -cp %JDBC_JAR% src\com\vehical\*.java src\com\vehical\model\*.java src\com\vehical\dao\*.java src\com\vehical\server\MainServer.java src\com\vehical\server\StaticFileHandler.java
)

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Run with: run.bat
) else (
    echo Compilation failed!
    pause
)
