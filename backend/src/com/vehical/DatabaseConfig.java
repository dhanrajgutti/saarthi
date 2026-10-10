package com.vehical;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final String CONFIG_FILE = "database.properties";
    private static String ORACLE_DRIVER;
    private static String ORACLE_URL;
    private static String USERNAME;
    private static String PASSWORD;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            ORACLE_DRIVER = props.getProperty("oracle.driver", "oracle.jdbc.OracleDriver");
            ORACLE_URL = props.getProperty("oracle.url", "jdbc:oracle:thin:@//localhost:1521/ORCL");
            USERNAME = props.getProperty("oracle.username", "vehical_user");
            PASSWORD = props.getProperty("oracle.password", "your_password");
        } catch (IOException e) {
            // Use defaults if config file not found
            System.out.println("Config file not found, using defaults");
            ORACLE_DRIVER = "oracle.jdbc.OracleDriver";
            ORACLE_URL = "jdbc:oracle:thin:@//localhost:1521/ORCL";
            USERNAME = "vehical_user";
            PASSWORD = "your_password";
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(ORACLE_DRIVER);
            return DriverManager.getConnection(ORACLE_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found", e);
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
