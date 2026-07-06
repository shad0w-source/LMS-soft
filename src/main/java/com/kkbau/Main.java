package com.kkbau;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    private static final String URL = "jdbc:postgresql://localhost:5432/lmsdb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {

        try {
            Connection connection
                    = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("connected");


            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                }

                LibraryDashboard dashboard = new LibraryDashboard(connection);

                dashboard.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        try {
                            connection.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                dashboard.setVisible(true);
            });

        } catch (SQLException e) {
            System.err.println("Connection failure error: " + e.getMessage());
        }
    }
}
