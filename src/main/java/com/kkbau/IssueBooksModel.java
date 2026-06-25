package com.kkbau;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class IssueBooksModel {
    private Connection conn;

    public IssueBooksModel(Connection conn) throws SQLException {
        this.conn = conn;
        String sql = "CREATE TABLE IF NOT EXISTS issued_books ("
                + "id SERIAL PRIMARY KEY,"
                + "book_id INTEGER NOT NULL,"
                + "member_id INTEGER NOT NULL,"
                + "issue_date DATE NOT NULL DEFAULT CURRENT_DATE,"
                + "FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,"
                + "FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE"
                + ")";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    private boolean idExists(String table, int id) throws SQLException {
        String sql = "SELECT 1 FROM " + table + " WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean issueBook(String memberIdText, String bookIdText) {
        try {
            int memberId = Integer.parseInt(memberIdText);
            int bookId = Integer.parseInt(bookIdText);

            if (!idExists("books", bookId) || !idExists("members", memberId)) return false;

            String insertSql = "INSERT INTO issued_books (book_id, member_id) VALUES (?, ?)";
            String updateStockSql = "UPDATE books SET stock = stock - 1 WHERE id = ? AND stock > 0";

            conn.setAutoCommit(false);
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql);
                 PreparedStatement psUpdate = conn.prepareStatement(updateStockSql)) {
                
                psInsert.setInt(1, bookId);
                psInsert.setInt(2, memberId);
                int rowsInserted = psInsert.executeUpdate();
                
                psUpdate.setInt(1, bookId);
                int rowsUpdated = psUpdate.executeUpdate();

                if (rowsInserted > 0 && rowsUpdated > 0) {
                    conn.commit();
                    return true;
                }
                conn.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    public boolean returnBook(String memberIdText, String bookIdText) {
        String deleteSql = "DELETE FROM issued_books WHERE id = (SELECT id FROM issued_books WHERE member_id = ? AND book_id = ? LIMIT 1)";
        String updateStockSql = "UPDATE books SET stock = stock + 1 WHERE id = ?";

        try {
            int memberId = Integer.parseInt(memberIdText);
            int bookId = Integer.parseInt(bookIdText);

            conn.setAutoCommit(false);
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql);
                 PreparedStatement psUpdate = conn.prepareStatement(updateStockSql)) {
                
                psDelete.setInt(1, memberId);
                psDelete.setInt(2, bookId);
                int rowsDeleted = psDelete.executeUpdate();

                if (rowsDeleted > 0) {
                    psUpdate.setInt(1, bookId);
                    psUpdate.executeUpdate();
                    conn.commit();
                    return true;
                }
                conn.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    public ArrayList<Object[]> findAllIssuedRecordsForTable() {
        ArrayList<Object[]> records = new ArrayList<>();
        String sql = "SELECT m.name, m.id, b.title, b.id, i.issue_date FROM issued_books i " +
                     "JOIN members m ON i.member_id = m.id JOIN books b ON i.book_id = b.id ORDER BY i.id DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                records.add(new Object[]{rs.getString(1) + " (ID #" + rs.getInt(2) + ")", 
                                        rs.getString(3) + " (ID #" + rs.getInt(4) + ")", rs.getString(5)});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return records;
    }
}