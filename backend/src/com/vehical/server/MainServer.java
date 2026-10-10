package com.vehical.server;

import com.vehical.dao.UserDAO;
import com.vehical.dao.VehicleDAO;
import com.vehical.model.User;
import com.vehical.model.Vehicle;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.sql.SQLException;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainServer {

    private static final int PORT = 8081;
    private static com.sun.net.httpserver.HttpServer httpServer;

    public static void main(String[] args) throws IOException {
        httpServer = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Register API endpoints
        httpServer.createContext("/api/auth/login", new LoginHandler());
        httpServer.createContext("/api/auth/me", new MeHandler());
        httpServer.createContext("/api/vehicle/lookup", new VehicleLookupHandler());
        httpServer.createContext("/api/vehicle/register", new VehicleRegisterHandler());

        // Serve static files from frontend directory
        String frontendPath = "../frontend";
        if (args.length > 0) {
            frontendPath = args[0];
        }
        httpServer.createContext("/", new StaticFileHandler(frontendPath));

        // Enable CORS
        httpServer.setExecutor(null);

        System.out.println("Server started on port " + PORT);
        System.out.println("Frontend: http://localhost:" + PORT);
        System.out.println("API Endpoints:");
        System.out.println("  POST http://localhost:" + PORT + "/api/auth/login");
        System.out.println("  GET  http://localhost:" + PORT + "/api/auth/me");
        System.out.println("  GET  http://localhost:" + PORT + "/api/vehicle/lookup?vehicleNo=MH-04-AB-1234");
        System.out.println("  POST http://localhost:" + PORT + "/api/vehicle/register");

        httpServer.start();
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("POST")) {
                sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                return;
            }

            // Handle CORS
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            
            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseJson(body);
                
                String username = params.get("username");
                String password = params.get("password");
                String role = params.get("role");

                if (username == null || password == null) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Username and password required\"}");
                    return;
                }

                UserDAO userDAO = new UserDAO();
                User user = userDAO.findByUsername(username);

                if (user == null) {
                    sendResponse(exchange, 401, "{\"success\": false, \"message\": \"Invalid credentials\"}");
                    return;
                }

                if (!user.getIsActive()) {
                    sendResponse(exchange, 403, "{\"success\": false, \"message\": \"Account is inactive\"}");
                    return;
                }

                // Simple password check (in production, use bcrypt)
                if (!password.equals("password123") && !password.equals("admin123")) {
                    // For demo, accept 4+ character passwords
                    if (password.length() < 4) {
                        sendResponse(exchange, 401, "{\"success\": false, \"message\": \"Invalid credentials\"}");
                        return;
                    }
                }

                // Check role if provided
                if (role != null && !role.isEmpty() && !role.equals(user.getRole())) {
                    sendResponse(exchange, 403, "{\"success\": false, \"message\": \"Role mismatch\"}");
                    return;
                }

                // Generate simple token
                String token = "token_" + System.currentTimeMillis() + "_" + user.getId();

                String responseData = String.format(
                    "{\"success\": true, \"message\": \"Login successful\", \"data\": {" +
                    "\"token\": \"%s\", \"tokenType\": \"Bearer\", \"expiresIn\": 86400, " +
                    "\"userId\": %d, \"username\": \"%s\", \"fullName\": \"%s\", \"email\": \"%s\", " +
                    "\"phone\": \"%s\", \"role\": \"%s\", \"department\": \"%s\", " +
                    "\"centerName\": \"%s\", \"agencyName\": \"%s\", \"licenseNumber\": \"%s\", " +
                    "\"loginTime\": \"%s\"}}",
                    token, user.getId(), user.getUsername(), user.getFullName(),
                    user.getEmail(), user.getPhone(), user.getRole(),
                    user.getDepartment() != null ? user.getDepartment() : "",
                    user.getCenterName() != null ? user.getCenterName() : "",
                    user.getAgencyName() != null ? user.getAgencyName() : "",
                    user.getLicenseNumber() != null ? user.getLicenseNumber() : "",
                    java.time.Instant.now().toString()
                );

                sendResponse(exchange, 200, responseData);

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error: " + e.getMessage() + "\"}");
            }
        }
    }

    static class MeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("GET")) {
                sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                return;
            }

            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            
            try {
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"User ID required\"}");
                    return;
                }

                Map<String, String> params = parseQuery(query);
                String userIdStr = params.get("userId");
                
                if (userIdStr == null) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"User ID required\"}");
                    return;
                }

                Long userId = Long.parseLong(userIdStr);
                UserDAO userDAO = new UserDAO();
                User user = userDAO.findById(userId);

                if (user == null) {
                    sendResponse(exchange, 404, "{\"success\": false, \"message\": \"User not found\"}");
                    return;
                }

                String responseData = String.format(
                    "{\"success\": true, \"data\": {" +
                    "\"userId\": %d, \"username\": \"%s\", \"fullName\": \"%s\", \"email\": \"%s\", " +
                    "\"phone\": \"%s\", \"role\": \"%s\", \"department\": \"%s\", " +
                    "\"centerName\": \"%s\", \"agencyName\": \"%s\", \"licenseNumber\": \"%s\"}}",
                    user.getId(), user.getUsername(), user.getFullName(),
                    user.getEmail(), user.getPhone(), user.getRole(),
                    user.getDepartment() != null ? user.getDepartment() : "",
                    user.getCenterName() != null ? user.getCenterName() : "",
                    user.getAgencyName() != null ? user.getAgencyName() : "",
                    user.getLicenseNumber() != null ? user.getLicenseNumber() : ""
                );

                sendResponse(exchange, 200, responseData);

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error\"}");
            }
        }
    }

    static class VehicleLookupHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("GET")) {
                sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                return;
            }

            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            try {
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Vehicle number is required\"}");
                    return;
                }

                Map<String, String> params = parseQuery(query);
                String vehicleNo = params.get("vehicleNo");

                if (vehicleNo == null || vehicleNo.trim().isEmpty()) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Vehicle number is required\"}");
                    return;
                }

                // Normalize vehicle number
                String normalizedPlate = vehicleNo
                        .trim()
                        .toUpperCase()
                        .replaceAll("[^A-Z0-9]", "");

                System.out.println("[VehicleLookup] Received: " + vehicleNo + " -> normalized: " + normalizedPlate);

                VehicleDAO vehicleDAO = new VehicleDAO();
                Vehicle vehicle = vehicleDAO.findByVehicleNo(normalizedPlate);

                if (vehicle == null) {
                    System.out.println("[VehicleLookup] Vehicle not found in database for: " + normalizedPlate);
                    sendResponse(exchange, 404, "{\"success\": false, \"message\": \"Vehicle not found in database\"}");
                    return;
                }

                System.out.println("[VehicleLookup] Vehicle found: " + vehicle.getVehicleNo());

                String responseData = String.format(
                    "{\"success\": true, \"message\": \"Vehicle found\", \"vehicle\": {" +
                    "\"vehicleNo\": \"%s\", \"ownerId\": %d, \"vehicleType\": \"%s\", \"make\": \"%s\", \"model\": \"%s\", " +
                    "\"fuelType\": \"%s\", \"registrationDate\": \"%s\", \"registrationState\": \"%s\", " +
                    "\"insuranceStatus\": \"%s\", \"pucStatus\": \"%s\", \"rcStatus\": \"%s\", " +
                    "\"engineNumber\": \"%s\", \"chassisNumber\": \"%s\", \"blacklisted\": %b, " +
                    "\"ownerName\": \"%s\", \"ownerMobile\": \"%s\", \"ownerEmail\": \"%s\"}}",
                    vehicle.getVehicleNo(), vehicle.getOwnerId(), vehicle.getVehicleType(), vehicle.getMake(), vehicle.getModel(),
                    vehicle.getFuelType(), vehicle.getRegistrationDate(), vehicle.getRegistrationState(),
                    vehicle.getInsuranceStatus(), vehicle.getPucStatus(), vehicle.getRcStatus(),
                    vehicle.getEngineNumber(), vehicle.getChassisNumber(), vehicle.getBlacklisted(),
                    vehicle.getOwnerName() != null ? vehicle.getOwnerName() : "",
                    vehicle.getOwnerMobile() != null ? vehicle.getOwnerMobile() : "",
                    vehicle.getOwnerEmail() != null ? vehicle.getOwnerEmail() : ""
                );

                sendResponse(exchange, 200, responseData);

            } catch (SQLException e) {
                System.err.println("Vehicle lookup database error: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}");
            } catch (Exception e) {
                System.err.println("Vehicle lookup error: " + e.getMessage());
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error: " + e.getMessage() + "\"}");
            }
        }
    }

    static class VehicleRegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("POST")) {
                sendResponse(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
                return;
            }

            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            
            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseJson(body);
                
                String vehicleNo = params.get("vehicleNo");
                String ownerName = params.get("ownerName");
                String ownerMobile = params.get("ownerMobile");
                String ownerEmail = params.get("ownerEmail");
                String ownerAddress = params.get("ownerAddress");
                String vehicleType = params.get("vehicleType");
                String make = params.get("make");
                String model = params.get("model");
                String fuelType = params.get("fuelType");
                String registrationDate = params.get("registrationDate");
                String registrationState = params.get("registrationState");
                String engineNumber = params.get("engineNumber");
                String chassisNumber = params.get("chassisNumber");

                if (vehicleNo == null || vehicleNo.isEmpty() || ownerName == null || ownerName.isEmpty()) {
                    sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Vehicle number and owner name are required\"}");
                    return;
                }

                VehicleDAO vehicleDAO = new VehicleDAO();
                
                // Check if vehicle already exists
                Vehicle existingVehicle = vehicleDAO.findByVehicleNo(vehicleNo);
                if (existingVehicle != null) {
                    sendResponse(exchange, 409, "{\"success\": false, \"message\": \"Vehicle already registered\"}");
                    return;
                }

                // Insert owner and get owner_id
                Long ownerId = vehicleDAO.insertOwner(
                    ownerName,
                    ownerMobile != null ? ownerMobile : "",
                    ownerEmail != null ? ownerEmail : "",
                    ownerAddress != null ? ownerAddress : ""
                );

                if (ownerId == null) {
                    sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to create owner\"}");
                    return;
                }

                // Create vehicle object
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleNo(vehicleNo);
                vehicle.setOwnerId(ownerId);
                vehicle.setVehicleType(vehicleType != null ? vehicleType : "Two Wheeler");
                vehicle.setMake(make != null ? make : "");
                vehicle.setModel(model != null ? model : "");
                vehicle.setFuelType(fuelType != null ? fuelType : "Petrol");
                vehicle.setRegistrationDate(registrationDate != null ? registrationDate : java.time.LocalDate.now().toString());
                vehicle.setRegistrationState(registrationState != null ? registrationState : "Maharashtra");
                vehicle.setInsuranceStatus("Valid");
                vehicle.setPucStatus("Valid");
                vehicle.setRcStatus("Active");
                vehicle.setEngineNumber(engineNumber != null ? engineNumber : "");
                vehicle.setChassisNumber(chassisNumber != null ? chassisNumber : "");
                vehicle.setBlacklisted(false);

                // Insert vehicle
                boolean success = vehicleDAO.insertVehicle(vehicle);

                if (success) {
                    String responseData = String.format(
                        "{\"success\": true, \"message\": \"Vehicle registered successfully\", \"data\": {" +
                        "\"vehicleNo\": \"%s\", \"ownerId\": %d, \"ownerName\": \"%s\"}}",
                        vehicleNo, ownerId, ownerName
                    );
                    sendResponse(exchange, 201, responseData);
                } else {
                    sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Failed to register vehicle\"}");
                }

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\": false, \"message\": \"Server error: " + e.getMessage() + "\"}");
            }
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private static Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
            String[] pairs = json.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":");
                if (kv.length == 2) {
                    String key = kv[0].trim().replaceAll("\"", "");
                    String value = kv[1].trim().replaceAll("\"", "");
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }
}
