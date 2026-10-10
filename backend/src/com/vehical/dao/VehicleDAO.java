package com.vehical.dao;

import com.vehical.DatabaseConfig;
import com.vehical.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VehicleDAO {

    public Vehicle findByVehicleNo(String vehicleNo) throws SQLException {
        // Normalize vehicle number: uppercase, remove spaces and hyphens
        String normalizedVehicleNo = normalizeVehicleNumber(vehicleNo);
        
        System.out.println("[VehicleDAO] Looking up vehicle: " + vehicleNo + " -> normalized: " + normalizedVehicleNo);
        
        // Use Oracle SQL functions to normalize both sides for comparison
        String sql = "SELECT v.vehicle_no, v.owner_id, v.vehicle_type, v.make, v.model, " +
                    "v.fuel_type, v.registration_date, v.registration_state, v.insurance_status, " +
                    "v.puc_status, v.rc_status, v.engine_number, v.chassis_number, v.blacklisted, " +
                    "o.owner_name, o.mobile, o.email " +
                    "FROM vehicle v " +
                    "LEFT JOIN owner o ON v.owner_id = o.owner_id " +
                    "WHERE REPLACE(REPLACE(UPPER(v.vehicle_no), '-', ''), ' ', '') = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, normalizedVehicleNo);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleNo(rs.getString("vehicle_no"));
                vehicle.setOwnerId(rs.getLong("owner_id"));
                vehicle.setVehicleType(rs.getString("vehicle_type"));
                vehicle.setMake(rs.getString("make"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setFuelType(rs.getString("fuel_type"));
                vehicle.setRegistrationDate(rs.getString("registration_date"));
                vehicle.setRegistrationState(rs.getString("registration_state"));
                vehicle.setInsuranceStatus(rs.getString("insurance_status"));
                vehicle.setPucStatus(rs.getString("puc_status"));
                vehicle.setRcStatus(rs.getString("rc_status"));
                vehicle.setEngineNumber(rs.getString("engine_number"));
                vehicle.setChassisNumber(rs.getString("chassis_number"));
                vehicle.setBlacklisted(rs.getInt("blacklisted") == 1);
                vehicle.setOwnerName(rs.getString("owner_name"));
                vehicle.setOwnerMobile(rs.getString("mobile"));
                vehicle.setOwnerEmail(rs.getString("email"));
                System.out.println("[VehicleDAO] Vehicle found: " + vehicle.getVehicleNo());
                return vehicle;
            }
            System.out.println("[VehicleDAO] Vehicle not found for: " + normalizedVehicleNo);
            return null;
        } catch (SQLException e) {
            System.err.println("Vehicle lookup error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
    
    /**
     * Normalize vehicle number by converting to uppercase and removing spaces and hyphens
     */
    private String normalizeVehicleNumber(String vehicleNo) {
        if (vehicleNo == null || vehicleNo.trim().isEmpty()) {
            return "";
        }
        return vehicleNo.toUpperCase().replaceAll("[-\\s]", "");
    }

    public boolean insertVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicle (vehicle_no, owner_id, vehicle_type, make, model, " +
                    "fuel_type, registration_date, registration_state, insurance_status, " +
                    "puc_status, rc_status, engine_number, chassis_number, blacklisted) " +
                    "VALUES (?, ?, ?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, vehicle.getVehicleNo());
            stmt.setLong(2, vehicle.getOwnerId());
            stmt.setString(3, vehicle.getVehicleType());
            stmt.setString(4, vehicle.getMake());
            stmt.setString(5, vehicle.getModel());
            stmt.setString(6, vehicle.getFuelType());
            stmt.setString(7, vehicle.getRegistrationDate());
            stmt.setString(8, vehicle.getRegistrationState());
            stmt.setString(9, vehicle.getInsuranceStatus());
            stmt.setString(10, vehicle.getPucStatus());
            stmt.setString(11, vehicle.getRcStatus());
            stmt.setString(12, vehicle.getEngineNumber());
            stmt.setString(13, vehicle.getChassisNumber());
            stmt.setInt(14, vehicle.getBlacklisted() ? 1 : 0);
            
            int rows = stmt.executeUpdate();
            return rows > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public Long insertOwner(String ownerName, String mobile, String email, String address) throws SQLException {
        String sql = "INSERT INTO owner (owner_name, mobile, email, address) " +
                    "VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, new String[]{"owner_id"});
            stmt.setString(1, ownerName);
            stmt.setString(2, mobile);
            stmt.setString(3, email);
            stmt.setString(4, address);
            
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
}
