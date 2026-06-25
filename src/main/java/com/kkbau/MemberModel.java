package com.kkbau;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MemberModel {
    private Connection conn;

    public MemberModel(Connection conn) {
        this.conn = conn;
    }

    public ArrayList<Member> findAllMembers() {
        ArrayList<Member> list = new ArrayList<>();
        String sql = "SELECT id, name, email FROM members ORDER BY id ASC"; 
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Member(
                    rs.getInt("id"), 
                    rs.getString("name"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insertMember(String name, String email) throws SQLException {
        String sql = "INSERT INTO members (name, email) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.executeUpdate();
        }
    }

    public void deleteMember(int id) throws SQLException {
        String sql = "DELETE FROM members WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void updateMember(int id, String name, String email) throws SQLException {
        String sql = "UPDATE members SET name = ?, email = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, id); // Ensure ID is the 3rd parameter
            ps.executeUpdate();
        }
    }
}