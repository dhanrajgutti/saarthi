package com.vehical.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VehicalAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicalAuthApplication.class, args);
        System.out.println("========================================");
        System.out.println("  Vehical Auth Service Started!");
        System.out.println("  Port: 8080");
        System.out.println("  Context: /api");
        System.out.println("  Login Endpoint: POST /api/auth/login");
        System.out.println("========================================");
    }
}
