package com.kkbau;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookModel {
    private Connection conn;

    public BookModel(Connection conn) throws SQLException {
         this.conn = conn;

         String sql = "CREATE TABLE IF NOT EXISTS books ("
                + "id SERIAL PRIMARY KEY,"
                + "title VARCHAR(255) NOT NULL,"
                + "author VARCHAR(255) NOT NULL,"
                + "stock INTEGER NOT NULL DEFAULT 0,"
                + "added_date DATE DEFAULT CURRENT_DATE"
                + ")";
         this.conn.prepareStatement(sql).executeUpdate();
    }

    public void insertBook(String title, String author, int stock) throws SQLException {
        String sql = "INSERT INTO books (title, author, stock) VALUES (?, ?, ?)";
        PreparedStatement psmt = this.conn.prepareStatement(sql);
        psmt.setString(1, title);
        psmt.setString(2, author);
        psmt.setInt(3, stock);
        psmt.executeUpdate();
    }


    public void updateBook(int id, String title, String author) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ? WHERE id = ?";
        try (PreparedStatement psmt = this.conn.prepareStatement(sql)) {
            psmt.setString(1, title);
            psmt.setString(2, author);
            psmt.setInt(3, id);
            psmt.executeUpdate();
        }
    }

    public void deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement psmt = this.conn.prepareStatement(sql)) {
            psmt.setInt(1, id);
            psmt.executeUpdate();
        }
    }

    public ArrayList<Book> findAllBooks() {
        ArrayList<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, stock, added_date FROM books ORDER BY id ASC";
        try (PreparedStatement psmt = this.conn.prepareStatement(sql);
             var rs = psmt.executeQuery()) {
            while (rs.next()) {
                Book b = new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("stock"),
                    rs.getString("added_date") 
                );
                books.add(b);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching books: " + e.getMessage());
        }
        return books;
    }

    boolean saveNewBook(String title, String author, int stock) {
        try {
            insertBook(title, author, stock);
            return true; 
        } catch (SQLException e) {
            System.err.println("Database save error: " + e.getMessage());
            return false; 
        }
    }
}