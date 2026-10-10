package com.vehical.dao;

import com.vehical.DatabaseConfig;
import com.vehical.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password, full_name, email, phone, role, " +
                    "department, center_name, agency_name, license_number, is_active " +
                    "FROM users WHERE username = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setDepartment(rs.getString("department"));
                user.setCenterName(rs.getString("center_name"));
                user.setAgencyName(rs.getString("agency_name"));
                user.setLicenseNumber(rs.getString("license_number"));
                user.setIsActive(rs.getInt("is_active") == 1);
                return user;
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public User findById(Long id) throws SQLException {
        String sql = "SELECT id, username, password, full_name, email, phone, role, " +
                    "department, center_name, agency_name, license_number, is_active " +
                    "FROM users WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setDepartment(rs.getString("department"));
                user.setCenterName(rs.getString("center_name"));
                user.setAgencyName(rs.getString("agency_name"));
                user.setLicenseNumber(rs.getString("license_number"));
                user.setIsActive(rs.getInt("is_active") == 1);
                return user;
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
}
